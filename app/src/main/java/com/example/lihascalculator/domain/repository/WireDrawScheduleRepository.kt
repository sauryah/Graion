package com.example.lihascalculator.domain.repository

import com.example.lihascalculator.domain.model.wiredrawing.SavedSchedule
import kotlinx.coroutines.flow.Flow

interface WireDrawScheduleRepository {
    fun getSchedules(): Flow<List<SavedSchedule>>
    suspend fun saveSchedule(name: String, dies: List<Double>): Long
    suspend fun deleteSchedule(id: Long)
    suspend fun clearAllSchedules()
}
