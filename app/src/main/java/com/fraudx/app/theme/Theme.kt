package com.fraudx.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val VerdantLightColorScheme = lightColorScheme(
    primary = VerdantColors.Primary,
    onPrimary = VerdantColors.OnPrimary,
    primaryContainer = VerdantColors.PrimaryContainer,
    onPrimaryContainer = VerdantColors.OnPrimaryContainer,
    secondary = VerdantColors.Secondary,
    onSecondary = VerdantColors.OnSecondary,
    secondaryContainer = VerdantColors.SecondaryContainer,
    onSecondaryContainer = VerdantColors.OnSecondaryContainer,
    tertiary = VerdantColors.Tertiary,
    onTertiary = VerdantColors.OnTertiary,
    tertiaryContainer = VerdantColors.TertiaryContainer,
    onTertiaryContainer = VerdantColors.OnTertiaryContainer,
    background = VerdantColors.Surface,
    onBackground = VerdantColors.OnSurface,
    surface = VerdantColors.Surface,
    onSurface = VerdantColors.OnSurface,
    surfaceVariant = VerdantColors.SurfaceContainerHighest,
    onSurfaceVariant = VerdantColors.OnSurfaceVariant,
    surfaceContainerLowest = VerdantColors.SurfaceContainerLowest,
    surfaceContainerLow = VerdantColors.SurfaceContainerLow,
    surfaceContainer = VerdantColors.SurfaceContainer,
    surfaceContainerHigh = VerdantColors.SurfaceContainerHigh,
    surfaceContainerHighest = VerdantColors.SurfaceContainerHighest,
    outline = VerdantColors.Outline,
    outlineVariant = VerdantColors.OutlineVariant,
    error = VerdantColors.Error,
    onError = VerdantColors.OnError,
    errorContainer = VerdantColors.ErrorContainer,
    onErrorContainer = VerdantColors.OnErrorContainer
)

private val VerdantShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun VerdantShieldTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VerdantLightColorScheme,
        typography = VerdantTypography,
        shapes = VerdantShapes,
        content = content
    )
}
