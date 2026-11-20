package com.fraudx.app.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProtectionModule {
    WEBSITE,
    SMS,
    CALL
}

@Serializable
enum class DbUpdateFrequency(val label: String) {
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY")
}

@Serializable
enum class RuleAction {
    ALLOWED,
    BLOCKED
}

@Serializable
enum class RuleCategory(val label: String, val filterChipLabel: String) {
    WEBSITE("WEB / URL", "Websites / URLs"),
    PHONE("PHONE / CALLER ID", "Phone Numbers"),
    SMS("SMS KEYWORD", "SMS Keywords")
}

@Serializable
enum class InterceptionStatus {
    BLOCKED,
    CLEARED
}

@Serializable
enum class InterceptionChannel(val label: String) {
    WEBSITE("Website"),
    SMS("SMS"),
    PHONE("Phone Call")
}

@Serializable
enum class VerdictLevel {
    SAFE,
    SUSPICIOUS,
    MALICIOUS
}

@Serializable
data class ModuleSnapshot(
    val website: Boolean = true,
    val sms: Boolean = true,
    val call: Boolean = true
) {
    val armedCount: Int
        get() = listOf(website, sms, call).count { it }

    val anyArmed: Boolean
        get() = armedCount > 0
}

@Serializable
data class ProtectionState(
    val modules: ModuleSnapshot = ModuleSnapshot(),
    val lastArmedSnapshot: ModuleSnapshot = ModuleSnapshot(),
    val websiteBlockedThisWeek: Int = 1248,
    val dbUpdateFrequency: DbUpdateFrequency = DbUpdateFrequency.DAILY,
    val lastDbSyncLabel: String = "Today at 04:15 AM (via WiFi)",
    val instantPushAlerts: Boolean = true,
    val voiceAssistance: Boolean = true,
    val engineVersion: String = "v4.19 SEC-NET",
    val buildStamp: String = "v4.8.2-SECURE",
    val policyStamp: String = "ESTAB-2025.04"
) {
    /** Synchronized master shield flag: OFF automatically when all 3 modules are disabled. */
    val isMasterEnabled: Boolean
        get() = modules.anyArmed

    val armedModuleCount: Int
        get() = modules.armedCount
}

@Serializable
data class ManualRule(
    val id: String,
    val target: String,
    val action: RuleAction,
    val badgeLabel: String,
    val category: RuleCategory,
    val description: String,
    val tag: String,
    val createdAtLabel: String
)

@Serializable
data class InterceptionEvent(
    val id: String,
    val target: String,
    val summary: String,
    val status: InterceptionStatus,
    val channel: InterceptionChannel,
    val relativeTime: String,
    val riskScore: Int,
    val sourceDetail: String,
    val heuristicSignals: List<String>
)

@Serializable
data class ScanVerdict(
    val payload: String,
    val detectedType: RuleCategory,
    val verdict: VerdictLevel,
    val riskScore: Int,
    val headline: String,
    val explanation: String,
    val signals: List<String>
)

@Serializable
data class AuditEntry(
    val id: String,
    val actionLabel: String,
    val target: String,
    val detail: String,
    val timestampLabel: String
)

/**
 * Unidirectional UI state container observed by all Jetpack Compose screens.
 */
data class FraudXUiState(
    val protection: ProtectionState = ProtectionState(),
    val rules: List<ManualRule> = emptyList(),
    val interceptions: List<InterceptionEvent> = emptyList(),
    val auditLog: List<AuditEntry> = emptyList(),
    val isSyncingDb: Boolean = false,
    val isScanningPayload: Boolean = false,
    val lastScanVerdict: ScanVerdict? = null,
    val selectedInterceptionId: String? = null
) {
    val allowedCount: Int
        get() = rules.count { it.action == RuleAction.ALLOWED }

    val blockedCount: Int
        get() = rules.count { it.action == RuleAction.BLOCKED }

    val trustedDomainsCount: Int
        get() = rules.count { it.action == RuleAction.ALLOWED && it.category == RuleCategory.WEBSITE }

    val bypassNumbersCount: Int
        get() = rules.count { it.action == RuleAction.ALLOWED && it.category == RuleCategory.PHONE }
}
