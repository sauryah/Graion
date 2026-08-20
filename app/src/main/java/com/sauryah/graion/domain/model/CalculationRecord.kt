package com.sauryah.graion.domain.model

data class CalculationRecord(
    val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
