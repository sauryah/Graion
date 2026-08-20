package com.sauryah.lihas.calculator.domain.repository

import com.sauryah.lihas.calculator.domain.model.wiredrawing.SavedSchedule
import kotlinx.coroutines.flow.Flow

interface WireDrawScheduleRepository {
    fun getSchedules(): Flow<List<SavedSchedule>>
    suspend fun saveSchedule(name: String, dies: List<Double>): Long
    suspend fun deleteSchedule(id: Long)
    suspend fun clearAllSchedules()
}
