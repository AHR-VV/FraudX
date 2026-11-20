package com.fraudx.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.SecurityUpdateWarning
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RuleFolder
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fraudx.app.model.DbUpdateFrequency
import com.fraudx.app.model.FraudXUiState
import com.fraudx.app.model.ProtectionModule
import com.fraudx.app.theme.JetBrainsMonoFamily
import com.fraudx.app.theme.PlusJakartaSansFamily
import com.fraudx.app.theme.VerdantColors
import com.fraudx.app.ui.components.FraudXSwitch
import com.fraudx.app.ui.components.FraudXTopBar
import java.util.Locale

@Composable
fun SettingsScreen(
    uiState: FraudXUiState,
    onNavigateBack: () -> Unit,
    onToggleModule: (ProtectionModule) -> Unit,
    onChangeDbFrequency: (DbUpdateFrequency) -> Unit,
    onSyncThreatDb: () -> Unit,
    onTogglePushAlerts: (Boolean) -> Unit,
    onOpenAllowBlock: () -> Unit
) {
    val protection = uiState.protection
    val armedCount = protection.armedModuleCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdantColors.Surface)
    ) {
        FraudXTopBar(
            subtitle = "Settings",
            isRootDashboard = false,
            subtitleAccentGreen = true,
            onLeadingClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Dynamic Perimeter Defense Overview Banner
            PerimeterOverviewBanner(armedCount = armedCount)

            // Primary Intercept Modules Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PRIMARY INTERCEPT MODULES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.6.sp,
                        color = VerdantColors.DeepForestInk
                    )
                    Text(
                        text = protection.policyStamp,
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = VerdantColors.Outline
                    )
                }

                InterceptModuleCard(
                    icon = Icons.Outlined.Public,
                    title = "Website Protection",
                    subtitle = "Live Zero-Day Link Intercept",
                    description = "Blocks phishing domains, malicious scripts, zero-day malware links, and deceptive redirects in Safari & Chrome via real-time SSL stream validation.",
                    footerIcon = Icons.Outlined.Bolt,
                    footerText = "${"%,d".format(Locale.US, protection.websiteBlockedThisWeek)} blocked this week",
                    checked = protection.modules.website,
                    onCheckedChange = { onToggleModule(ProtectionModule.WEBSITE) }
                )

                InterceptModuleCard(
                    icon = Icons.Outlined.Sms,
                    title = "SMS Protection",
                    subtitle = "Natural Language Smishing Filter",
                    description = "Filters smishing fraud messages, bank impersonation texts, and unverified sender links before notifications reach your lock screen.",
                    footerIcon = Icons.Outlined.FavoriteBorder,
                    footerText = "Zero inbox compromise",
                    checked = protection.modules.sms,
                    onCheckedChange = { onToggleModule(ProtectionModule.SMS) }
                )

                InterceptModuleCard(
                    icon = Icons.Outlined.PhoneInTalk,
                    title = "Call Protection",
                    subtitle = "Carrier-Grade Caller ID Screening",
                    description = "Real-time caller ID scam screening, robocall silent blocking, and instant spoofed geographic footprint detection.",
                    footerIcon = Icons.Outlined.CheckCircle,
                    footerText = "Telephony honeynet active",
                    checked = protection.modules.call,
                    onCheckedChange = { onToggleModule(ProtectionModule.CALL) }
                )
            }

            // Secondary Preferences Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SECONDARY PREFERENCES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.6.sp,
                        color = VerdantColors.DeepForestInk
                    )
                    Text(
                        text = "AUTO PILOT",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = VerdantColors.Outline
                    )
                }

                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = VerdantColors.SurfaceContainerLowest,
                    border = BorderStroke(1.dp, VerdantColors.BorderHairline),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Threat Database Updates Row
                        ThreatDbUpdateRow(
                            lastSyncLabel = protection.lastDbSyncLabel,
                            selectedFrequency = protection.dbUpdateFrequency,
                            isSyncing = uiState.isSyncingDb,
                            onSelectFrequency = onChangeDbFrequency,
                            onSyncNow = onSyncThreatDb
                        )

                        HorizontalDivider(color = VerdantColors.BorderHairline, thickness = 1.dp)

                        // Instant Push Alerts Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                PreferenceLeadingIcon(Icons.Outlined.NotificationsActive)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Instant Push Alerts",
                                        fontFamily = PlusJakartaSansFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = VerdantColors.DeepForestInk,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Immediate priority breach notifications",
                                        fontSize = 12.sp,
                                        color = VerdantColors.OnSurfaceVariant
                                    )
                                }
                            }
                            FraudXSwitch(
                                checked = protection.instantPushAlerts,
                                onCheckedChange = onTogglePushAlerts
                            )
                        }

                        HorizontalDivider(color = VerdantColors.BorderHairline, thickness = 1.dp)

                        // Allow / Block Manually Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onOpenAllowBlock)
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                PreferenceLeadingIcon(Icons.Outlined.RuleFolder)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Allow/Block Manually",
                                        fontFamily = PlusJakartaSansFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = VerdantColors.DeepForestInk
                                    )
                                    Text(
                                        text = "${uiState.allowedCount} allowed rules, ${uiState.blockedCount} blocked rules",
                                        fontSize = 12.sp,
                                        color = VerdantColors.OnSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "MANAGE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp,
                                    color = VerdantColors.Primary
                                )
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = "Manage manual rules",
                                    tint = VerdantColors.Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Encrypted Telemetry Footnote
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = VerdantColors.Primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "All telemetry encrypted locally",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = VerdantColors.OnSurfaceVariant.copy(alpha = 0.85f)
                    )
                }
                Text(
                    text = protection.buildStamp,
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = VerdantColors.Outline
                )
            }
        }
    }
}

@Composable
private fun PerimeterOverviewBanner(armedCount: Int) {
    val spec = when (armedCount) {
        3 -> BannerSpec(
            title = "Full Perimeter Defense",
            subtitle = "3 of 3 Modules Armed",
            iconBg = VerdantColors.SecondaryContainer,
            iconTint = VerdantColors.Primary,
            dotColor = VerdantColors.PrimaryContainer,
            icon = Icons.Filled.VerifiedUser
        )
        2 -> BannerSpec(
            title = "Partial Perimeter Defense",
            subtitle = "2 of 3 Modules Armed",
            iconBg = VerdantColors.WarningBg,
            iconTint = VerdantColors.WarningInk,
            dotColor = VerdantColors.WarningDot,
            icon = Icons.Filled.Shield
        )
        1 -> BannerSpec(
            title = "Partial Perimeter Defense",
            subtitle = "1 of 3 Modules Armed",
            iconBg = VerdantColors.ErrorContainer,
            iconTint = VerdantColors.Error,
            dotColor = VerdantColors.Error,
            icon = Icons.Filled.GppBad
        )
        else -> BannerSpec(
            title = "Perimeter Defense Disabled",
            subtitle = "0 of 3 Modules Armed (Vulnerable)",
            iconBg = VerdantColors.ErrorContainer,
            iconTint = VerdantColors.Error,
            dotColor = VerdantColors.Error,
            icon = Icons.Filled.SecurityUpdateWarning
        )
    }

    val animatedIconBg by animateColorAsState(spec.iconBg, tween(260), label = "banner_icon_bg")
    val animatedIconTint by animateColorAsState(spec.iconTint, tween(260), label = "banner_icon_tint")
    val animatedDotColor by animateColorAsState(spec.dotColor, tween(260), label = "banner_dot")

    val pulseTransition = rememberInfiniteTransition(label = "status_dot_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = VerdantColors.SurfaceContainerLowest,
        border = BorderStroke(1.dp, VerdantColors.BorderHairline),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            animatedDotColor.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        center = Offset(x = 920f, y = 210f),
                        radius = 420f
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(modifier = Modifier.size(46.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(animatedIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = spec.icon,
                            contentDescription = null,
                            tint = animatedIconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (armedCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .scale(pulseScale)
                                    .alpha(pulseAlpha)
                                    .clip(CircleShape)
                                    .background(animatedDotColor)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(animatedDotColor)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spec.title,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = VerdantColors.DeepForestInk
                    )
                    Text(
                        text = spec.subtitle,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = animatedIconTint
                    )
                }
            }
        }
    }
}

@Composable
private fun InterceptModuleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    description: String,
    footerIcon: ImageVector,
    footerText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = VerdantColors.SurfaceContainerLowest,
        border = BorderStroke(1.dp, VerdantColors.BorderHairline),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                            .background(VerdantColors.SecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = VerdantColors.Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VerdantColors.DeepForestInk
                        )
                        Text(
                            text = subtitle,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = VerdantColors.Primary
                        )
                    }
                }
                FraudXSwitch(checked = checked, onCheckedChange = onCheckedChange)
            }

            Text(
                text = description,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = VerdantColors.OnSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = footerIcon,
                    contentDescription = null,
                    tint = if (checked) VerdantColors.Primary else VerdantColors.Outline,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = if (checked) footerText else "Module paused",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = if (checked) VerdantColors.Primary else VerdantColors.Outline
                )
            }
        }
    }
}

@Composable
private fun ThreatDbUpdateRow(
    lastSyncLabel: String,
    selectedFrequency: DbUpdateFrequency,
    isSyncing: Boolean,
    onSelectFrequency: (DbUpdateFrequency) -> Unit,
    onSyncNow: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSyncNow)
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(VerdantColors.SurfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = VerdantColors.Primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Sync,
                        contentDescription = "Sync now",
                        tint = VerdantColors.DeepForestInk,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Threat Database Updates",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = VerdantColors.DeepForestInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Last updated: $lastSyncLabel",
                    fontSize = 12.sp,
                    color = VerdantColors.OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box {
            Surface(
                onClick = { menuExpanded = true },
                shape = CircleShape,
                color = VerdantColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, VerdantColors.BorderHairline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = selectedFrequency.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        color = VerdantColors.DeepForestInk
                    )
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = "Select frequency",
                        tint = VerdantColors.OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                containerColor = VerdantColors.SurfaceContainerLowest
            ) {
                DbUpdateFrequency.entries.forEach { freq ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = freq.label,
                                fontWeight = if (freq == selectedFrequency) FontWeight.Bold else FontWeight.Medium,
                                color = if (freq == selectedFrequency) VerdantColors.Primary else VerdantColors.OnSurface
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onSelectFrequency(freq)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferenceLeadingIcon(icon: ImageVector) {
    Surface(
        shape = CircleShape,
        color = VerdantColors.SurfaceContainerLow,
        border = BorderStroke(1.dp, VerdantColors.BorderHairline),
        modifier = Modifier.size(38.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VerdantColors.DeepForestInk,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private data class BannerSpec(
    val title: String,
    val subtitle: String,
    val iconBg: Color,
    val iconTint: Color,
    val dotColor: Color,
    val icon: ImageVector
)
