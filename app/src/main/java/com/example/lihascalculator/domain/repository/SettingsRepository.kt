package com.example.lihascalculator.domain.repository

import com.example.lihascalculator.domain.model.ThemeMode
import com.example.lihascalculator.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setSoundEnabled(enabled: Boolean)
}
