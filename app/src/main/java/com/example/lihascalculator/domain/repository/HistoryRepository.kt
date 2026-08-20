package com.sauryah.lihas.calculator.domain.repository

import com.sauryah.lihas.calculator.domain.model.CalculationRecord
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(): Flow<List<CalculationRecord>>
    suspend fun saveCalculation(expression: String, result: String): Long
    suspend fun deleteCalculation(id: Long)
    suspend fun clearHistory()
}
