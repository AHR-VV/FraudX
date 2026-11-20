package com.fraudx.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SecurityUpdateGood
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fraudx.app.model.FraudXUiState
import com.fraudx.app.model.InterceptionChannel
import com.fraudx.app.model.InterceptionEvent
import com.fraudx.app.model.InterceptionStatus
import com.fraudx.app.model.RuleAction
import com.fraudx.app.theme.JetBrainsMonoFamily
import com.fraudx.app.theme.PlusJakartaSansFamily
import com.fraudx.app.theme.VerdantColors
import com.fraudx.app.ui.components.FraudXTopBar

@Composable
fun InterceptionLogScreen(
    uiState: FraudXUiState,
    onNavigateBack: () -> Unit,
    onEscalateRule: (InterceptionEvent, RuleAction) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var statusFilter by rememberSaveable { mutableStateOf<InterceptionStatus?>(null) }
    var channelFilter by rememberSaveable { mutableStateOf<InterceptionChannel?>(null) }
    var expandedId by rememberSaveable(uiState.selectedInterceptionId) {
        mutableStateOf(uiState.selectedInterceptionId)
    }

    val blockedTotal = uiState.interceptions.count { it.status == InterceptionStatus.BLOCKED }
    val clearedTotal = uiState.interceptions.count { it.status == InterceptionStatus.CLEARED }

    val filtered = remember(uiState.interceptions, searchQuery, statusFilter, channelFilter) {
        val q = searchQuery.trim().lowercase()
        uiState.interceptions.filter { event ->
            (statusFilter == null || event.status == statusFilter) &&
                (channelFilter == null || event.channel == channelFilter) &&
                (q.isEmpty() ||
                    event.target.lowercase().contains(q) ||
                    event.summary.lowercase().contains(q) ||
                    event.sourceDetail.lowercase().contains(q))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdantColors.Surface)
    ) {
        FraudXTopBar(
            subtitle = "Interception Log",
            isRootDashboard = false,
            subtitleAccentGreen = true,
            onLeadingClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tabular Metrics Summary Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LogMetricCard(
                    label = "Total",
                    value = uiState.interceptions.size.toString(),
                    accent = VerdantColors.DeepForestInk,
                    modifier = Modifier.weight(1f)
                )
                LogMetricCard(
                    label = "Blocked",
                    value = blockedTotal.toString(),
                    accent = VerdantColors.Error,
                    modifier = Modifier.weight(1f)
                )
                LogMetricCard(
                    label = "Cleared",
                    value = clearedTotal.toString(),
                    accent = VerdantColors.Primary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = VerdantColors.Outline,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear search",
                                tint = VerdantColors.OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                placeholder = {
                    Text("Search intercepted domains, numbers...", fontSize = 13.sp, color = VerdantColors.Outline)
                },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = VerdantColors.SurfaceContainerLowest,
                    unfocusedContainerColor = VerdantColors.SurfaceContainerLowest,
                    focusedBorderColor = VerdantColors.Primary,
                    unfocusedBorderColor = VerdantColors.OutlineVariant.copy(alpha = 0.6f)
                )
            )

            // Status Segmented Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(
                    label = "All Events",
                    selected = statusFilter == null,
                    onClick = { statusFilter = null },
                    modifier = Modifier.weight(1f)
                )
                StatusChip(
                    label = "Blocked",
                    selected = statusFilter == InterceptionStatus.BLOCKED,
                    onClick = { statusFilter = InterceptionStatus.BLOCKED },
                    modifier = Modifier.weight(1f)
                )
                StatusChip(
                    label = "Cleared",
                    selected = statusFilter == InterceptionStatus.CLEARED,
                    onClick = { statusFilter = InterceptionStatus.CLEARED },
                    modifier = Modifier.weight(1f)
                )
            }

            // Channel Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChannelPill(
                    label = "All Channels",
                    selected = channelFilter == null,
                    onClick = { channelFilter = null }
                )
                InterceptionChannel.entries.forEach { channel ->
                    ChannelPill(
                        label = channel.label,
                        selected = channelFilter == channel,
                        onClick = { channelFilter = channel }
                    )
                }
            }

            // Expandable Interception Entries
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filtered.forEach { event ->
                    ExpandableInterceptionCard(
                        event = event,
                        expanded = expandedId == event.id,
                        onToggleExpand = {
                            expandedId = if (expandedId == event.id) null else event.id
                        },
                        onBlock = { onEscalateRule(event, RuleAction.BLOCKED) },
                        onAllow = { onEscalateRule(event, RuleAction.ALLOWED) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LogMetricCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = VerdantColors.SurfaceContainerLowest,
        border = BorderStroke(1.dp, VerdantColors.BorderHairline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = accent
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = VerdantColors.Outline
            )
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) VerdantColors.ForestToggleCore else VerdantColors.SurfaceContainerLowest,
        border = BorderStroke(1.dp, if (selected) VerdantColors.ForestToggleCore else VerdantColors.BorderHairline),
        modifier = modifier.height(38.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (selected) Color.White else VerdantColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChannelPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) VerdantColors.AllowTabPillBg else VerdantColors.SurfaceContainerLowest,
        border = BorderStroke(
            1.dp,
            if (selected) VerdantColors.SecondaryContainer else VerdantColors.OutlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Text(
            text = label,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            color = if (selected) VerdantColors.OnTertiaryContainer else VerdantColors.OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun ExpandableInterceptionCard(
    event: InterceptionEvent,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onBlock: () -> Unit,
    onAllow: () -> Unit
) {
    val isBlocked = event.status == InterceptionStatus.BLOCKED

    Surface(
        onClick = onToggleExpand,
        shape = RoundedCornerShape(22.dp),
        color = VerdantColors.SurfaceContainerLowest,
        border = BorderStroke(
            1.dp,
            if (expanded) VerdantColors.Primary.copy(alpha = 0.45f) else VerdantColors.BorderHairline
        ),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isBlocked) VerdantColors.InactiveChipBg else VerdantColors.SecondaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBlocked) Icons.Outlined.GppBad else Icons.Outlined.SecurityUpdateGood,
                            contentDescription = null,
                            tint = if (isBlocked) VerdantColors.InactiveRedAccent else VerdantColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = event.target,
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = VerdantColors.OnSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isBlocked) VerdantColors.Error else VerdantColors.SecondaryContainer
                            ) {
                                Text(
                                    text = if (isBlocked) "BLOCKED" else "CLEARED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = if (isBlocked) Color.White else VerdantColors.OnSecondaryFixedVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${event.channel.label} • ${event.summary}",
                            fontSize = 12.sp,
                            color = VerdantColors.Outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = event.relativeTime,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VerdantColors.Outline
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = VerdantColors.Outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdantColors.SurfaceContainerLow.copy(alpha = 0.6f))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = VerdantColors.BorderHairline, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = event.sourceDetail,
                            fontSize = 12.sp,
                            color = VerdantColors.OnSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Risk ${event.riskScore}/100",
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isBlocked) VerdantColors.Error else VerdantColors.Primary
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        event.heuristicSignals.forEach { signal ->
                            Text(
                                text = "• $signal",
                                fontSize = 12.sp,
                                color = VerdantColors.OnSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onBlock,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VerdantColors.Error,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Add to Blocklist", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onAllow,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Trust in Allowlist", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
