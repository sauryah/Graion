package com.example.lihascalculator.domain.repository

import com.example.lihascalculator.domain.model.CalculationRecord
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(): Flow<List<CalculationRecord>>
    suspend fun saveCalculation(expression: String, result: String): Long
    suspend fun deleteCalculation(id: Long)
    suspend fun clearHistory()
}
