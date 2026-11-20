package com.fraudx.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FindInPage
import androidx.compose.material.icons.rounded.GppBad
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fraudx.app.model.RuleAction
import com.fraudx.app.model.ScanVerdict
import com.fraudx.app.model.VerdictLevel
import com.fraudx.app.theme.JetBrainsMonoFamily
import com.fraudx.app.theme.PlusJakartaSansFamily
import com.fraudx.app.theme.VerdantColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkInspectorSheet(
    isScanning: Boolean,
    verdict: ScanVerdict?,
    onDismiss: () -> Unit,
    onRunScan: (String) -> Unit,
    onEscalateRule: (ScanVerdict, RuleAction) -> Unit,
    onSaveToLog: (ScanVerdict) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var payloadInput by rememberSaveable { mutableStateOf("") }

    val quickSamples = listOf(
        "Phishing Link" to "https://secure-banking-login.xyz/verify-account",
        "Suspicious SMS" to "URGENT: Your parcel fee is unpaid, confirm at bit.ly/3xPay",
        "Trusted Domain" to "https://chase.com/security"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VerdantColors.SurfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FindInPage,
                        contentDescription = null,
                        tint = VerdantColors.Primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Inspect Unknown Payload",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = VerdantColors.OnSurface
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VerdantColors.SurfaceContainer)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close Inspector",
                        tint = VerdantColors.OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "Paste any suspicious text message, unfamiliar URL, or sender handle to simulate isolation before opening.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = VerdantColors.Outline
            )

            // Quick test samples for one-tap verification
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickSamples.forEach { (label, sample) ->
                    Surface(
                        onClick = { payloadInput = sample },
                        shape = CircleShape,
                        color = VerdantColors.SurfaceContainerLow,
                        border = BorderStroke(1.dp, VerdantColors.BorderHairline)
                    ) {
                        Text(
                            text = "Try: $label",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VerdantColors.DeepForestInk,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // Input
            OutlinedTextField(
                value = payloadInput,
                onValueChange = { payloadInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "https://... or paste raw text message",
                        color = VerdantColors.Outline,
                        fontSize = 14.sp
                    )
                },
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = VerdantColors.SurfaceContainerLow,
                    unfocusedContainerColor = VerdantColors.SurfaceContainerLow,
                    focusedBorderColor = VerdantColors.Primary,
                    unfocusedBorderColor = VerdantColors.OutlineVariant.copy(alpha = 0.6f)
                )
            )

            // Deep Scan Action Button
            Button(
                onClick = { onRunScan(payloadInput) },
                enabled = payloadInput.isNotBlank() && !isScanning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdantColors.Primary,
                    contentColor = Color.White
                )
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Analyzing target heuristics...",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Policy,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Deep Scan",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Detailed Scan Verdict Card
            if (verdict != null) {
                VerdictCard(
                    verdict = verdict,
                    onBlock = { onEscalateRule(verdict, RuleAction.BLOCKED) },
                    onAllow = { onEscalateRule(verdict, RuleAction.ALLOWED) },
                    onSaveLog = { onSaveToLog(verdict) }
                )
            }
        }
    }
}

@Composable
private fun VerdictCard(
    verdict: ScanVerdict,
    onBlock: () -> Unit,
    onAllow: () -> Unit,
    onSaveLog: () -> Unit
) {
    val (bg, border, ink, badgeText, icon) = when (verdict.verdict) {
        VerdictLevel.MALICIOUS -> Tuple5(
            VerdantColors.AlertCardBg,
            VerdantColors.AlertCardBorder,
            VerdantColors.Error,
            "HIGH RISK • ${verdict.riskScore}/100",
            Icons.Rounded.GppBad
        )
        VerdictLevel.SUSPICIOUS -> Tuple5(
            VerdantColors.WarningBg.copy(alpha = 0.55f),
            VerdantColors.WarningDot.copy(alpha = 0.45f),
            VerdantColors.WarningInk,
            "SUSPICIOUS • ${verdict.riskScore}/100",
            Icons.Rounded.Warning
        )
        VerdictLevel.SAFE -> Tuple5(
            VerdantColors.SecondaryContainer.copy(alpha = 0.4f),
            VerdantColors.SecondaryFixedDim,
            VerdantColors.Primary,
            "SAFE • ${verdict.riskScore}/100",
            Icons.Rounded.CheckCircle
        )
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bg,
        border = BorderStroke(1.dp, border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ink,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = badgeText,
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = ink
                    )
                }
                Text(
                    text = verdict.detectedType.label,
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = VerdantColors.Outline
                )
            }

            Text(
                text = verdict.headline,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = VerdantColors.OnSurface
            )

            Text(
                text = verdict.explanation,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = VerdantColors.OnSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                verdict.signals.forEach { signal ->
                    Text(
                        text = "• $signal",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = VerdantColors.OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (verdict.verdict != VerdictLevel.SAFE) {
                    Button(
                        onClick = onBlock,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdantColors.Error,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Block Target", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onSaveLog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save to Log", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Button(
                        onClick = onAllow,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdantColors.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Add to Allowlist", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onSaveLog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Log Cleared", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
