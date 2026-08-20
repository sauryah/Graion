package com.sauryah.graion.ui.calculator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sauryah.graion.data.local.CalculatorDatabase
import com.sauryah.graion.data.preferences.PreferencesRepository
import com.sauryah.graion.data.repository.HistoryRepositoryImpl
import com.sauryah.graion.domain.engine.CalculatorEngine

class CalculatorViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            val database = CalculatorDatabase.getInstance(context)
            val historyRepository = HistoryRepositoryImpl(database.calculationDao())
            val preferencesRepository = PreferencesRepository(context)
            val engine = CalculatorEngine()

            return CalculatorViewModel(
                engine = engine,
                historyRepository = historyRepository,
                settingsRepository = preferencesRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
