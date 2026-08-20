package com.sauryah.lihas.calculator.domain.model.wiredrawing

import kotlinx.serialization.Serializable

@Serializable
data class PassResult(
    val passNumber: Int,
    val fromDie: Double,
    val toDie: Double,
    val areaBefore: Double,
    val areaAfter: Double,
    val areaReductionPercent: Double,
    val elongationPercent: Double,
    val reductionRatio: Double
)

@Serializable
data class WireDrawingStats(
    val totalPasses: Int,
    val startingDie: Double,
    val finalDie: Double,
    val avgElongationPercent: Double,
    val maxElongationPercent: Double,
    val minElongationPercent: Double,
    val avgAreaReductionPercent: Double,
    val overallAreaReductionPercent: Double,
    val overallReductionRatio: Double
)

enum class QualityRating(val title: String, val stars: Int) {
    EXCELLENT("Excellent", 5),
    VERY_GOOD("Very Good", 4),
    GOOD("Good", 3),
    FAIR("Fair", 2),
    POOR("Poor", 1),
    NOT_APPLICABLE("N/A", 0)
}

@Serializable
data class ConsistencyResult(
    val avgElongation: Double,
    val maxDeviation: Double,
    val rating: QualityRating,
    val stars: Int
)

@Serializable
data class SuggestedIntermediatePass(
    val passIndex: Int,
    val fromDie: Double,
    val toDie: Double,
    val currentElongation: Double,
    val proposedDies: List<Double> // Includes fromDie, intermediate dies, toDie
)

@Serializable
data class TargetCheckResult(
    val passNumber: Int,
    val fromDie: Double,
    val toDie: Double,
    val elongation: Double,
    val targetMin: Double,
    val targetMax: Double,
    val isOutOfRange: Boolean
)

@Serializable
data class SavedSchedule(
    val id: Long = 0,
    val name: String,
    val dies: List<Double>,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class CadDieSpec(
    val dieType: String = "ROUND",
    val dieId: String,
    val punchedSize: Double,
    val currentSize: Double,
    val inletSize: Double,
    val status: String = "RUNNING",
    val casing: String = "Standard Carbide",
    val reductionAngleDeg: Double = 14.0,
    val bearingLengthPercent: Double = 35.0,
    val backReliefAngleDeg: Double = 30.0
)
