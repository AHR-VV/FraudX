export type ProtectionModule = 'website' | 'sms' | 'call';

export type DbUpdateFrequency = 'DAILY' | 'WEEKLY' | 'MONTHLY';

export type RuleAction = 'allowed' | 'blocked';

export type RuleCategory = 'website' | 'phone' | 'sms';

export type InterceptionStatus = 'blocked' | 'cleared';

export type InterceptionChannel = 'website' | 'sms' | 'phone';

export type VerdictLevel = 'safe' | 'suspicious' | 'malicious';

export type ActiveScreen = 'dashboard' | 'settings' | 'allow_block' | 'interception_log';

export interface ModuleSnapshot {
  website: boolean;
  sms: boolean;
  call: boolean;
}

export interface ProtectionState {
  modules: ModuleSnapshot;
  lastArmedSnapshot: ModuleSnapshot;
  websiteBlockedThisWeek: number;
  dbUpdateFrequency: DbUpdateFrequency;
  lastDbSyncLabel: string;
  instantPushAlerts: boolean;
  voiceAssistance: boolean;
  engineVersion: string;
  buildStamp: string;
  policyStamp: string;
}

export interface ManualRule {
  id: string;
  target: string;
  action: RuleAction;
  badgeLabel: string;
  category: RuleCategory;
  description: string;
  tag: string;
  createdAtLabel: string;
}

export interface InterceptionEvent {
  id: string;
  target: string;
  summary: string;
  status: InterceptionStatus;
  channel: InterceptionChannel;
  relativeTime: string;
  riskScore: number;
  sourceDetail: string;
  heuristicSignals: string[];
}

export interface ScanVerdict {
  payload: string;
  detectedType: RuleCategory;
  verdict: VerdictLevel;
  riskScore: number;
  headline: string;
  explanation: string;
  signals: string[];
}

export interface AuditEntry {
  id: string;
  actionLabel: string;
  target: string;
  detail: string;
  timestampLabel: string;
}
