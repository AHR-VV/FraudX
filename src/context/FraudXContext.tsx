import React, { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react';
import {
  countArmedModules,
  fraudxService,
  INITIAL_AUDIT_LOG,
  INITIAL_INTERCEPTIONS,
  INITIAL_PROTECTION,
  INITIAL_RULES,
} from '../services/fraudxService';
import type {
  ActiveScreen,
  AuditEntry,
  DbUpdateFrequency,
  InterceptionEvent,
  ManualRule,
  ProtectionModule,
  ProtectionState,
  RuleAction,
  RuleCategory,
  ScanVerdict,
} from '../types/fraudx';

interface FraudXContextValue {
  activeScreen: ActiveScreen;
  navigate: (screen: ActiveScreen) => void;
  protection: ProtectionState;
  isMasterEnabled: boolean;
  armedModuleCount: number;
  rules: ManualRule[];
  allowedCount: number;
  blockedCount: number;
  interceptions: InterceptionEvent[];
  auditLog: AuditEntry[];
  isSyncingDb: boolean;
  isScanningPayload: boolean;
  lastScanVerdict: ScanVerdict | null;
  inspectorOpen: boolean;
  auditSheetOpen: boolean;
  selectedInterceptionId: string | null;
  toastMessage: string | null;

  openInspector: () => void;
  closeInspector: () => void;
  openAuditSheet: () => void;
  closeAuditSheet: () => void;
  openFullLog: (interceptionId?: string | null) => void;

  toggleMasterShield: () => Promise<void>;
  toggleModule: (module: ProtectionModule) => Promise<void>;
  updateDbFrequency: (frequency: DbUpdateFrequency) => Promise<void>;
  syncThreatDatabase: () => Promise<void>;
  setInstantPushAlerts: (enabled: boolean) => Promise<void>;
  setVoiceAssistance: (enabled: boolean) => Promise<void>;
  inspectPayload: (rawPayload: string) => Promise<void>;
  escalateVerdictToRule: (verdict: ScanVerdict, action: RuleAction) => Promise<void>;
  saveVerdictToLog: (verdict: ScanVerdict) => Promise<void>;
  addManualRule: (target: string, action: RuleAction, category: RuleCategory) => Promise<void>;
  escalateInterceptionToRule: (event: InterceptionEvent, action: RuleAction) => Promise<void>;
  flipRuleAction: (ruleId: string) => Promise<void>;
  deleteRule: (ruleId: string) => void;
  showToast: (message: string) => void;
  dismissToast: () => void;
}

const FraudXContext = createContext<FraudXContextValue | null>(null);

export const FraudXProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [activeScreen, setActiveScreen] = useState<ActiveScreen>('dashboard');
  const [protection, setProtection] = useState<ProtectionState>(INITIAL_PROTECTION);
  const [rules, setRules] = useState<ManualRule[]>(INITIAL_RULES);
  const [interceptions, setInterceptions] = useState<InterceptionEvent[]>(INITIAL_INTERCEPTIONS);
  const [auditLog, setAuditLog] = useState<AuditEntry[]>(INITIAL_AUDIT_LOG);

  const [isSyncingDb, setIsSyncingDb] = useState(false);
  const [isScanningPayload, setIsScanningPayload] = useState(false);
  const [lastScanVerdict, setLastScanVerdict] = useState<ScanVerdict | null>(null);
  const [inspectorOpen, setInspectorOpen] = useState(false);
  const [auditSheetOpen, setAuditSheetOpen] = useState(false);
  const [selectedInterceptionId, setSelectedInterceptionId] = useState<string | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const toastTimerRef = useRef<number | null>(null);

  const showToast = useCallback((message: string) => {
    setToastMessage(message);
    if (toastTimerRef.current !== null) {
      window.clearTimeout(toastTimerRef.current);
    }
    toastTimerRef.current = window.setTimeout(() => {
      setToastMessage(null);
    }, 3000);
  }, []);

  const dismissToast = useCallback(() => {
    if (toastTimerRef.current !== null) {
      window.clearTimeout(toastTimerRef.current);
    }
    setToastMessage(null);
  }, []);

  const navigate = useCallback((screen: ActiveScreen) => {
    setActiveScreen(screen);
  }, []);

  const openInspector = useCallback(() => {
    setLastScanVerdict(null);
    setInspectorOpen(true);
  }, []);

  const closeInspector = useCallback(() => {
    setInspectorOpen(false);
    setLastScanVerdict(null);
  }, []);

  const openAuditSheet = useCallback(() => setAuditSheetOpen(true), []);
  const closeAuditSheet = useCallback(() => setAuditSheetOpen(false), []);

  const openFullLog = useCallback((interceptionId?: string | null) => {
    setSelectedInterceptionId(interceptionId ?? null);
    setActiveScreen('interception_log');
  }, []);

  const toggleMasterShield = useCallback(async () => {
    const { protection: next, auditEntry } = await fraudxService.toggleMasterShield(protection);
    setProtection(next);
    setAuditLog((prev) => [auditEntry, ...prev]);
    const armed = countArmedModules(next.modules);
    showToast(
      armed > 0
        ? `Real-time protection enabled (${armed}/3 modules armed)`
        : 'Protection paused — device perimeter inactive'
    );
  }, [protection, showToast]);

  const toggleModule = useCallback(
    async (module: ProtectionModule) => {
      const next = await fraudxService.toggleModule(protection, module);
      setProtection(next);
      if (countArmedModules(next.modules) === 0) {
        showToast('All modules disabled — Dashboard protection set to OFF');
      }
    },
    [protection, showToast]
  );

  const updateDbFrequency = useCallback(
    async (frequency: DbUpdateFrequency) => {
      const next = await fraudxService.updateDbFrequency(protection, frequency);
      setProtection(next);
      showToast(`Threat database updates set to ${frequency.toLowerCase()}`);
    },
    [protection, showToast]
  );

  const syncThreatDatabase = useCallback(async () => {
    if (isSyncingDb) return;
    setIsSyncingDb(true);
    try {
      const next = await fraudxService.syncThreatDatabase(protection);
      setProtection(next);
      showToast('Threat definitions synchronized');
    } finally {
      setIsSyncingDb(false);
    }
  }, [isSyncingDb, protection, showToast]);

  const setInstantPushAlerts = useCallback(
    async (enabled: boolean) => {
      const next = await fraudxService.setInstantPushAlerts(protection, enabled);
      setProtection(next);
    },
    [protection]
  );

  const setVoiceAssistance = useCallback(
    async (enabled: boolean) => {
      const next = await fraudxService.setVoiceAssistance(protection, enabled);
      setProtection(next);
    },
    [protection]
  );

  const inspectPayload = useCallback(
    async (rawPayload: string) => {
      if (!rawPayload.trim() || isScanningPayload) return;
      setIsScanningPayload(true);
      setLastScanVerdict(null);
      try {
        const verdict = await fraudxService.inspectPayload(rawPayload);
        setLastScanVerdict(verdict);
      } finally {
        setIsScanningPayload(false);
      }
    },
    [isScanningPayload]
  );

  const escalateVerdictToRule = useCallback(
    async (verdict: ScanVerdict, action: RuleAction) => {
      const { rule, auditEntry } = await fraudxService.createManualRule({
        target: verdict.payload,
        action,
        category: verdict.detectedType,
        description: verdict.headline,
        tag: 'INSPECTOR ESCALATION',
      });
      const event = await fraudxService.createInterceptionFromVerdict(verdict);
      setRules((prev) => [
        rule,
        ...prev.filter((r) => r.target.toLowerCase() !== rule.target.toLowerCase()),
      ]);
      setInterceptions((prev) => [event, ...prev]);
      setAuditLog((prev) => [auditEntry, ...prev]);
      setInspectorOpen(false);
      setLastScanVerdict(null);
      showToast(
        `Saved "${rule.target}" to ${action === 'allowed' ? 'Allowed list' : 'Blocked list'}`
      );
    },
    [showToast]
  );

  const saveVerdictToLog = useCallback(
    async (verdict: ScanVerdict) => {
      const event = await fraudxService.createInterceptionFromVerdict(verdict);
      setInterceptions((prev) => [event, ...prev]);
      setInspectorOpen(false);
      setLastScanVerdict(null);
      showToast('Recorded scan result in Interception Log');
    },
    [showToast]
  );

  const addManualRule = useCallback(
    async (target: string, action: RuleAction, category: RuleCategory) => {
      if (!target.trim()) return;
      const { rule, auditEntry } = await fraudxService.createManualRule({
        target,
        action,
        category,
      });
      setRules((prev) => [
        rule,
        ...prev.filter((r) => r.target.toLowerCase() !== rule.target.toLowerCase()),
      ]);
      setAuditLog((prev) => [auditEntry, ...prev]);
      showToast(`Added ${rule.target} to ${action === 'allowed' ? 'Whitelist' : 'Blacklist'}`);
    },
    [showToast]
  );

  const escalateInterceptionToRule = useCallback(
    async (event: InterceptionEvent, action: RuleAction) => {
      const category: RuleCategory =
        event.channel === 'website' ? 'website' : event.channel === 'phone' ? 'phone' : 'sms';
      const { rule, auditEntry } = await fraudxService.createManualRule({
        target: event.target,
        action,
        category,
        description: event.summary,
        tag: 'LOG ESCALATION',
      });
      setRules((prev) => [
        rule,
        ...prev.filter((r) => r.target.toLowerCase() !== rule.target.toLowerCase()),
      ]);
      setAuditLog((prev) => [auditEntry, ...prev]);
      showToast(`Added ${event.target} to ${action === 'allowed' ? 'Allowlist' : 'Blocklist'}`);
    },
    [showToast]
  );

  const flipRuleAction = useCallback(
    async (ruleId: string) => {
      const existing = rules.find((r) => r.id === ruleId);
      if (!existing) return;
      const { rule: updated, auditEntry } = await fraudxService.flipRuleAction(existing);
      setRules((prev) => prev.map((r) => (r.id === ruleId ? updated : r)));
      setAuditLog((prev) => [auditEntry, ...prev]);
      showToast(`Moved ${updated.target} to ${updated.action === 'allowed' ? 'Allowed' : 'Blocked'}`);
    },
    [rules, showToast]
  );

  const deleteRule = useCallback(
    (ruleId: string) => {
      const target = rules.find((r) => r.id === ruleId);
      setRules((prev) => prev.filter((r) => r.id !== ruleId));
      if (target) {
        setAuditLog((prev) => [fraudxService.createDeleteAudit(target), ...prev]);
      }
      showToast('Manual rule deleted');
    },
    [rules, showToast]
  );

  const armedModuleCount = countArmedModules(protection.modules);
  const isMasterEnabled = armedModuleCount > 0;
  const allowedCount = useMemo(() => rules.filter((r) => r.action === 'allowed').length, [rules]);
  const blockedCount = useMemo(() => rules.filter((r) => r.action === 'blocked').length, [rules]);

  const value = useMemo<FraudXContextValue>(
    () => ({
      activeScreen,
      navigate,
      protection,
      isMasterEnabled,
      armedModuleCount,
      rules,
      allowedCount,
      blockedCount,
      interceptions,
      auditLog,
      isSyncingDb,
      isScanningPayload,
      lastScanVerdict,
      inspectorOpen,
      auditSheetOpen,
      selectedInterceptionId,
      toastMessage,
      openInspector,
      closeInspector,
      openAuditSheet,
      closeAuditSheet,
      openFullLog,
      toggleMasterShield,
      toggleModule,
      updateDbFrequency,
      syncThreatDatabase,
      setInstantPushAlerts,
      setVoiceAssistance,
      inspectPayload,
      escalateVerdictToRule,
      saveVerdictToLog,
      addManualRule,
      escalateInterceptionToRule,
      flipRuleAction,
      deleteRule,
      showToast,
      dismissToast,
    }),
    [
      activeScreen,
      navigate,
      protection,
      isMasterEnabled,
      armedModuleCount,
      rules,
      allowedCount,
      blockedCount,
      interceptions,
      auditLog,
      isSyncingDb,
      isScanningPayload,
      lastScanVerdict,
      inspectorOpen,
      auditSheetOpen,
      selectedInterceptionId,
      toastMessage,
      openInspector,
      closeInspector,
      openAuditSheet,
      closeAuditSheet,
      openFullLog,
      toggleMasterShield,
      toggleModule,
      updateDbFrequency,
      syncThreatDatabase,
      setInstantPushAlerts,
      setVoiceAssistance,
      inspectPayload,
      escalateVerdictToRule,
      saveVerdictToLog,
      addManualRule,
      escalateInterceptionToRule,
      flipRuleAction,
      deleteRule,
      showToast,
      dismissToast,
    ]
  );

  return <FraudXContext.Provider value={value}>{children}</FraudXContext.Provider>;
};

export function useFraudX(): FraudXContextValue {
  const ctx = useContext(FraudXContext);
  if (!ctx) {
    throw new Error('useFraudX must be used inside FraudXProvider');
  }
  return ctx;
}
