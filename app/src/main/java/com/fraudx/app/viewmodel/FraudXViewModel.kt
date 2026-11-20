package com.fraudx.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fraudx.app.data.FraudXRepository
import com.fraudx.app.data.MockFraudXRepository
import com.fraudx.app.model.DbUpdateFrequency
import com.fraudx.app.model.FraudXUiState
import com.fraudx.app.model.InterceptionEvent
import com.fraudx.app.model.ProtectionModule
import com.fraudx.app.model.RuleAction
import com.fraudx.app.model.RuleCategory
import com.fraudx.app.model.ScanVerdict
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Unidirectional Data Flow (UDF) ViewModel shared across all FraudX Compose destinations.
 * Inject a production [FraudXRepository] via Hilt/Koin/Factory when connecting a real backend.
 */
class FraudXViewModel(
    private val repository: FraudXRepository = MockFraudXRepository()
) : ViewModel() {

    private val isSyncingDb = MutableStateFlow(false)
    private val isScanningPayload = MutableStateFlow(false)
    private val lastScanVerdict = MutableStateFlow<ScanVerdict?>(null)
    private val selectedInterceptionId = MutableStateFlow<String?>(null)

    private val _snackbarEvents = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    private data class TransientState(
        val syncingDb: Boolean,
        val scanning: Boolean,
        val verdict: ScanVerdict?,
        val selectedId: String?
    )

    private val transientFlow = combine(
        isSyncingDb,
        isScanningPayload,
        lastScanVerdict,
        selectedInterceptionId
    ) { syncing, scanning, verdict, selectedId ->
        TransientState(syncing, scanning, verdict, selectedId)
    }

    val uiState: StateFlow<FraudXUiState> = combine(
        repository.observeProtectionState(),
        repository.observeRules(),
        repository.observeInterceptions(),
        repository.observeAuditLog(),
        transientFlow
    ) { protection, rules, interceptions, auditLog, transient ->
        FraudXUiState(
            protection = protection,
            rules = rules,
            interceptions = interceptions,
            auditLog = auditLog,
            isSyncingDb = transient.syncingDb,
            isScanningPayload = transient.scanning,
            lastScanVerdict = transient.verdict,
            selectedInterceptionId = transient.selectedId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FraudXUiState()
    )

    fun toggleMasterShield() {
        viewModelScope.launch {
            val updated = repository.toggleMasterShield()
            val message = if (updated.isMasterEnabled) {
                "Real-time protection enabled (${updated.armedModuleCount}/3 modules armed)"
            } else {
                "Protection paused — device perimeter inactive"
            }
            _snackbarEvents.emit(message)
        }
    }

    fun toggleModule(module: ProtectionModule) {
        viewModelScope.launch {
            val updated = repository.toggleModule(module)
            if (!updated.isMasterEnabled) {
                _snackbarEvents.emit("All modules disabled — Dashboard protection set to OFF")
            }
        }
    }

    fun updateDbFrequency(frequency: DbUpdateFrequency) {
        viewModelScope.launch {
            repository.updateDbFrequency(frequency)
            _snackbarEvents.emit("Threat database updates set to ${frequency.label.lowercase()}")
        }
    }

    fun syncThreatDatabase() {
        if (isSyncingDb.value) return
        viewModelScope.launch {
            isSyncingDb.value = true
            try {
                repository.syncThreatDatabase()
                _snackbarEvents.emit("Threat definitions synchronized")
            } finally {
                isSyncingDb.value = false
            }
        }
    }

    fun setInstantPushAlerts(enabled: Boolean) {
        viewModelScope.launch {
            repository.setInstantPushAlerts(enabled)
        }
    }

    fun inspectPayload(rawPayload: String) {
        if (rawPayload.isBlank() || isScanningPayload.value) return
        viewModelScope.launch {
            isScanningPayload.value = true
            lastScanVerdict.value = null
            try {
                val verdict = repository.inspectPayload(rawPayload)
                lastScanVerdict.value = verdict
            } finally {
                isScanningPayload.value = false
            }
        }
    }

    fun clearScanVerdict() {
        lastScanVerdict.value = null
    }

    fun escalateVerdictToRule(verdict: ScanVerdict, action: RuleAction) {
        viewModelScope.launch {
            repository.addManualRule(
                target = verdict.payload,
                action = action,
                category = verdict.detectedType,
                description = verdict.headline,
                tag = "INSPECTOR ESCALATION"
            )
            repository.recordVerdictToLog(verdict)
            val dest = if (action == RuleAction.ALLOWED) "Allowed list" else "Blocked list"
            _snackbarEvents.emit("Saved \"${verdict.payload}\" to $dest")
        }
    }

    fun saveVerdictToLogOnly(verdict: ScanVerdict) {
        viewModelScope.launch {
            repository.recordVerdictToLog(verdict)
            _snackbarEvents.emit("Recorded scan result in Interception Log")
        }
    }

    fun addManualRule(target: String, action: RuleAction, category: RuleCategory) {
        if (target.isBlank()) return
        viewModelScope.launch {
            val created = repository.addManualRule(target = target, action = action, category = category)
            val listName = if (created.action == RuleAction.ALLOWED) "Whitelist" else "Blacklist"
            _snackbarEvents.emit("Added ${created.target} to $listName")
        }
    }

    fun escalateInterceptionToRule(event: InterceptionEvent, action: RuleAction) {
        val category = when (event.channel) {
            com.fraudx.app.model.InterceptionChannel.WEBSITE -> RuleCategory.WEBSITE
            com.fraudx.app.model.InterceptionChannel.PHONE -> RuleCategory.PHONE
            com.fraudx.app.model.InterceptionChannel.SMS -> RuleCategory.SMS
        }
        viewModelScope.launch {
            repository.addManualRule(
                target = event.target,
                action = action,
                category = category,
                description = event.summary,
                tag = "LOG ESCALATION"
            )
            val label = if (action == RuleAction.ALLOWED) "Allowlist" else "Blocklist"
            _snackbarEvents.emit("Added ${event.target} to $label")
        }
    }

    fun toggleRuleAction(ruleId: String) {
        viewModelScope.launch {
            val updated = repository.toggleRuleAction(ruleId)
            updated?.let {
                val dest = if (it.action == RuleAction.ALLOWED) "Allowed" else "Blocked"
                _snackbarEvents.emit("Moved ${it.target} to $dest")
            }
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            repository.deleteRule(ruleId)
            _snackbarEvents.emit("Manual rule deleted")
        }
    }

    fun selectInterception(id: String?) {
        selectedInterceptionId.value = id
    }

    fun notifyMessage(message: String) {
        viewModelScope.launch {
            _snackbarEvents.emit(message)
        }
    }
}
