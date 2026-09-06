package com.beperfectsalon.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = WinePrimary,
    onPrimary = LightText,
    secondary = RoseGold,
    onSecondary = CharcoalText,
    background = CreamBackground,
    onBackground = CharcoalText,
    surface = CreamBackground,
    onSurface = CharcoalText,
    error = ErrorRed,
)

private val DarkColors = darkColorScheme(
    primary = WinePrimaryDark,
    onPrimary = LightText,
    secondary = RoseGold,
    onSecondary = CharcoalText,
    background = DarkBackground,
    onBackground = LightText,
    surface = DarkBackground,
    onSurface = LightText,
    error = ErrorRed,
)

@Composable
fun BePerfectSalonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = SalonTypography,
        content = content,
    )
}
