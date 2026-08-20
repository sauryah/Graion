package com.sauryah.lihas.calculator.domain.model

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = false
)
