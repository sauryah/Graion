package com.example.lihascalculator.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Brand Accents
val PrimaryViolet = Color(0xFF7C3AED)
val PrimaryVioletLight = Color(0xFF8B5CF6)
val PrimaryVioletDark = Color(0xFF6D28D9)
val LavenderAccent = Color(0xFFC4B5FD)
val LavenderSoft = Color(0xFFEDE9FE)

// Dark Theme Colors (Obsidian & Electric Violet)
val DarkBackground = Color(0xFF0E1017)
val DarkSurface = Color(0xFF161922)
val DarkSurfaceVariant = Color(0xFF202534)
val DarkDisplayBackground = Color(0xFF0E1017)
val DarkNumberButton = Color(0xFF1C202C)
val DarkNumberText = Color(0xFFF8FAFC)
val DarkFunctionButton = Color(0xFF282E40)
val DarkFunctionText = Color(0xFFC7D2FE)
val DarkOperatorButton = Color(0xFF3B2C63)
val DarkOperatorText = Color(0xFFE0D4FC)
val DarkEqualsButton = Color(0xFF7C3AED)
val DarkEqualsText = Color(0xFFFFFFFF)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextPreview = Color(0xFF64748B)
val DarkError = Color(0xFFF87171)

// Light Theme Colors (Porcelain & Royal Violet)
val LightBackground = Color(0xFFF6F8FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightDisplayBackground = Color(0xFFF6F8FB)
val LightNumberButton = Color(0xFFFFFFFF)
val LightNumberText = Color(0xFF0F172A)
val LightFunctionButton = Color(0xFFE2E8F0)
val LightFunctionText = Color(0xFF334155)
val LightOperatorButton = Color(0xFFEDE9FE)
val LightOperatorText = Color(0xFF6D28D9)
val LightEqualsButton = Color(0xFF7C3AED)
val LightEqualsText = Color(0xFFFFFFFF)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
val LightTextPreview = Color(0xFF94A3B8)
val LightError = Color(0xFFDC2626)

@Immutable
data class CalculatorColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val displayBackground: Color,
    val numberButton: Color,
    val numberText: Color,
    val functionButton: Color,
    val functionText: Color,
    val operatorButton: Color,
    val operatorText: Color,
    val equalsButton: Color,
    val equalsText: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textPreview: Color,
    val error: Color,
    val isDark: Boolean,
    val accentPrimary: Color = PrimaryViolet
)

val LocalCalculatorColors = staticCompositionLocalOf {
    CalculatorColors(
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
}

