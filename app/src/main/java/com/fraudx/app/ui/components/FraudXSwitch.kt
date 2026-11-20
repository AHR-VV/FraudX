package com.fraudx.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.fraudx.app.theme.VerdantColors

/**
 * Custom FraudX tactile switch matching the Verdant Shield Settings screenshot:
 * 48x28dp pill track with pure white sliding thumb showing a crisp Check or Close glyph,
 * centered inside a 48dp minimum accessibility touch hitbox.
 */
@Composable
fun FraudXSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) VerdantColors.PrimaryContainer else VerdantColors.SurfaceDim,
        animationSpec = tween(durationMillis = 180),
        label = "switch_track"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = tween(durationMillis = 180),
        label = "switch_thumb_offset"
    )

    Box(
        modifier = modifier
            .size(width = 52.dp, height = 48.dp)
            .clickable(
                role = Role.Switch,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 28.dp)
                .clip(CircleShape)
                .background(trackColor)
                .padding(2.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(24.dp)
                    .shadow(elevation = 2.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (checked) Icons.Rounded.Check else Icons.Rounded.Close,
                    contentDescription = null,
                    tint = if (checked) VerdantColors.Primary else VerdantColors.Outline,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
