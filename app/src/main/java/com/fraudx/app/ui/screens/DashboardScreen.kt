package com.fraudx.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material.icons.outlined.GppMaybe
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.SecurityUpdateGood
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fraudx.app.model.FraudXUiState
import com.fraudx.app.model.InterceptionEvent
import com.fraudx.app.model.InterceptionStatus
import com.fraudx.app.theme.PlusJakartaSansFamily
import com.fraudx.app.theme.VerdantColors
import com.fraudx.app.ui.components.FraudXTopBar

@Composable
fun DashboardScreen(
    uiState: FraudXUiState,
    onOpenSettings: () -> Unit,
    onToggleProtection: () -> Unit,
    onOpenInspector: () -> Unit,
    onOpenFullLog: (String?) -> Unit
) {
    val isActive = uiState.protection.isMasterEnabled
    val armedCount = uiState.protection.armedModuleCount

    val coreColor by animateColorAsState(
        targetValue = if (isActive) VerdantColors.ForestToggleCore else VerdantColors.InactiveChipBg.copy(alpha = 0.65f),
        animationSpec = tween(260),
        label = "shield_core_color"
    )
    val outerRingColor by animateColorAsState(
        targetValue = if (isActive) VerdantColors.EmeraldRingGlow.copy(alpha = 0.4f) else VerdantColors.InactiveChipBg,
        animationSpec = tween(260),
        label = "shield_outer_ring"
    )
    val coreTextColor by animateColorAsState(
        targetValue = if (isActive) Color.White else VerdantColors.InactiveCrimson,
        animationSpec = tween(220),
        label = "shield_label_color"
    )
    val ctaBackground by animateColorAsState(
        targetValue = if (isActive) VerdantColors.LimeActionBg else VerdantColors.SecondaryContainer,
        animationSpec = tween(240),
        label = "cta_bg"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdantColors.Surface)
    ) {
        FraudXTopBar(
            subtitle = "DASHBOARD",
            isRootDashboard = true,
            onLeadingClick = onOpenSettings
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Top Status Chip & Live Engine Telemetry Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isActive) VerdantColors.SecondaryContainer.copy(alpha = 0.72f) else VerdantColors.InactiveChipBg,
                    border = BorderStroke(
                        1.dp,
                        if (isActive) VerdantColors.Secondary.copy(alpha = 0.2f) else VerdantColors.InactiveChipBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) Color(0xFF16A34A) else Color(0xFFEF4444)
                                )
                        )
                        Text(
                            text = when {
                                !isActive -> "PERIMETER: INACTIVE"
                                armedCount < 3 -> "LIVE PERIMETER: $armedCount/3"
                                else -> "LIVE PERIMETER: ACTIVE"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.7.sp,
                            color = if (isActive) Color(0xFF0F5328) else VerdantColors.InactiveCrimson
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Outlined.VerifiedUser else Icons.Outlined.GppMaybe,
                        contentDescription = null,
                        tint = if (isActive) VerdantColors.Primary else Color(0xFFB91C1C),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isActive) uiState.protection.engineVersion else "Unprotected",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = if (isActive) VerdantColors.Outline else Color(0xFFB91C1C)
                    )
                }
            }

            // Concentric Master ON/OFF Hero Control
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(224.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Concentric Ring
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .border(width = 10.dp, color = outerRingColor, shape = CircleShape)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Interactive Circular Core Puck
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .shadow(
                                    elevation = if (isActive) 8.dp else 4.dp,
                                    shape = CircleShape,
                                    ambientColor = if (isActive) VerdantColors.EmeraldRingGlow else VerdantColors.InactiveOuterBorder,
                                    spotColor = if (isActive) VerdantColors.EmeraldRingGlow else VerdantColors.InactiveOuterBorder
                                )
                                .clip(CircleShape)
                                .background(
                                    if (isActive) coreColor else Color.White
                                )
                                .then(
                                    if (!isActive) {
                                        Modifier
                                            .border(3.dp, VerdantColors.InactiveOuterBorder, CircleShape)
                                            .padding(8.dp)
                                            .clip(CircleShape)
                                            .background(coreColor)
                                            .border(2.dp, VerdantColors.InactiveInnerBorder, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable(
                                    role = Role.Button,
                                    onClickLabel = if (isActive) "Disable protection" else "Enable protection",
                                    onClick = onToggleProtection
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isActive) "ON" else "OFF",
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = if (isActive) 42.sp else 36.sp,
                                letterSpacing = (-0.8).sp,
                                color = coreTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isActive) "Protection enabled" else "Protection disabled",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isActive) VerdantColors.DashboardHeadingInk else VerdantColors.InactiveCrimson
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (!isActive) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = VerdantColors.InactiveRedAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(
                        text = if (isActive) {
                            if (armedCount < 3) {
                                "$armedCount of 3 modules active • Tap to pause"
                            } else {
                                "Tap to turn off"
                            }
                        } else {
                            "Tap On/Off button to resume real-time protection"
                        },
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.Normal else FontWeight.Medium,
                        color = if (isActive) VerdantColors.Outline else VerdantColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Device Vulnerable Alert Card (Visible when Protection is OFF)
            AnimatedVisibility(
                visible = !isActive,
                enter = fadeIn(tween(200)) + expandVertically(tween(220)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(200))
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = VerdantColors.AlertCardBg,
                    border = BorderStroke(1.dp, VerdantColors.AlertCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = VerdantColors.AlertIconRose,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DEVICE VULNERABLE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.6.sp,
                                    color = VerdantColors.AlertTitleRose
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = VerdantColors.AlertBadgeBg
                                ) {
                                    Text(
                                        text = "ALERT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = VerdantColors.AlertBadgeInk,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Real-time fraud heuristics, SMS smishing filters, and suspicious call detection are paused.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = VerdantColors.AlertBodyInk
                            )
                        }
                    }
                }
            }

            // Something Look Suspicious? Action CTA
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Something look suspicious?",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = VerdantColors.DashboardHeadingInk
                )

                Surface(
                    onClick = onOpenInspector,
                    shape = RoundedCornerShape(28.dp),
                    color = ctaBackground,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Link,
                                contentDescription = null,
                                tint = VerdantColors.LimeActionInk,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Check a link or message",
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = VerdantColors.LimeActionInk,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Open Payload Inspector",
                            tint = VerdantColors.LimeActionInk,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Recent Interceptions Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Interceptions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = VerdantColors.OnSurface
                    )
                    Text(
                        text = "VIEW FULL LOG",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.6.sp,
                        color = VerdantColors.Primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenFullLog(null) }
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                    )
                }

                uiState.interceptions.take(2).forEach { event ->
                    RecentInterceptionCard(
                        event = event,
                        isShieldActive = isActive,
                        onClick = { onOpenFullLog(event.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentInterceptionCard(
    event: InterceptionEvent,
    isShieldActive: Boolean,
    onClick: () -> Unit
) {
    val isBlocked = event.status == InterceptionStatus.BLOCKED

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = VerdantColors.SurfaceContainerLowest,
        border = BorderStroke(1.dp, VerdantColors.SurfaceContainerHigh),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
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
                        .background(
                            if (isBlocked) VerdantColors.InactiveChipBg else VerdantColors.SecondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBlocked) {
                            Icons.Outlined.GppBad
                        } else {
                            Icons.Outlined.SecurityUpdateGood
                        },
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = VerdantColors.OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                isBlocked && isShieldActive -> VerdantColors.Error
                                isBlocked -> VerdantColors.InactiveChipBg
                                else -> VerdantColors.SecondaryContainer
                            }
                        ) {
                            Text(
                                text = if (isBlocked) "BLOCKED" else "CLEARED",
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = when {
                                    isBlocked && isShieldActive -> Color.White
                                    isBlocked -> Color(0xFFB91C1C)
                                    else -> VerdantColors.OnSecondaryFixedVariant
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.summary,
                        fontSize = 12.sp,
                        color = VerdantColors.Outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = event.relativeTime,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = VerdantColors.Outline
            )
        }
    }
}
