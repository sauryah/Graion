package com.sauryah.graion.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sauryah.graion.domain.model.wiredrawing.SavedSchedule

@Entity(
    tableName = "wire_draw_schedules",
    indices = [Index(value = ["timestamp"])]
)
data class WireDrawScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val diesCsv: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun WireDrawScheduleEntity.toDomainModel(): SavedSchedule {
    val dies = diesCsv.split(",").mapNotNull { it.trim().toDoubleOrNull() }
    return SavedSchedule(
        id = id,
        name = name,
        dies = dies,
        timestamp = timestamp
    )
}

fun SavedSchedule.toEntity(): WireDrawScheduleEntity {
    return WireDrawScheduleEntity(
        id = id,
        name = name,
        diesCsv = dies.joinToString(","),
        timestamp = timestamp
    )
}
