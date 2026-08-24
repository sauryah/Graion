package com.sauryah.graion.domain.model

enum class AngleMode {
    DEGREES,
    RADIANS
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val angleMode: AngleMode = AngleMode.DEGREES
)

