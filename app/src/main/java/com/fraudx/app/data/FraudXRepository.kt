package com.fraudx.app.data

import com.fraudx.app.model.AuditEntry
import com.fraudx.app.model.DbUpdateFrequency
import com.fraudx.app.model.InterceptionEvent
import com.fraudx.app.model.ManualRule
import com.fraudx.app.model.ProtectionModule
import com.fraudx.app.model.ProtectionState
import com.fraudx.app.model.RuleAction
import com.fraudx.app.model.RuleCategory
import com.fraudx.app.model.ScanVerdict
import kotlinx.coroutines.flow.Flow

/**
 * Primary backend integration contract for FraudX.
 *
 * Downstream backend agents should implement this interface using Retrofit/Ktor + Room
 * and swap [MockFraudXRepository] inside dependency injection or [com.fraudx.app.viewmodel.FraudXViewModel].
 * See `BACKEND_HANDOFF.md` at the repository root for exact REST endpoints and payload schemas.
 */
interface FraudXRepository {

    /** Stream of master perimeter state, 3 intercept modules, and secondary preferences. */
    fun observeProtectionState(): Flow<ProtectionState>

    /** Stream of deterministic Allow/Block manual override rules. */
    fun observeRules(): Flow<List<ManualRule>>

    /** Stream of real-time and historical threat interceptions. */
    fun observeInterceptions(): Flow<List<InterceptionEvent>>

    /** Stream of local audit entries for rule and shield state mutations. */
    fun observeAuditLog(): Flow<List<AuditEntry>>

    /**
     * Toggles the master Dashboard ON/OFF shield.
     * Disabling disarms all 3 modules while preserving `lastArmedSnapshot`.
     * Enabling restores `lastArmedSnapshot` (or arms all 3 if snapshot had 0 armed).
     */
    suspend fun toggleMasterShield(): ProtectionState

    /**
     * Toggles a single intercept module (Website, SMS, or Call).
     * Disabling the final active module automatically flips master protection OFF.
     */
    suspend fun toggleModule(module: ProtectionModule): ProtectionState

    /** Updates automated threat database refresh cadence (Daily / Weekly / Monthly). */
    suspend fun updateDbFrequency(frequency: DbUpdateFrequency): ProtectionState

    /** Triggers an on-demand threat intelligence database sync. */
    suspend fun syncThreatDatabase(): ProtectionState

    /** Toggles high-priority breach push notifications. */
    suspend fun setInstantPushAlerts(enabled: Boolean): ProtectionState

    /** Toggles real-time audio alerts and voice guidance for incoming threat warnings. */
    suspend fun setVoiceAssistance(enabled: Boolean): ProtectionState

    /**
     * Evaluates an unknown URL, SMS snippet, or phone number and returns a structured
     * heuristic verdict (SAFE, SUSPICIOUS, or MALICIOUS).
     */
    suspend fun inspectPayload(rawPayload: String): ScanVerdict

    /** Records a completed scan verdict directly into the interception log. */
    suspend fun recordVerdictToLog(verdict: ScanVerdict): InterceptionEvent

    /** Inserts a new manual Allow or Block override rule. */
    suspend fun addManualRule(
        target: String,
        action: RuleAction,
        category: RuleCategory,
        description: String? = null,
        tag: String? = null
    ): ManualRule

    /** Moves an existing rule between ALLOWED and BLOCKED lists. */
    suspend fun toggleRuleAction(ruleId: String): ManualRule?

    /** Removes a manual override rule by ID. */
    suspend fun deleteRule(ruleId: String)
}
