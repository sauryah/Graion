package com.example.lihascalculator.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.example.lihascalculator.domain.model.ThemeMode

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

private val DarkCalculatorColors = CalculatorColors(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    displayBackground = DarkDisplayBackground,
    numberButton = DarkNumberButton,
    numberText = DarkNumberText,
    functionButton = DarkFunctionButton,
    functionText = DarkFunctionText,
    operatorButton = DarkOperatorButton,
    operatorText = DarkOperatorText,
    equalsButton = DarkEqualsButton,
    equalsText = DarkEqualsText,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textPreview = DarkTextPreview,
    error = DarkError,
    isDark = true
)

private val LightCalculatorColors = CalculatorColors(
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    displayBackground = LightDisplayBackground,
    numberButton = LightNumberButton,
    numberText = LightNumberText,
    functionButton = LightFunctionButton,
    functionText = LightFunctionText,
    operatorButton = LightOperatorButton,
    operatorText = LightOperatorText,
    equalsButton = LightEqualsButton,
    equalsText = LightEqualsText,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textPreview = LightTextPreview,
    error = LightError,
    isDark = false
)

object CalculatorTheme {
    val colors: CalculatorColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCalculatorColors.current
}

@Composable
fun LihasCalculatorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val calculatorColors = if (isDark) DarkCalculatorColors else LightCalculatorColors

    CompositionLocalProvider(LocalCalculatorColors provides calculatorColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
