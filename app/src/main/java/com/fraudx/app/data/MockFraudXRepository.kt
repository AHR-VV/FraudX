package com.fraudx.app.data

import com.fraudx.app.model.AuditEntry
import com.fraudx.app.model.DbUpdateFrequency
import com.fraudx.app.model.InterceptionChannel
import com.fraudx.app.model.InterceptionEvent
import com.fraudx.app.model.InterceptionStatus
import com.fraudx.app.model.ManualRule
import com.fraudx.app.model.ModuleSnapshot
import com.fraudx.app.model.ProtectionModule
import com.fraudx.app.model.ProtectionState
import com.fraudx.app.model.RuleAction
import com.fraudx.app.model.RuleCategory
import com.fraudx.app.model.ScanVerdict
import com.fraudx.app.model.VerdictLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * In-memory mock implementation of [FraudXRepository] seeded with the exact reference screens.
 * Replace with a production Retrofit/Room repository without changing any Compose UI code.
 */
class MockFraudXRepository : FraudXRepository {

    private val protectionFlow = MutableStateFlow(ProtectionState())

    private val rulesFlow = MutableStateFlow(
        listOf(
            ManualRule(
                id = "rule-1",
                target = "chase.com",
                action = RuleAction.ALLOWED,
                badgeLabel = "ALLOW",
                category = RuleCategory.WEBSITE,
                description = "Always Allow (Bypasses AI heuristic engine)",
                tag = "TAG: TRUSTED FINANCE",
                createdAtLabel = "ADDED 3D AGO"
            ),
            ManualRule(
                id = "rule-2",
                target = "+1 (800) 555-0199",
                action = RuleAction.ALLOWED,
                badgeLabel = "ALLOW",
                category = RuleCategory.PHONE,
                description = "Verified Bank Hotline • Bypass Robocall Drop",
                tag = "CALLER-ID BYPASS",
                createdAtLabel = "ADDED 1W AGO"
            ),
            ManualRule(
                id = "rule-3",
                target = "account-security-update-notice.xyz",
                action = RuleAction.BLOCKED,
                badgeLabel = "HARD BLOCK",
                category = RuleCategory.WEBSITE,
                description = "Phishing Decoy • Smishing Intercept",
                tag = "TRIGGER: SMS EXPLOIT",
                createdAtLabel = "ADDED 4H AGO"
            ),
            ManualRule(
                id = "rule-4",
                target = "+1 (888) 492-9102",
                action = RuleAction.BLOCKED,
                badgeLabel = "BLOCK",
                category = RuleCategory.PHONE,
                description = "Scam Call • Auto-silent & Drop Protocol",
                tag = "AUTO-DROP WITHOUT RING",
                createdAtLabel = "ADDED YESTERDAY"
            ),
            ManualRule(
                id = "rule-5",
                target = "\"URGENT: WIRE TRANSFER\"",
                action = RuleAction.BLOCKED,
                badgeLabel = "BLOCK PATTERN",
                category = RuleCategory.SMS,
                description = "SMS Keyword Rule • Instant Quarantine",
                tag = "REGEX MATCH",
                createdAtLabel = "ADDED 5D AGO"
            )
        )
    )

    private val interceptionsFlow = MutableStateFlow(
        listOf(
            InterceptionEvent(
                id = "int-1",
                target = "secure-banking-login.xyz",
                summary = "SMS credential phishing decoy",
                status = InterceptionStatus.BLOCKED,
                channel = InterceptionChannel.WEBSITE,
                relativeTime = "12m ago",
                riskScore = 96,
                sourceDetail = "Intercepted link tap from SMS thread +1 (415) 890-3321",
                heuristicSignals = listOf(
                    "High-risk newly registered .xyz TLD (< 48h)",
                    "Brand impersonation credential form signature",
                    "Missing organization EV/OV TLS identity"
                )
            ),
            InterceptionEvent(
                id = "int-2",
                target = "FedEx Delivery Tracking #91",
                summary = "Genuine postal webhook",
                status = InterceptionStatus.CLEARED,
                channel = InterceptionChannel.SMS,
                relativeTime = "1h ago",
                riskScore = 6,
                sourceDetail = "Verified carrier short code 48773",
                heuristicSignals = listOf(
                    "DKIM / carrier short-code sender verified",
                    "Destination resolves cleanly to fedex.com"
                )
            ),
            InterceptionEvent(
                id = "int-3",
                target = "+1 (888) 492-9102",
                summary = "Spoofed IRS tax collection robocall",
                status = InterceptionStatus.BLOCKED,
                channel = InterceptionChannel.PHONE,
                relativeTime = "3h ago",
                riskScore = 91,
                sourceDetail = "STIR/SHAKEN attestation failed (Level C)",
                heuristicSignals = listOf(
                    "VoIP spoofed geographic footprint mismatch",
                    "Reported by 14,200+ honeynet sensors in 24h"
                )
            ),
            InterceptionEvent(
                id = "int-4",
                target = "pay-toll-overdue-ezpass.top",
                summary = "Unpaid highway toll smishing bait",
                status = InterceptionStatus.BLOCKED,
                channel = InterceptionChannel.SMS,
                relativeTime = "Yesterday",
                riskScore = 94,
                sourceDetail = "Quarantined before lock-screen notification",
                heuristicSignals = listOf(
                    "Urgency coercion wording matched NLP filter",
                    "Payment card skimmer redirect chain detected"
                )
            ),
            InterceptionEvent(
                id = "int-5",
                target = "chase.com/verify-alert",
                summary = "Trusted banking domain allowlist match",
                status = InterceptionStatus.CLEARED,
                channel = InterceptionChannel.WEBSITE,
                relativeTime = "2d ago",
                riskScore = 2,
                sourceDetail = "Matched manual Allow override (chase.com)",
                heuristicSignals = listOf(
                    "Manual whitelist precedence applied",
                    "Valid DigiCert EV TLS certificate chain"
                )
            )
        )
    )

    private val auditLogFlow = MutableStateFlow(
        listOf(
            AuditEntry(
                id = "aud-1",
                actionLabel = "RULE ADDED",
                target = "account-security-update-notice.xyz",
                detail = "Hard Block • Website / URL",
                timestampLabel = "4h ago"
            ),
            AuditEntry(
                id = "aud-2",
                actionLabel = "RULE ADDED",
                target = "+1 (888) 492-9102",
                detail = "Block • Phone / Caller ID",
                timestampLabel = "Yesterday"
            ),
            AuditEntry(
                id = "aud-3",
                actionLabel = "RULE ADDED",
                target = "chase.com",
                detail = "Allow • Trusted Finance",
                timestampLabel = "3d ago"
            )
        )
    )

    override fun observeProtectionState(): Flow<ProtectionState> = protectionFlow.asStateFlow()
    override fun observeRules(): Flow<List<ManualRule>> = rulesFlow.asStateFlow()
    override fun observeInterceptions(): Flow<List<InterceptionEvent>> = interceptionsFlow.asStateFlow()
    override fun observeAuditLog(): Flow<List<AuditEntry>> = auditLogFlow.asStateFlow()

    override suspend fun toggleMasterShield(): ProtectionState {
        lateinit var updated: ProtectionState
        protectionFlow.update { current ->
            val next = if (current.isMasterEnabled) {
                current.copy(
                    lastArmedSnapshot = current.modules,
                    modules = ModuleSnapshot(website = false, sms = false, call = false)
                )
            } else {
                val restored = if (current.lastArmedSnapshot.anyArmed) {
                    current.lastArmedSnapshot
                } else {
                    ModuleSnapshot(website = true, sms = true, call = true)
                }
                current.copy(modules = restored)
            }
            updated = next
            next
        }
        appendAudit(
            actionLabel = if (updated.isMasterEnabled) "SHIELD ARMED" else "SHIELD PAUSED",
            target = "Master Perimeter",
            detail = "${updated.armedModuleCount} of 3 modules armed"
        )
        return updated
    }

    override suspend fun toggleModule(module: ProtectionModule): ProtectionState {
        lateinit var updated: ProtectionState
        protectionFlow.update { current ->
            val nextModules = when (module) {
                ProtectionModule.WEBSITE -> current.modules.copy(website = !current.modules.website)
                ProtectionModule.SMS -> current.modules.copy(sms = !current.modules.sms)
                ProtectionModule.CALL -> current.modules.copy(call = !current.modules.call)
            }
            val nextSnapshot = if (nextModules.anyArmed) nextModules else current.lastArmedSnapshot
            val next = current.copy(
                modules = nextModules,
                lastArmedSnapshot = nextSnapshot
            )
            updated = next
            next
        }
        return updated
    }

    override suspend fun updateDbFrequency(frequency: DbUpdateFrequency): ProtectionState {
        lateinit var updated: ProtectionState
        protectionFlow.update { current ->
            updated = current.copy(dbUpdateFrequency = frequency)
            updated
        }
        return updated
    }

    override suspend fun syncThreatDatabase(): ProtectionState {
        delay(650)
        lateinit var updated: ProtectionState
        protectionFlow.update { current ->
            updated = current.copy(lastDbSyncLabel = "Just now (via encrypted sync)")
            updated
        }
        return updated
    }

    override suspend fun setInstantPushAlerts(enabled: Boolean): ProtectionState {
        lateinit var updated: ProtectionState
        protectionFlow.update { current ->
            updated = current.copy(instantPushAlerts = enabled)
            updated
        }
        return updated
    }

    override suspend fun setVoiceAssistance(enabled: Boolean): ProtectionState {
        lateinit var updated: ProtectionState
        protectionFlow.update { current ->
            updated = current.copy(voiceAssistance = enabled)
            updated
        }
        return updated
    }

    override suspend fun inspectPayload(rawPayload: String): ScanVerdict {
        delay(900)
        val trimmed = rawPayload.trim()
        val lower = trimmed.lowercase()

        val detectedType = when {
            lower.startsWith("http") || lower.contains(".com") || lower.contains(".xyz") ||
                lower.contains(".top") || lower.contains(".net") || lower.contains(".org") ->
                RuleCategory.WEBSITE
            trimmed.replace(Regex("[\\s()+-]"), "").all { it.isDigit() } &&
                trimmed.count { it.isDigit() } >= 7 ->
                RuleCategory.PHONE
            else -> RuleCategory.SMS
        }

        val highRiskMarkers = listOf(
            ".xyz", ".top", ".ru", "login", "verify-account", "wire transfer",
            "urgent", "suspended", "claim prize", "gift card", "otp", "ezpass", "492-9102"
        )
        val mediumRiskMarkers = listOf(
            "bit.ly", "tinyurl", "click here", "update", "confirm", "refund", "delivery fee"
        )
        val trustedMarkers = listOf(
            "chase.com", "fedex.com", "google.com", "apple.com", "555-0199"
        )

        val matchedHigh = highRiskMarkers.filter { lower.contains(it) }
        val matchedMedium = mediumRiskMarkers.filter { lower.contains(it) }
        val isTrusted = trustedMarkers.any { lower.contains(it) } && matchedHigh.isEmpty()

        return when {
            isTrusted -> ScanVerdict(
                payload = trimmed,
                detectedType = detectedType,
                verdict = VerdictLevel.SAFE,
                riskScore = 4,
                headline = "Verified safe — no threat vectors found",
                explanation = "Target matched trusted domain/sender reputation with clean TLS and zero deceptive redirects.",
                signals = listOf(
                    "Clean domain & carrier reputation ledger",
                    "Zero credential harvesting patterns detected",
                    "No obfuscated redirect hops"
                )
            )
            matchedHigh.isNotEmpty() -> ScanVerdict(
                payload = trimmed,
                detectedType = detectedType,
                verdict = VerdictLevel.MALICIOUS,
                riskScore = (86 + matchedHigh.size * 4).coerceAtMost(99),
                headline = "High-risk phishing or fraud vector detected",
                explanation = "Payload exhibits credential harvesting or social-engineering coercion signatures. Do not open or reply.",
                signals = listOf(
                    "Matched high-risk fraud indicators: ${matchedHigh.joinToString(", ")}",
                    "Elevated impersonation / smishing heuristic score",
                    "Recommended action: Add immediately to Blocklist"
                )
            )
            matchedMedium.isNotEmpty() -> ScanVerdict(
                payload = trimmed,
                detectedType = detectedType,
                verdict = VerdictLevel.SUSPICIOUS,
                riskScore = 62,
                headline = "Suspicious payload — proceed with caution",
                explanation = "Contains shortened link or unverified urgency cues commonly used in social engineering campaigns.",
                signals = listOf(
                    "Matched caution triggers: ${matchedMedium.joinToString(", ")}",
                    "Sender identity could not be cryptographically verified"
                )
            )
            else -> ScanVerdict(
                payload = trimmed,
                detectedType = detectedType,
                verdict = VerdictLevel.SAFE,
                riskScore = 11,
                headline = "Target isolated — no malicious indicators",
                explanation = "Real-time heuristic sweep found no known phishing domains, malware scripts, or smishing signatures.",
                signals = listOf(
                    "Zero matches in FraudX threat intelligence feed",
                    "No deceptive URL obfuscation or spoofed headers"
                )
            )
        }
    }

    override suspend fun recordVerdictToLog(verdict: ScanVerdict): InterceptionEvent {
        val status = if (verdict.verdict == VerdictLevel.SAFE) {
            InterceptionStatus.CLEARED
        } else {
            InterceptionStatus.BLOCKED
        }
        val channel = when (verdict.detectedType) {
            RuleCategory.WEBSITE -> InterceptionChannel.WEBSITE
            RuleCategory.PHONE -> InterceptionChannel.PHONE
            RuleCategory.SMS -> InterceptionChannel.SMS
        }
        val event = InterceptionEvent(
            id = "int-${UUID.randomUUID().toString().take(8)}",
            target = verdict.payload,
            summary = verdict.headline,
            status = status,
            channel = channel,
            relativeTime = "Just now",
            riskScore = verdict.riskScore,
            sourceDetail = "Manual deep-scan via Link & Message Inspector",
            heuristicSignals = verdict.signals
        )
        interceptionsFlow.update { listOf(event) + it }
        return event
    }

    override suspend fun addManualRule(
        target: String,
        action: RuleAction,
        category: RuleCategory,
        description: String?,
        tag: String?
    ): ManualRule {
        val cleaned = target.trim()
        val badge = when {
            action == RuleAction.ALLOWED -> "ALLOW"
            category == RuleCategory.SMS -> "BLOCK PATTERN"
            category == RuleCategory.PHONE -> "BLOCK"
            else -> "HARD BLOCK"
        }
        val rule = ManualRule(
            id = "rule-${UUID.randomUUID().toString().take(8)}",
            target = cleaned,
            action = action,
            badgeLabel = badge,
            category = category,
            description = description ?: if (action == RuleAction.ALLOWED) {
                "User Custom Allow Rule • Bypasses AI heuristic engine"
            } else {
                "User Custom Deterministic Block Rule"
            },
            tag = tag ?: "MANUAL OVERRIDE",
            createdAtLabel = "JUST NOW"
        )
        rulesFlow.update { current ->
            listOf(rule) + current.filterNot { it.target.equals(cleaned, ignoreCase = true) }
        }
        appendAudit(
            actionLabel = if (action == RuleAction.ALLOWED) "ALLOW ADDED" else "BLOCK ADDED",
            target = cleaned,
            detail = "$badge • ${category.label}"
        )
        return rule
    }

    override suspend fun toggleRuleAction(ruleId: String): ManualRule? {
        var updatedRule: ManualRule? = null
        rulesFlow.update { current ->
            current.map { rule ->
                if (rule.id == ruleId) {
                    val nextAction = if (rule.action == RuleAction.ALLOWED) {
                        RuleAction.BLOCKED
                    } else {
                        RuleAction.ALLOWED
                    }
                    val nextBadge = if (nextAction == RuleAction.ALLOWED) "ALLOW" else "HARD BLOCK"
                    rule.copy(
                        action = nextAction,
                        badgeLabel = nextBadge,
                        description = if (nextAction == RuleAction.ALLOWED) {
                            "Always Allow (Bypasses AI heuristic engine)"
                        } else {
                            "Hard Block • Manual Quarantine Override"
                        }
                    ).also { updatedRule = it }
                } else {
                    rule
                }
            }
        }
        updatedRule?.let {
            appendAudit(
                actionLabel = "RULE MOVED",
                target = it.target,
                detail = "Switched to ${it.badgeLabel}"
            )
        }
        return updatedRule
    }

    override suspend fun deleteRule(ruleId: String) {
        val removed = rulesFlow.value.firstOrNull { it.id == ruleId }
        rulesFlow.update { list -> list.filterNot { it.id == ruleId } }
        removed?.let {
            appendAudit(
                actionLabel = "RULE DELETED",
                target = it.target,
                detail = "Removed from ${it.action.name.lowercase()} perimeter"
            )
        }
    }

    private fun appendAudit(actionLabel: String, target: String, detail: String) {
        val entry = AuditEntry(
            id = "aud-${UUID.randomUUID().toString().take(8)}",
            actionLabel = actionLabel,
            target = target,
            detail = detail,
            timestampLabel = "Just now"
        )
        auditLogFlow.update { listOf(entry) + it }
    }
}
