package com.sauryah.graion.domain.model.wiredrawing

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

enum class WireMaterial(val displayName: String, val densityGPerCm3: Double) {
    COPPER("Copper (Cu - ETP/OFHC)", 8.96),
    ALUMINUM_EC("EC Aluminum (Al 99.5%)", 2.70),
    ALUMINUM_ALLOY("Aluminum Alloy (6xxx/5xxx)", 2.68),
    CARBON_STEEL_HIGH("High Carbon Steel", 7.85),
    STAINLESS_STEEL_304("Stainless Steel 304/316", 7.93),
    BRASS_70_30("Brass (70/30)", 8.53),
    BRONZE_PHOSPHOR("Phosphor Bronze", 8.86),
    NICKEL_200("Pure Nickel 200", 8.89),
    TITANIUM_GRADE_2("Titanium Grade 2", 4.51),
    GOLD("Gold (Au)", 19.32),
    SILVER("Silver (Ag)", 10.49)
}

@Serializable
data class WireWeightLengthResult(
    val diameterMm: Double,
    val lengthMetres: Double,
    val weightKg: Double,
    val linearMassGPerM: Double,
    val materialName: String
)

@Serializable
data class DieGeometryResult(
    val optimalApproachAngleDeg: Double,
    val recommendedBearingLengthMm: Double,
    val bearingLengthRatioPercent: Double,
    val frictionCoefficient: Double,
    val deltaParameter: Double,
    val deformationQuality: String
)


