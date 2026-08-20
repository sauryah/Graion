package com.sauryah.lihas.calculator.ui.wiredrawing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sauryah.lihas.calculator.data.local.CalculatorDatabase
import com.sauryah.lihas.calculator.data.repository.WireDrawScheduleRepositoryImpl

class WireDrawingViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WireDrawingViewModel::class.java)) {
            val database = CalculatorDatabase.getInstance(context)
            val scheduleRepository = WireDrawScheduleRepositoryImpl(database.wireDrawScheduleDao())
            return WireDrawingViewModel(scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
