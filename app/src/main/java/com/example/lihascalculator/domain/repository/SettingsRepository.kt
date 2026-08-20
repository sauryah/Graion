package com.sauryah.lihas.calculator.domain.repository

import com.sauryah.lihas.calculator.domain.model.ThemeMode
import com.sauryah.lihas.calculator.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setSoundEnabled(enabled: Boolean)
}
