package com.sauryah.graion.domain.repository

import com.sauryah.graion.domain.model.AngleMode
import com.sauryah.graion.domain.model.ThemeMode
import com.sauryah.graion.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setAngleMode(mode: AngleMode)
}

