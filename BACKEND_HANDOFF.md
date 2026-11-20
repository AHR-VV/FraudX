# FraudX — Backend & Native Android Integration Handoff Guide

> **Audience**: Backend engineers, Android systems engineers, and autonomous coding agents tasked with connecting the FraudX UI to live fraud-detection APIs, persistent databases, and Android OS interception services.
>
> **Golden Rule**: You should **not** need to modify any Jetpack Compose screen (`app/src/main/java/com/fraudx/app/ui/**`) or React preview screen (`src/screens/**`). All business logic, API calls, and storage are strictly isolated behind **`FraudXRepository`** (Kotlin) and **`FraudXService`** (TypeScript web preview).

---

## 1. Repository & Project Architecture

This repository ships as a **Hybrid Native Android (Kotlin + Jetpack Compose) + Synced Web Preview** workspace:

| Layer | Native Android (Kotlin / Compose) | Interactive Web Preview (TypeScript) |
| :--- | :--- | :--- |
| **Entry Point** | `app/src/main/java/com/fraudx/app/MainActivity.kt` | `src/App.tsx` |
| **Theme & Tokens** | `app/src/main/java/com/fraudx/app/theme/` (`VerdantShieldTheme`) | `src/index.css` (`@theme` Verdant Shield) |
| **Data Schemas** | `app/src/main/java/com/fraudx/app/model/FraudXModels.kt` | `src/types/fraudx.ts` |
| **Service Interface** | `app/src/main/java/com/fraudx/app/data/FraudXRepository.kt` | `src/services/fraudxService.ts` |
| **Mock Seed Adapter** | `app/src/main/java/com/fraudx/app/data/MockFraudXRepository.kt` | `src/services/fraudxService.ts` (`MockFraudXService`) |
| **State Holder (UDF)** | `app/src/main/java/com/fraudx/app/viewmodel/FraudXViewModel.kt` | `src/context/FraudXContext.tsx` |
| **Navigation & Screens** | `app/src/main/java/com/fraudx/app/ui/navigation/FraudXNavHost.kt` | `src/App.tsx` + `src/screens/*` |

### Screen-to-State Navigation Map

```text
DashboardScreen (ON / OFF)
 ├── [Gear Icon] ──────────────▶ SettingsScreen
 │                                 ├── PerimeterOverviewBanner (0–3 armed states)
 │                                 ├── Primary Intercept Modules (Website, SMS, Call)
 │                                 ├── Threat Database Updates (Daily/Weekly/Monthly + Sync)
 │                                 ├── Instant Push Alerts Switch
 │                                 └── [Manage >] ──▶ AllowBlockScreen
 │                                                      ├── Allowed / Blocked Segmented Tabs
 │                                                      ├── Inject New Rule (L3-FILTER)
 │                                                      ├── Search + Category Filter Chips
 │                                                      ├── Rule Cards (Flip / Copy / Delete)
 │                                                      └── [Audit Log] ──▶ AuditLogSheet
 ├── [Master ON/OFF Shield] ───▶ toggleMasterShield() (Synced with 3 Settings modules)
 ├── [Check a link or message] ▶ LinkInspectorSheet (Deep Scan → Verdict → Block/Allow/Log)
 └── [View full log / Row tap] ▶ InterceptionLogScreen (Metrics, Filters, Expandable Signals)
```

---

## 2. Core State Invariants (Must Be Preserved by Backend)

1. **Master Shield ↔ 3-Module Synchronization**:
   - `ProtectionState.isMasterEnabled` is derived from `modules.website || modules.sms || modules.call`.
   - Turning the Dashboard shield **OFF** sets all 3 modules to `false` while preserving the user's previous configuration in `lastArmedSnapshot`.
   - Turning the Dashboard shield **ON** restores `lastArmedSnapshot` (or arms all 3 modules if `lastArmedSnapshot` had 0 enabled).
   - Disabling the last remaining enabled module in Settings automatically causes `isMasterEnabled == false`, flipping the Dashboard to **OFF (Device Vulnerable)**.
   - Enabling any single module in Settings while `OFF` automatically flips the Dashboard to **ON**.
2. **Perimeter Banner Thresholds** (`SettingsScreen`):
   - `3 / 3` armed → **Full Perimeter Defense** (Emerald)
   - `2 / 3` armed → **Partial Perimeter Defense** (Amber)
   - `1 / 3` armed → **Partial Perimeter Defense** (Crimson)
   - `0 / 3` armed → **Perimeter Defense Disabled — 0 of 3 Modules Armed (Vulnerable)** (Crimson)
3. **Deterministic Rule Precedence**:
   - **Manual `ALLOWED` rules supersede all automated heuristics and `BLOCKED` rules** (explicitly surfaced in the bottom Notice banner on `AllowBlockScreen`).
   - Targets should be normalized (trimmed, lowercased domains, E.164 phone matching) and deduplicated per user account so moving or re-adding an existing target updates its action rather than duplicating entries.

---

## 3. Kotlin Data Models & JSON Schemas

All network payloads map directly to `@Serializable` types in `FraudXModels.kt`:

### `ProtectionState`
```json
{
  "modules": {
    "website": true,
    "sms": true,
    "call": true
  },
  "lastArmedSnapshot": {
    "website": true,
    "sms": true,
    "call": true
  },
  "websiteBlockedThisWeek": 1248,
  "dbUpdateFrequency": "DAILY",
  "lastDbSyncLabel": "Today at 04:15 AM (via WiFi)",
  "instantPushAlerts": true,
  "engineVersion": "v4.19 SEC-NET",
  "buildStamp": "v4.8.2-SECURE",
  "policyStamp": "ESTAB-2025.04"
}
```

### `ManualRule`
```json
{
  "id": "rule-1",
  "target": "chase.com",
  "action": "ALLOWED",
  "badgeLabel": "ALLOW",
  "category": "WEBSITE",
  "description": "Always Allow (Bypasses AI heuristic engine)",
  "tag": "TAG: TRUSTED FINANCE",
  "createdAtLabel": "ADDED 3D AGO"
}
```
- `action`: `"ALLOWED" | "BLOCKED"`
- `category`: `"WEBSITE" | "PHONE" | "SMS"`

### `InterceptionEvent`
```json
{
  "id": "int-1",
  "target": "secure-banking-login.xyz",
  "summary": "SMS credential phishing decoy",
  "status": "BLOCKED",
  "channel": "WEBSITE",
  "relativeTime": "12m ago",
  "riskScore": 96,
  "sourceDetail": "Intercepted link tap from SMS thread +1 (415) 890-3321",
  "heuristicSignals": [
    "High-risk newly registered .xyz TLD (< 48h)",
    "Brand impersonation credential form signature"
  ]
}
```
- `status`: `"BLOCKED" | "CLEARED"`
- `channel`: `"WEBSITE" | "SMS" | "PHONE"`

### `ScanVerdict`
```json
{
  "payload": "https://secure-banking-login.xyz/verify-account",
  "detectedType": "WEBSITE",
  "verdict": "MALICIOUS",
  "riskScore": 94,
  "headline": "High-risk phishing or fraud vector detected",
  "explanation": "Payload exhibits credential harvesting signatures.",
  "signals": [
    "Matched high-risk fraud indicators: .xyz, login, verify-account"
  ]
}
```
- `verdict`: `"SAFE" | "SUSPICIOUS" | "MALICIOUS"`

### `AuditEntry`
```json
{
  "id": "aud-1",
  "actionLabel": "RULE ADDED",
  "target": "account-security-update-notice.xyz",
  "detail": "Hard Block • Website / URL",
  "timestampLabel": "4h ago"
}
```

---

## 4. REST API Contract Specification

All endpoints expect `Authorization: Bearer <token>` and `Content-Type: application/json`.

| Method & Endpoint | Repository Hook | Request Body | Response (`200` / `201`) |
| :--- | :--- | :--- | :--- |
| `GET /v1/protection` | `observeProtectionState()` | — | `ProtectionState` |
| `PATCH /v1/protection/master` | `toggleMasterShield()` | `{ "enabled": boolean }` | `ProtectionState` |
| `PATCH /v1/protection/modules` | `toggleModule(module)` | `{ "module": "WEBSITE"\|"SMS"\|"CALL", "enabled": boolean }` | `ProtectionState` |
| `PATCH /v1/protection/preferences` | `updateDbFrequency()` / `setInstantPushAlerts()` | `{ "dbUpdateFrequency"?: string, "instantPushAlerts"?: boolean }` | `ProtectionState` |
| `POST /v1/protection/sync` | `syncThreatDatabase()` | `{}` | `{ "protection": ProtectionState, "signaturesUpdated": number }` |
| `POST /v1/inspect` | `inspectPayload(rawPayload)` | `{ "payload": string }` | `ScanVerdict` |
| `GET /v1/rules` | `observeRules()` | Query: `?action=&category=&q=` | `{ "items": ManualRule[] }` |
| `POST /v1/rules` | `addManualRule(...)` | `{ "target": string, "action": RuleAction, "category": RuleCategory, "description"?: string, "tag"?: string }` | `ManualRule` (`201`) |
| `PATCH /v1/rules/{id}` | `toggleRuleAction(id)` | `{ "action": RuleAction }` | `ManualRule` |
| `DELETE /v1/rules/{id}` | `deleteRule(id)` | — | `204 No Content` |
| `GET /v1/interceptions` | `observeInterceptions()` | Query: `?status=&channel=&limit=50` | `{ "items": InterceptionEvent[] }` |
| `POST /v1/interceptions` | `recordVerdictToLog(verdict)` | `ScanVerdict` | `InterceptionEvent` (`201`) |
| `GET /v1/audit-log` | `observeAuditLog()` | Query: `?limit=100` | `{ "items": AuditEntry[] }` |

### Standard Error Envelope (`4xx` / `5xx`)
```json
{
  "error": {
    "code": "INVALID_TARGET_FORMAT",
    "message": "Provide a valid domain, phone number, or SMS keyword.",
    "retryable": false
  }
}
```

---

## 5. Wiring Production Backend into Kotlin Android

### Step 1: Implement `FraudXRepository`
Create `RemoteFraudXRepository(api: FraudXApi, dao: FraudXDao)` implementing `com.fraudx.app.data.FraudXRepository`:
- Use **Room** (`Flow<List<ManualRuleEntity>>`, `Flow<List<InterceptionEntity>>`) as the offline-first single source of truth so `observeRules()` and `observeInterceptions()` stay reactive even when offline.
- Store `ProtectionState` preferences in **Jetpack DataStore** backed by Android Keystore encryption (`EncryptedSharedPreferences` / Tink) to honor the UI claim *"All telemetry encrypted locally"*.
- Perform optimistic updates on `toggleModule()`, `toggleMasterShield()`, and `addManualRule()`, reconciling with Retrofit/Ktor responses and rolling back on non-retryable `4xx` errors.

### Step 2: Inject into `FraudXViewModel`
Provide a `ViewModelProvider.Factory` (or Hilt `@HiltViewModel`) supplying `RemoteFraudXRepository` into `FraudXViewModel(repository)`:
```kotlin
class FraudXViewModelFactory(
    private val repository: FraudXRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FraudXViewModel(repository) as T
    }
}
```

### Step 3: Native Android Interception Services
Connect each Settings module toggle to its real Android OS runtime hook:
- **Website Protection** (`ProtectionModule.WEBSITE`): Bind a local DNS/TLS-filtering `VpnService` (or Safe Browsing + `AccessibilityService` URL bar observer) that queries local bloom filters + `POST /v1/inspect` and honors `ALLOWED` rules first.
- **SMS Protection** (`ProtectionModule.SMS`): Hook a `NotificationListenerService` or SMS screening service running on-device NLP smishing heuristics + regex rules from `RuleCategory.SMS`.
- **Call Protection** (`ProtectionModule.CALL`): Register an Android `CallScreeningService` (`android.permission.BIND_SCREENING_SERVICE`) checking `ManualRule` allowlist bypasses first, then blocklist/honeynet reputation to return `CallResponse.Builder().setDisallowCall(true).setSilenceCall(true)`.
- **Threat Database Updates** (`dbUpdateFrequency`): Schedule a `PeriodicWorkRequest` via **WorkManager** (`1 day` / `7 days` / `30 days`) with `NetworkType.UNMETERED` (WiFi constraint) calling `syncThreatDatabase()`.
- **Instant Push Alerts** (`instantPushAlerts`): Register a **Firebase Cloud Messaging** (`FirebaseMessagingService`) token and toggle the high-priority `fraudx_breach_alerts` notification channel.

---

## 6. Swapping the Web Preview Adapter (Optional)

If you also want the browser preview to hit your staging REST API:
1. Open `src/services/fraudxService.ts`.
2. Replace `export const fraudxService: FraudXService = new MockFraudXService();` with an HTTP implementation calling the `/v1/*` endpoints above.
3. No React screen or component files require changes.
