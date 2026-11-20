package com.fraudx.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fraudx.app.theme.VerdantColors
import com.fraudx.app.ui.components.AuditLogSheet
import com.fraudx.app.ui.components.LinkInspectorSheet
import com.fraudx.app.ui.screens.AllowBlockScreen
import com.fraudx.app.ui.screens.DashboardScreen
import com.fraudx.app.ui.screens.InterceptionLogScreen
import com.fraudx.app.ui.screens.SettingsScreen
import com.fraudx.app.viewmodel.FraudXViewModel

object FraudXRoutes {
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
    const val ALLOW_BLOCK = "allow_block"
    const val INTERCEPTION_LOG = "interception_log"
}

@Composable
fun FraudXNavHost(
    viewModel: FraudXViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showInspectorSheet by rememberSaveable { mutableStateOf(false) }
    var showAuditSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = VerdantColors.Surface,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = FraudXRoutes.DASHBOARD,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(220)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(220)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(220)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(220)
                    )
                }
            ) {
                composable(FraudXRoutes.DASHBOARD) {
                    DashboardScreen(
                        uiState = uiState,
                        onOpenSettings = { navController.navigate(FraudXRoutes.SETTINGS) },
                        onToggleProtection = { viewModel.toggleMasterShield() },
                        onOpenInspector = {
                            viewModel.clearScanVerdict()
                            showInspectorSheet = true
                        },
                        onOpenFullLog = { selectedId ->
                            viewModel.selectInterception(selectedId)
                            navController.navigate(FraudXRoutes.INTERCEPTION_LOG)
                        }
                    )
                }

                composable(FraudXRoutes.SETTINGS) {
                    SettingsScreen(
                        uiState = uiState,
                        onNavigateBack = { navController.popBackStack() },
                        onToggleModule = { viewModel.toggleModule(it) },
                        onChangeDbFrequency = { viewModel.updateDbFrequency(it) },
                        onSyncThreatDb = { viewModel.syncThreatDatabase() },
                        onTogglePushAlerts = { viewModel.setInstantPushAlerts(it) },
                        onToggleVoiceAssistance = { viewModel.setVoiceAssistance(it) },
                        onOpenAllowBlock = { navController.navigate(FraudXRoutes.ALLOW_BLOCK) }
                    )
                }

                composable(FraudXRoutes.ALLOW_BLOCK) {
                    AllowBlockScreen(
                        uiState = uiState,
                        onNavigateBack = { navController.popBackStack() },
                        onAddRule = { target, action, category ->
                            viewModel.addManualRule(target, action, category)
                        },
                        onToggleRuleAction = { viewModel.toggleRuleAction(it) },
                        onDeleteRule = { viewModel.deleteRule(it) },
                        onOpenAuditLog = { showAuditSheet = true },
                        onNotify = { viewModel.notifyMessage(it) }
                    )
                }

                composable(FraudXRoutes.INTERCEPTION_LOG) {
                    InterceptionLogScreen(
                        uiState = uiState,
                        onNavigateBack = { navController.popBackStack() },
                        onEscalateRule = { event, action ->
                            viewModel.escalateInterceptionToRule(event, action)
                        }
                    )
                }
            }
        }
    }

    if (showInspectorSheet) {
        LinkInspectorSheet(
            isScanning = uiState.isScanningPayload,
            verdict = uiState.lastScanVerdict,
            onDismiss = {
                showInspectorSheet = false
                viewModel.clearScanVerdict()
            },
            onRunScan = { payload -> viewModel.inspectPayload(payload) },
            onEscalateRule = { verdict, action ->
                viewModel.escalateVerdictToRule(verdict, action)
                showInspectorSheet = false
                viewModel.clearScanVerdict()
            },
            onSaveToLog = { verdict ->
                viewModel.saveVerdictToLogOnly(verdict)
                showInspectorSheet = false
                viewModel.clearScanVerdict()
            }
        )
    }

    if (showAuditSheet) {
        AuditLogSheet(
            entries = uiState.auditLog,
            onDismiss = { showAuditSheet = false },
            onCopiedAudit = { viewModel.notifyMessage("Audit log JSON copied to clipboard") }
        )
    }
}
