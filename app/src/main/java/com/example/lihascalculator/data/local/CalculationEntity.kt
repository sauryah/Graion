package com.sauryah.lihas.calculator.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sauryah.lihas.calculator.domain.model.CalculationRecord

@Entity(
    tableName = "calculations",
    indices = [Index(value = ["timestamp"])]
)
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun CalculationEntity.toDomainModel(): CalculationRecord {
    return CalculationRecord(
        id = id,
        expression = expression,
        result = result,
        timestamp = timestamp
    )
}

fun CalculationRecord.toEntity(): CalculationEntity {
    return CalculationEntity(
        id = id,
        expression = expression,
        result = result,
        timestamp = timestamp
    )
}
