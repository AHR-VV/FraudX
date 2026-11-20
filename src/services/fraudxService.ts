import type {
  AuditEntry,
  DbUpdateFrequency,
  InterceptionChannel,
  InterceptionEvent,
  ManualRule,
  ModuleSnapshot,
  ProtectionModule,
  ProtectionState,
  RuleAction,
  RuleCategory,
  ScanVerdict,
} from '../types/fraudx';

/**
 * Frontend service contract mirroring `FraudXRepository.kt`.
 * Backend agents can swap `MockFraudXService` for a REST/WebSocket client matching `BACKEND_HANDOFF.md`.
 */
export interface FraudXService {
  getInitialState(): Promise<{
    protection: ProtectionState;
    rules: ManualRule[];
    interceptions: InterceptionEvent[];
    auditLog: AuditEntry[];
  }>;
  toggleMasterShield(current: ProtectionState): Promise<{
    protection: ProtectionState;
    auditEntry: AuditEntry;
  }>;
  toggleModule(current: ProtectionState, module: ProtectionModule): Promise<ProtectionState>;
  updateDbFrequency(current: ProtectionState, frequency: DbUpdateFrequency): Promise<ProtectionState>;
  syncThreatDatabase(current: ProtectionState): Promise<ProtectionState>;
  setInstantPushAlerts(current: ProtectionState, enabled: boolean): Promise<ProtectionState>;
  setVoiceAssistance(current: ProtectionState, enabled: boolean): Promise<ProtectionState>;
  inspectPayload(rawPayload: string): Promise<ScanVerdict>;
  createInterceptionFromVerdict(verdict: ScanVerdict): Promise<InterceptionEvent>;
  createManualRule(params: {
    target: string;
    action: RuleAction;
    category: RuleCategory;
    description?: string;
    tag?: string;
  }): Promise<{ rule: ManualRule; auditEntry: AuditEntry }>;
  flipRuleAction(rule: ManualRule): Promise<{ rule: ManualRule; auditEntry: AuditEntry }>;
  createDeleteAudit(rule: ManualRule): AuditEntry;
}

const makeId = (prefix: string) => `${prefix}-${Math.random().toString(36).slice(2, 9)}`;

export const countArmedModules = (modules: ModuleSnapshot): number =>
  Number(modules.website) + Number(modules.sms) + Number(modules.call);

export const INITIAL_PROTECTION: ProtectionState = {
  modules: {
    website: true,
    sms: true,
    call: true,
  },
  lastArmedSnapshot: {
    website: true,
    sms: true,
    call: true,
  },
  websiteBlockedThisWeek: 1248,
  dbUpdateFrequency: 'DAILY',
  lastDbSyncLabel: 'Today at 04:15 AM (via WiFi)',
  instantPushAlerts: true,
  voiceAssistance: true,
  engineVersion: 'v4.19 SEC-NET',
  buildStamp: 'v4.8.2-SECURE',
  policyStamp: 'ESTAB-2025.04',
};

export const INITIAL_RULES: ManualRule[] = [
  {
    id: 'rule-1',
    target: 'chase.com',
    action: 'allowed',
    badgeLabel: 'ALLOW',
    category: 'website',
    description: 'Always Allow (Bypasses AI heuristic engine)',
    tag: 'TAG: TRUSTED FINANCE',
    createdAtLabel: 'ADDED 3D AGO',
  },
  {
    id: 'rule-2',
    target: '+1 (800) 555-0199',
    action: 'allowed',
    badgeLabel: 'ALLOW',
    category: 'phone',
    description: 'Verified Bank Hotline • Bypass Robocall Drop',
    tag: 'CALLER-ID BYPASS',
    createdAtLabel: 'ADDED 1W AGO',
  },
  {
    id: 'rule-3',
    target: 'account-security-update-notice.xyz',
    action: 'blocked',
    badgeLabel: 'HARD BLOCK',
    category: 'website',
    description: 'Phishing Decoy • Smishing Intercept',
    tag: 'TRIGGER: SMS EXPLOIT',
    createdAtLabel: 'ADDED 4H AGO',
  },
  {
    id: 'rule-4',
    target: '+1 (888) 492-9102',
    action: 'blocked',
    badgeLabel: 'BLOCK',
    category: 'phone',
    description: 'Scam Call • Auto-silent & Drop Protocol',
    tag: 'AUTO-DROP WITHOUT RING',
    createdAtLabel: 'ADDED YESTERDAY',
  },
  {
    id: 'rule-5',
    target: '"URGENT: WIRE TRANSFER"',
    action: 'blocked',
    badgeLabel: 'BLOCK PATTERN',
    category: 'sms',
    description: 'SMS Keyword Rule • Instant Quarantine',
    tag: 'REGEX MATCH',
    createdAtLabel: 'ADDED 5D AGO',
  },
];

export const INITIAL_INTERCEPTIONS: InterceptionEvent[] = [
  {
    id: 'int-1',
    target: 'secure-banking-login.xyz',
    summary: 'SMS credential phishing decoy',
    status: 'blocked',
    channel: 'website',
    relativeTime: '12m ago',
    riskScore: 96,
    sourceDetail: 'Intercepted link tap from SMS thread +1 (415) 890-3321',
    heuristicSignals: [
      'High-risk newly registered .xyz TLD (< 48h)',
      'Brand impersonation credential form signature',
      'Missing organization EV/OV TLS identity',
    ],
  },
  {
    id: 'int-2',
    target: 'FedEx Delivery Tracking #91',
    summary: 'Genuine postal webhook',
    status: 'cleared',
    channel: 'sms',
    relativeTime: '1h ago',
    riskScore: 6,
    sourceDetail: 'Verified carrier short code 48773',
    heuristicSignals: [
      'DKIM / carrier short-code sender verified',
      'Destination resolves cleanly to fedex.com',
    ],
  },
  {
    id: 'int-3',
    target: '+1 (888) 492-9102',
    summary: 'Spoofed IRS tax collection robocall',
    status: 'blocked',
    channel: 'phone',
    relativeTime: '3h ago',
    riskScore: 91,
    sourceDetail: 'STIR/SHAKEN attestation failed (Level C)',
    heuristicSignals: [
      'VoIP spoofed geographic footprint mismatch',
      'Reported by 14,200+ honeynet sensors in 24h',
    ],
  },
  {
    id: 'int-4',
    target: 'pay-toll-overdue-ezpass.top',
    summary: 'Unpaid highway toll smishing bait',
    status: 'blocked',
    channel: 'sms',
    relativeTime: 'Yesterday',
    riskScore: 94,
    sourceDetail: 'Quarantined before lock-screen notification',
    heuristicSignals: [
      'Urgency coercion wording matched NLP filter',
      'Payment card skimmer redirect chain detected',
    ],
  },
  {
    id: 'int-5',
    target: 'chase.com/verify-alert',
    summary: 'Trusted banking domain allowlist match',
    status: 'cleared',
    channel: 'website',
    relativeTime: '2d ago',
    riskScore: 2,
    sourceDetail: 'Matched manual Allow override (chase.com)',
    heuristicSignals: [
      'Manual whitelist precedence applied',
      'Valid DigiCert EV TLS certificate chain',
    ],
  },
];

export const INITIAL_AUDIT_LOG: AuditEntry[] = [
  {
    id: 'aud-1',
    actionLabel: 'RULE ADDED',
    target: 'account-security-update-notice.xyz',
    detail: 'Hard Block • Website / URL',
    timestampLabel: '4h ago',
  },
  {
    id: 'aud-2',
    actionLabel: 'RULE ADDED',
    target: '+1 (888) 492-9102',
    detail: 'Block • Phone / Caller ID',
    timestampLabel: 'Yesterday',
  },
  {
    id: 'aud-3',
    actionLabel: 'RULE ADDED',
    target: 'chase.com',
    detail: 'Allow • Trusted Finance',
    timestampLabel: '3d ago',
  },
];

class MockFraudXService implements FraudXService {
  async getInitialState() {
    return {
      protection: INITIAL_PROTECTION,
      rules: INITIAL_RULES,
      interceptions: INITIAL_INTERCEPTIONS,
      auditLog: INITIAL_AUDIT_LOG,
    };
  }

  async toggleMasterShield(current: ProtectionState) {
    const isCurrentlyActive = countArmedModules(current.modules) > 0;
    let next: ProtectionState;
    if (isCurrentlyActive) {
      next = {
        ...current,
        lastArmedSnapshot: { ...current.modules },
        modules: { website: false, sms: false, call: false },
      };
    } else {
      const snapshotHasAny = countArmedModules(current.lastArmedSnapshot) > 0;
      next = {
        ...current,
        modules: snapshotHasAny
          ? { ...current.lastArmedSnapshot }
          : { website: true, sms: true, call: true },
      };
    }

    const armedCount = countArmedModules(next.modules);
    return {
      protection: next,
      auditEntry: {
        id: makeId('aud'),
        actionLabel: armedCount > 0 ? 'SHIELD ARMED' : 'SHIELD PAUSED',
        target: 'Master Perimeter',
        detail: `${armedCount} of 3 modules armed`,
        timestampLabel: 'Just now',
      },
    };
  }

  async toggleModule(current: ProtectionState, module: ProtectionModule) {
    const nextModules: ModuleSnapshot = {
      ...current.modules,
      [module]: !current.modules[module],
    };
    const nextSnapshot =
      countArmedModules(nextModules) > 0 ? { ...nextModules } : current.lastArmedSnapshot;

    return {
      ...current,
      modules: nextModules,
      lastArmedSnapshot: nextSnapshot,
    };
  }

  async updateDbFrequency(current: ProtectionState, frequency: DbUpdateFrequency) {
    return {
      ...current,
      dbUpdateFrequency: frequency,
    };
  }

  async syncThreatDatabase(current: ProtectionState) {
    await new Promise((resolve) => setTimeout(resolve, 650));
    return {
      ...current,
      lastDbSyncLabel: 'Just now (via encrypted sync)',
    };
  }

  async setInstantPushAlerts(current: ProtectionState, enabled: boolean) {
    return {
      ...current,
      instantPushAlerts: enabled,
    };
  }

  async setVoiceAssistance(current: ProtectionState, enabled: boolean) {
    return {
      ...current,
      voiceAssistance: enabled,
    };
  }

  async inspectPayload(rawPayload: string): Promise<ScanVerdict> {
    await new Promise((resolve) => setTimeout(resolve, 850));
    const trimmed = rawPayload.trim();
    const lower = trimmed.toLowerCase();

    const digitsOnly = trimmed.replace(/[\s()+-]/g, '');
    let detectedType: RuleCategory = 'sms';
    if (
      lower.startsWith('http') ||
      lower.includes('.com') ||
      lower.includes('.xyz') ||
      lower.includes('.top') ||
      lower.includes('.net') ||
      lower.includes('.org')
    ) {
      detectedType = 'website';
    } else if (/^\d{7,}$/.test(digitsOnly)) {
      detectedType = 'phone';
    }

    const highRiskMarkers = [
      '.xyz',
      '.top',
      '.ru',
      'login',
      'verify-account',
      'wire transfer',
      'urgent',
      'suspended',
      'claim prize',
      'gift card',
      'otp',
      'ezpass',
      '492-9102',
    ];
    const mediumRiskMarkers = [
      'bit.ly',
      'tinyurl',
      'click here',
      'update',
      'confirm',
      'refund',
      'delivery fee',
    ];
    const trustedMarkers = ['chase.com', 'fedex.com', 'google.com', 'apple.com', '555-0199'];

    const matchedHigh = highRiskMarkers.filter((m) => lower.includes(m));
    const matchedMedium = mediumRiskMarkers.filter((m) => lower.includes(m));
    const isTrusted = trustedMarkers.some((m) => lower.includes(m)) && matchedHigh.length === 0;

    if (isTrusted) {
      return {
        payload: trimmed,
        detectedType,
        verdict: 'safe',
        riskScore: 4,
        headline: 'Verified safe — no threat vectors found',
        explanation:
          'Target matched trusted domain/sender reputation with clean TLS and zero deceptive redirects.',
        signals: [
          'Clean domain & carrier reputation ledger',
          'Zero credential harvesting patterns detected',
          'No obfuscated redirect hops',
        ],
      };
    }

    if (matchedHigh.length > 0) {
      return {
        payload: trimmed,
        detectedType,
        verdict: 'malicious',
        riskScore: Math.min(99, 86 + matchedHigh.length * 4),
        headline: 'High-risk phishing or fraud vector detected',
        explanation:
          'Payload exhibits credential harvesting or social-engineering coercion signatures. Do not open or reply.',
        signals: [
          `Matched high-risk fraud indicators: ${matchedHigh.join(', ')}`,
          'Elevated impersonation / smishing heuristic score',
          'Recommended action: Add immediately to Blocklist',
        ],
      };
    }

    if (matchedMedium.length > 0) {
      return {
        payload: trimmed,
        detectedType,
        verdict: 'suspicious',
        riskScore: 62,
        headline: 'Suspicious payload — proceed with caution',
        explanation:
          'Contains shortened link or unverified urgency cues commonly used in social engineering campaigns.',
        signals: [
          `Matched caution triggers: ${matchedMedium.join(', ')}`,
          'Sender identity could not be cryptographically verified',
        ],
      };
    }

    return {
      payload: trimmed,
      detectedType,
      verdict: 'safe',
      riskScore: 11,
      headline: 'Target isolated — no malicious indicators',
      explanation:
        'Real-time heuristic sweep found no known phishing domains, malware scripts, or smishing signatures.',
      signals: [
        'Zero matches in FraudX threat intelligence feed',
        'No deceptive URL obfuscation or spoofed headers',
      ],
    };
  }

  async createInterceptionFromVerdict(verdict: ScanVerdict): Promise<InterceptionEvent> {
    const channel: InterceptionChannel =
      verdict.detectedType === 'website'
        ? 'website'
        : verdict.detectedType === 'phone'
          ? 'phone'
          : 'sms';

    return {
      id: makeId('int'),
      target: verdict.payload,
      summary: verdict.headline,
      status: verdict.verdict === 'safe' ? 'cleared' : 'blocked',
      channel,
      relativeTime: 'Just now',
      riskScore: verdict.riskScore,
      sourceDetail: 'Manual deep-scan via Link & Message Inspector',
      heuristicSignals: verdict.signals,
    };
  }

  async createManualRule(params: {
    target: string;
    action: RuleAction;
    category: RuleCategory;
    description?: string;
    tag?: string;
  }) {
    const cleaned = params.target.trim();
    const badgeLabel =
      params.action === 'allowed'
        ? 'ALLOW'
        : params.category === 'sms'
          ? 'BLOCK PATTERN'
          : params.category === 'phone'
            ? 'BLOCK'
            : 'HARD BLOCK';

    const categoryLabel =
      params.category === 'website'
        ? 'WEB / URL'
        : params.category === 'phone'
          ? 'PHONE / CALLER ID'
          : 'SMS KEYWORD';

    const rule: ManualRule = {
      id: makeId('rule'),
      target: cleaned,
      action: params.action,
      badgeLabel,
      category: params.category,
      description:
        params.description ??
        (params.action === 'allowed'
          ? 'User Custom Allow Rule • Bypasses AI heuristic engine'
          : 'User Custom Deterministic Block Rule'),
      tag: params.tag ?? 'MANUAL OVERRIDE',
      createdAtLabel: 'JUST NOW',
    };

    const auditEntry: AuditEntry = {
      id: makeId('aud'),
      actionLabel: params.action === 'allowed' ? 'ALLOW ADDED' : 'BLOCK ADDED',
      target: cleaned,
      detail: `${badgeLabel} • ${categoryLabel}`,
      timestampLabel: 'Just now',
    };

    return { rule, auditEntry };
  }

  async flipRuleAction(rule: ManualRule) {
    const nextAction: RuleAction = rule.action === 'allowed' ? 'blocked' : 'allowed';
    const nextBadge = nextAction === 'allowed' ? 'ALLOW' : 'HARD BLOCK';
    const updated: ManualRule = {
      ...rule,
      action: nextAction,
      badgeLabel: nextBadge,
      description:
        nextAction === 'allowed'
          ? 'Always Allow (Bypasses AI heuristic engine)'
          : 'Hard Block • Manual Quarantine Override',
    };
    const auditEntry: AuditEntry = {
      id: makeId('aud'),
      actionLabel: 'RULE MOVED',
      target: updated.target,
      detail: `Switched to ${nextBadge}`,
      timestampLabel: 'Just now',
    };
    return { rule: updated, auditEntry };
  }

  createDeleteAudit(rule: ManualRule): AuditEntry {
    return {
      id: makeId('aud'),
      actionLabel: 'RULE DELETED',
      target: rule.target,
      detail: `Removed from ${rule.action} perimeter`,
      timestampLabel: 'Just now',
    };
  }
}

export const fraudxService: FraudXService = new MockFraudXService();
