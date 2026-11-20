package com.fraudx.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddModerator
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PhoneDisabled
import androidx.compose.material.icons.outlined.PhonelinkErase
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.PublicOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fraudx.app.model.FraudXUiState
import com.fraudx.app.model.ManualRule
import com.fraudx.app.model.RuleAction
import com.fraudx.app.model.RuleCategory
import com.fraudx.app.theme.JetBrainsMonoFamily
import com.fraudx.app.theme.PlusJakartaSansFamily
import com.fraudx.app.theme.VerdantColors
import com.fraudx.app.ui.components.FraudXTopBar

@Composable
fun AllowBlockScreen(
    uiState: FraudXUiState,
    onNavigateBack: () -> Unit,
    onAddRule: (String, RuleAction, RuleCategory) -> Unit,
    onToggleRuleAction: (String) -> Unit,
    onDeleteRule: (String) -> Unit,
    onOpenAuditLog: () -> Unit,
    onNotify: (String) -> Unit
) {
    var activeTab by rememberSaveable { mutableStateOf(RuleAction.ALLOWED) }
    var newTarget by rememberSaveable { mutableStateOf("") }
    var addActionMode by rememberSaveable { mutableStateOf(RuleAction.ALLOWED) }
    var addCategory by rememberSaveable { mutableStateOf(RuleCategory.WEBSITE) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilterCategory by rememberSaveable { mutableStateOf<RuleCategory?>(null) }

    val filteredRules = remember(uiState.rules, activeTab, selectedFilterCategory, searchQuery) {
        val q = searchQuery.trim().lowercase()
        uiState.rules.filter { rule ->
            rule.action == activeTab &&
                (selectedFilterCategory == null || rule.category == selectedFilterCategory) &&
                (q.isEmpty() ||
                    rule.target.lowercase().contains(q) ||
                    rule.description.lowercase().contains(q) ||
                    rule.tag.lowercase().contains(q))
        }
    }

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
                .padding(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Segmented Master Tabs: Allowed vs Blocked
            Surface(
                shape = CircleShape,
                color = VerdantColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, VerdantColors.SurfaceContainerHigh),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SegmentedRuleTab(
                        label = "Allowed",
                        count = uiState.allowedCount,
                        selected = activeTab == RuleAction.ALLOWED,
                        isBlockedStyle = false,
                        onClick = { activeTab = RuleAction.ALLOWED },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentedRuleTab(
                        label = "Blocked",
                        count = uiState.blockedCount,
                        selected = activeTab == RuleAction.BLOCKED,
                        isBlockedStyle = true,
                        onClick = { activeTab = RuleAction.BLOCKED },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Inject New Rule Card (L3-Filter)
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = VerdantColors.SurfaceContainerLowest,
                border = BorderStroke(1.dp, VerdantColors.OutlineVariant.copy(alpha = 0.45f)),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddModerator,
                                contentDescription = null,
                                tint = VerdantColors.Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Inject New Rule",
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = VerdantColors.OnSurface
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VerdantColors.SurfaceContainerLow,
                            border = BorderStroke(1.dp, VerdantColors.SurfaceContainerHigh)
                        ) {
                            Text(
                                text = "L3-FILTER",
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = VerdantColors.OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTarget,
                            onValueChange = { newTarget = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Terminal,
                                    contentDescription = null,
                                    tint = VerdantColors.Outline,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "e.g. domain.com, +1800...",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontSize = 13.sp,
                                    color = VerdantColors.Outline
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = VerdantColors.SurfaceContainerLowest,
                                unfocusedContainerColor = VerdantColors.SurfaceContainerLow.copy(alpha = 0.7f),
                                focusedBorderColor = VerdantColors.Primary,
                                unfocusedBorderColor = VerdantColors.OutlineVariant.copy(alpha = 0.6f)
                            )
                        )

                        Button(
                            onClick = {
                                if (newTarget.isNotBlank()) {
                                    onAddRule(newTarget, addActionMode, addCategory)
                                    activeTab = addActionMode
                                    newTarget = ""
                                }
                            },
                            enabled = newTarget.isNotBlank(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VerdantColors.PrimaryContainer,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add",
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Scope & Action Quick Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val isAllowAdd = addActionMode == RuleAction.ALLOWED
                            Surface(
                                onClick = {
                                    addActionMode = if (isAllowAdd) RuleAction.BLOCKED else RuleAction.ALLOWED
                                },
                                shape = CircleShape,
                                color = if (isAllowAdd) VerdantColors.SecondaryContainer else VerdantColors.ErrorContainer,
                                border = BorderStroke(
                                    1.dp,
                                    if (isAllowAdd) VerdantColors.SecondaryFixedDim else VerdantColors.ErrorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isAllowAdd) VerdantColors.Primary else VerdantColors.Error)
                                    )
                                    Text(
                                        text = if (isAllowAdd) "ALLOW" else "BLOCK",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isAllowAdd) VerdantColors.OnSecondaryFixedVariant else VerdantColors.OnErrorContainer
                                    )
                                }
                            }

                            Box {
                                Surface(
                                    onClick = { categoryDropdownExpanded = true },
                                    shape = CircleShape,
                                    color = VerdantColors.SurfaceContainer,
                                    border = BorderStroke(1.dp, VerdantColors.OutlineVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = addCategory.label,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 10.sp,
                                            color = VerdantColors.OnSurface
                                        )
                                        Icon(
                                            imageVector = Icons.Outlined.ExpandMore,
                                            contentDescription = "Category",
                                            tint = VerdantColors.OnSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false },
                                    containerColor = VerdantColors.SurfaceContainerLowest
                                ) {
                                    RuleCategory.entries.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.label, fontSize = 12.sp) },
                                            onClick = {
                                                addCategory = cat
                                                categoryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "AUTO-ACTIVE",
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = VerdantColors.OnSurfaceVariant
                        )
                    }
                }
            }

            // Search Input & Category Chip Carousel
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                    contentDescription = "Clear Search",
                                    tint = VerdantColors.OnSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search targets, tags, keywords...",
                            fontSize = 13.sp,
                            color = VerdantColors.Outline
                        )
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = VerdantColors.SurfaceContainerLowest,
                        unfocusedContainerColor = VerdantColors.SurfaceContainerLowest,
                        focusedBorderColor = VerdantColors.Primary,
                        unfocusedBorderColor = VerdantColors.OutlineVariant.copy(alpha = 0.6f)
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryFilterChip(
                        label = "All Records",
                        selected = selectedFilterCategory == null,
                        onClick = { selectedFilterCategory = null }
                    )
                    RuleCategory.entries.forEach { cat ->
                        CategoryFilterChip(
                            label = cat.filterChipLabel,
                            selected = selectedFilterCategory == cat,
                            onClick = { selectedFilterCategory = cat }
                        )
                    }
                }
            }

            // Active List Header & Audit Log Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (activeTab == RuleAction.ALLOWED) "Whitelist Perimeter" else "Blacklist Perimeter",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = VerdantColors.OnSurface
                    )
                    Surface(
                        shape = CircleShape,
                        color = VerdantColors.SurfaceContainerHigh
                    ) {
                        Text(
                            text = "Displaying ${filteredRules.size} items",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VerdantColors.OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onOpenAuditLog)
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = VerdantColors.OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "AUDIT LOG",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = VerdantColors.OnSurfaceVariant
                    )
                }
            }

            // Rule Cards or Empty State
            if (filteredRules.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = VerdantColors.SurfaceContainerLowest,
                    border = BorderStroke(1.dp, VerdantColors.OutlineVariant.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(VerdantColors.SurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SearchOff,
                                contentDescription = null,
                                tint = VerdantColors.OnSurfaceVariant,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = "No Rules Found",
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VerdantColors.OnSurface
                        )
                        Text(
                            text = "No custom overrides match your active search terms or category filter.",
                            fontSize = 12.sp,
                            color = VerdantColors.OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            onClick = {
                                searchQuery = ""
                                selectedFilterCategory = null
                            },
                            shape = CircleShape,
                            color = VerdantColors.AllowTabPillBg
                        ) {
                            Text(
                                text = "Clear Filters",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = VerdantColors.OnTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filteredRules.forEach { rule ->
                        ManualRuleCard(
                            rule = rule,
                            onFlipAction = { onToggleRuleAction(rule.id) },
                            onDelete = { onDeleteRule(rule.id) },
                            onCopiedTarget = { onNotify("Copied ${rule.target}") }
                        )
                    }
                }
            }

            // Bottom Notice Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = VerdantColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, VerdantColors.OutlineVariant.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Policy,
                        contentDescription = null,
                        tint = VerdantColors.Tertiary,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(20.dp)
                    )
                    Column {
                        Text(
                            text = "NOTICE",
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp,
                            color = VerdantColors.Tertiary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Manual allow exceptions supersede all other rules",
                            fontSize = 13.sp,
                            color = VerdantColors.OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedRuleTab(
    label: String,
    count: Int,
    selected: Boolean,
    isBlockedStyle: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        selected && !isBlockedStyle -> VerdantColors.AllowTabPillBg
        selected && isBlockedStyle -> VerdantColors.ErrorContainer
        else -> Color.Transparent
    }
    val textColor = when {
        selected && !isBlockedStyle -> VerdantColors.OnTertiaryContainer
        selected && isBlockedStyle -> VerdantColors.OnErrorContainer
        else -> VerdantColors.OnSurfaceVariant
    }

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        modifier = modifier.height(44.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = if (isBlockedStyle) Icons.Outlined.GppBad else Icons.Outlined.VerifiedUser,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 14.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = CircleShape,
                color = if (selected) Color.White else VerdantColors.SurfaceContainerHighest
            ) {
                Text(
                    text = count.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isBlockedStyle) VerdantColors.Error else VerdantColors.Primary,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterChip(
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
            fontFamily = PlusJakartaSansFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            color = if (selected) VerdantColors.OnTertiaryContainer else VerdantColors.OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ManualRuleCard(
    rule: ManualRule,
    onFlipAction: () -> Unit,
    onDelete: () -> Unit,
    onCopiedTarget: () -> Unit
) {
    val isAllowed = rule.action == RuleAction.ALLOWED
    var overflowExpanded by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    val icon = when {
        isAllowed && rule.category == RuleCategory.WEBSITE -> Icons.Outlined.Language
        isAllowed && rule.category == RuleCategory.PHONE -> Icons.Outlined.Call
        isAllowed -> Icons.Outlined.Sms
        rule.category == RuleCategory.WEBSITE -> Icons.Outlined.PublicOff
        rule.category == RuleCategory.PHONE -> Icons.Outlined.PhoneDisabled
        else -> Icons.Outlined.PhonelinkErase
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = VerdantColors.SurfaceContainerLowest,
        border = BorderStroke(
            1.dp,
            if (isAllowed) VerdantColors.OutlineVariant.copy(alpha = 0.45f) else VerdantColors.ErrorContainer
        ),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isAllowed) VerdantColors.SecondaryContainer.copy(alpha = 0.75f) else VerdantColors.ErrorContainer.copy(alpha = 0.75f),
                        border = BorderStroke(
                            1.dp,
                            if (isAllowed) VerdantColors.SecondaryFixedDim.copy(alpha = 0.5f) else VerdantColors.ErrorContainer
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isAllowed) VerdantColors.Primary else VerdantColors.Error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = rule.target,
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = VerdantColors.OnSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Surface(
                                shape = CircleShape,
                                color = if (isAllowed) VerdantColors.AllowTabPillBg else VerdantColors.ErrorContainer
                            ) {
                                Text(
                                    text = rule.badgeLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isAllowed) VerdantColors.OnTertiaryContainer else VerdantColors.OnErrorContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rule.description,
                            fontSize = 13.sp,
                            color = if (isAllowed) VerdantColors.OnSurfaceVariant else VerdantColors.Error
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { overflowExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "Rule actions",
                            tint = VerdantColors.OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = overflowExpanded,
                        onDismissRequest = { overflowExpanded = false },
                        containerColor = VerdantColors.SurfaceContainerLowest
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(if (isAllowed) "Move to Blocked" else "Move to Allowed")
                            },
                            onClick = {
                                overflowExpanded = false
                                onFlipAction()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Copy Target") },
                            onClick = {
                                overflowExpanded = false
                                clipboard.setText(AnnotatedString(rule.target))
                                onCopiedTarget()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Rule", color = VerdantColors.Error) },
                            onClick = {
                                overflowExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = if (isAllowed) VerdantColors.SurfaceContainerHigh else VerdantColors.ErrorContainer.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isAllowed) VerdantColors.SurfaceContainerLow.copy(alpha = 0.55f)
                        else VerdantColors.ErrorContainer.copy(alpha = 0.14f)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${rule.tag}  •  ${rule.createdAtLabel}",
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 10.sp,
                    color = if (!isAllowed && rule.tag.startsWith("TRIGGER")) VerdantColors.Error else VerdantColors.Outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onDelete)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete rule",
                        tint = VerdantColors.OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "DELETE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = VerdantColors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}
