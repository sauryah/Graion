package com.example.lihascalculator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WireDrawScheduleDao {

    @Query("SELECT * FROM wire_draw_schedules ORDER BY timestamp DESC")
    fun getAllSchedules(): Flow<List<WireDrawScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: WireDrawScheduleEntity): Long

    @Query("DELETE FROM wire_draw_schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Long)

    @Query("DELETE FROM wire_draw_schedules")
    suspend fun clearAllSchedules()
}
