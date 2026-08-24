package com.sauryah.graion.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.sauryah.graion.domain.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryViolet,
    onPrimary = Color.White,
    primaryContainer = DarkOperatorButton,
    onPrimaryContainer = DarkOperatorText,
    secondary = LavenderAccent,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    error = DarkError,
    onError = Color.White
)

private val OledColorScheme = darkColorScheme(
    primary = PrimaryViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF281E44),
    onPrimaryContainer = DarkOperatorText,
    secondary = LavenderAccent,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = DarkTextPrimary,
    surface = Color(0xFF0A0C12),
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF141824),
    onSurfaceVariant = DarkTextSecondary,
    error = DarkError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryViolet,
    onPrimary = Color.White,
    primaryContainer = LightOperatorButton,
    onPrimaryContainer = LightOperatorText,
    secondary = PrimaryVioletLight,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = LightError,
    onError = Color.White
)

object CalculatorTheme {
    val colors: CalculatorColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCalculatorColors.current
}

@Composable
fun GraionTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.OLED_BLACK -> true
    }

    val colorScheme = when (themeMode) {
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.OLED_BLACK -> OledColorScheme
    }

    val calculatorColors = when (themeMode) {
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) DarkCalculatorColors else LightCalculatorColors
        ThemeMode.LIGHT -> LightCalculatorColors
        ThemeMode.DARK -> DarkCalculatorColors
        ThemeMode.OLED_BLACK -> OledBlackCalculatorColors
    }

    CompositionLocalProvider(LocalCalculatorColors provides calculatorColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
