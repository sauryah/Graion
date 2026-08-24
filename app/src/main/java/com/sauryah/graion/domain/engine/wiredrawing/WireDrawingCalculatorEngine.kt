package com.sauryah.graion.domain.engine.wiredrawing

import com.sauryah.graion.domain.model.wiredrawing.ConsistencyResult
import com.sauryah.graion.domain.model.wiredrawing.PassResult
import com.sauryah.graion.domain.model.wiredrawing.QualityRating
import com.sauryah.graion.domain.model.wiredrawing.SuggestedIntermediatePass
import com.sauryah.graion.domain.model.wiredrawing.TargetCheckResult
import com.sauryah.graion.domain.model.wiredrawing.WireDrawingStats
import com.sauryah.graion.domain.model.wiredrawing.WireMaterial
import com.sauryah.graion.domain.model.wiredrawing.WireWeightLengthResult
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

object WireDrawingCalculatorEngine {

    val DEFAULT_EXAMPLE_DIES: List<Double> = listOf(
        2.490, 2.217, 1.974, 1.757, 1.564, 1.392, 1.239,
        1.103, 0.982, 0.874, 0.778, 0.693, 0.617, 0.550,
        0.490, 0.437, 0.389, 0.347, 0.309
    )

    fun roundTo3(value: Double): Double {
        if (value.isNaN() || value.isInfinite()) return 0.0
        return BigDecimal(value.toString())
            .setScale(3, RoundingMode.HALF_UP)
            .toDouble()
    }

    /**
     * Parses raw input text with separators (spaces, tabs, commas, semicolons, pipes, newlines),
     * stripping smart quotes and validating numeric > 0 values.
     */
    fun parseInputText(rawText: String): Pair<List<Double>, List<String>> {
        val sanitized = rawText
            .replace("“", "")
            .replace("”", "")
            .replace("‘", "")
            .replace("’", "")
            .replace("\"", "")
            .replace("'", "")
            .replace("[", "")
            .replace("]", "")
            .trim()

        if (sanitized.isEmpty()) {
            return Pair(emptyList(), emptyList())
        }

        val tokens = sanitized.split(Regex("[\\s,;|\\n\\t]+")).filter { it.isNotBlank() }
        val validDies = mutableListOf<Double>()
        val errors = mutableListOf<String>()

        for (token in tokens) {
            val num = token.toDoubleOrNull()
            if (num == null) {
                errors.add("Invalid numeric value: '$token'")
            } else if (num <= 0) {
                errors.add("Diameter must be positive (> 0): '$token'")
            } else {
                validDies.add(roundTo3(num))
            }
        }

        return Pair(validDies, errors)
    }

    /**
     * Calculates area = π * d² / 4
     */
    fun calculateArea(diameter: Double): Double {
        if (diameter <= 0.0) return 0.0
        return roundTo3(PI * diameter * diameter / 4.0)
    }

    /**
     * Calculates elongation = (d_before² / d_after² - 1) * 100
     */
    fun calculateElongation(dBefore: Double, dAfter: Double): Double {
        if (dBefore <= 0.0 || dAfter <= 0.0) return 0.0
        val ratio = (dBefore * dBefore) / (dAfter * dAfter)
        return roundTo3((ratio - 1.0) * 100.0)
    }

    /**
     * Calculates areaReduction = (1 - d_after² / d_before²) * 100
     */
    fun calculateAreaReduction(dBefore: Double, dAfter: Double): Double {
        if (dBefore <= 0.0 || dAfter <= 0.0) return 0.0
        val ratio = (dAfter * dAfter) / (dBefore * dBefore)
        return roundTo3((1.0 - ratio) * 100.0)
    }

    /**
     * Calculates reductionRatio = d_before² / d_after²
     */
    fun calculateReductionRatio(dBefore: Double, dAfter: Double): Double {
        if (dBefore <= 0.0 || dAfter <= 0.0) return 0.0
        return roundTo3((dBefore * dBefore) / (dAfter * dAfter))
    }

    /**
     * Inverse formula: d_after from elongation
     * d_after = d_before / sqrt(1 + elongation / 100)
     * Edge case: elongation <= -100 -> die unchanged
     */
    fun calculateDiameterFromElongation(dBefore: Double, elongationPercent: Double): Double {
        if (elongationPercent <= -100.0) return dBefore
        val denom = sqrt(1.0 + elongationPercent / 100.0)
        if (denom == 0.0) return dBefore
        return roundTo3(dBefore / denom)
    }

    /**
     * Inverse formula: d_after from area reduction
     * d_after = d_before * sqrt(1 - reduction / 100)
     * Edge case: reduction >= 100 -> die = 0
     */
    fun calculateDiameterFromAreaReduction(dBefore: Double, reductionPercent: Double): Double {
        if (reductionPercent >= 100.0) return 0.0
        if (reductionPercent < 0.0) return dBefore
        return roundTo3(dBefore * sqrt(1.0 - reductionPercent / 100.0))
    }

    /**
     * Inverse formula: d_after from reduction ratio
     * d_after = d_before / sqrt(ratio)
     * Edge case: ratio <= 0 -> die unchanged
     */
    fun calculateDiameterFromRatio(dBefore: Double, ratio: Double): Double {
        if (ratio <= 0.0) return dBefore
        return roundTo3(dBefore / sqrt(ratio))
    }

    /**
     * Calculates pass table from sequence of dies.
     */
    fun calculatePasses(dies: List<Double>): List<PassResult> {
        if (dies.size < 2) return emptyList()

        val passes = mutableListOf<PassResult>()
        for (i in 0 until dies.size - 1) {
            val from = dies[i]
            val to = dies[i + 1]

            val areaBefore = calculateArea(from)
            val areaAfter = calculateArea(to)
            val areaReduction = calculateAreaReduction(from, to)
            val elongation = calculateElongation(from, to)
            val ratio = calculateReductionRatio(from, to)

            passes.add(
                PassResult(
                    passNumber = i + 1,
                    fromDie = from,
                    toDie = to,
                    areaBefore = areaBefore,
                    areaAfter = areaAfter,
                    areaReductionPercent = areaReduction,
                    elongationPercent = elongation,
                    reductionRatio = ratio
                )
            )
        }
        return passes
    }

    /**
     * Calculates comprehensive statistics from passes and dies.
     */
    fun calculateStatistics(dies: List<Double>, passes: List<PassResult>): WireDrawingStats {
        if (dies.size < 2 || passes.isEmpty()) {
            return WireDrawingStats(
                totalPasses = 0,
                startingDie = dies.firstOrNull() ?: 0.0,
                finalDie = dies.lastOrNull() ?: 0.0,
                avgElongationPercent = 0.0,
                maxElongationPercent = 0.0,
                minElongationPercent = 0.0,
                avgAreaReductionPercent = 0.0,
                overallAreaReductionPercent = 0.0,
                overallReductionRatio = 0.0
            )
        }

        val start = dies.first()
        val end = dies.last()

        val elongations = passes.map { it.elongationPercent }
        val reductions = passes.map { it.areaReductionPercent }

        val avgElongation = roundTo3(elongations.average())
        val maxElongation = roundTo3(elongations.maxOrNull() ?: 0.0)
        val minElongation = roundTo3(elongations.minOrNull() ?: 0.0)

        val avgReduction = roundTo3(reductions.average())
        val overallReduction = calculateAreaReduction(start, end)
        val overallRatio = calculateReductionRatio(start, end)

        return WireDrawingStats(
            totalPasses = passes.size,
            startingDie = start,
            finalDie = end,
            avgElongationPercent = avgElongation,
            maxElongationPercent = maxElongation,
            minElongationPercent = minElongation,
            avgAreaReductionPercent = avgReduction,
            overallAreaReductionPercent = overallReduction,
            overallReductionRatio = overallRatio
        )
    }

    /**
     * Calculates consistency rating based on maximum deviation from average elongation:
     * variation = max(|pass_elongation - avg_elongation|)
     * <= 1 -> 5 stars / Excellent
     * <= 2 -> 4 stars / Very Good
     * <= 3 -> 3 stars / Good
     * <= 5 -> 2 stars / Fair
     * > 5  -> 1 star / Poor
     * < 2 dies -> N/A, 0 stars
     */
    fun calculateConsistency(passes: List<PassResult>): ConsistencyResult {
        if (passes.isEmpty()) {
            return ConsistencyResult(
                avgElongation = 0.0,
                maxDeviation = 0.0,
                rating = QualityRating.NOT_APPLICABLE,
                stars = 0
            )
        }

        val avgElongation = passes.map { it.elongationPercent }.average()
        val maxDeviation = passes.maxOfOrNull { abs(it.elongationPercent - avgElongation) } ?: 0.0

        val (rating, stars) = when {
            maxDeviation <= 1.0 -> Pair(QualityRating.EXCELLENT, 5)
            maxDeviation <= 2.0 -> Pair(QualityRating.VERY_GOOD, 4)
            maxDeviation <= 3.0 -> Pair(QualityRating.GOOD, 3)
            maxDeviation <= 5.0 -> Pair(QualityRating.FAIR, 2)
            else -> Pair(QualityRating.POOR, 1)
        }

        return ConsistencyResult(
            avgElongation = roundTo3(avgElongation),
            maxDeviation = roundTo3(maxDeviation),
            rating = rating,
            stars = stars
        )
    }

    /**
     * Generates a constant-elongation die series:
     * pass count = ceil( ln(d_start / d_end) / ln(sqrt(1 + elongation / 100)) )
     * If final-pass range is supplied, attempts to adjust pass count.
     * Guaranteed to end at >= d_end.
     */
    fun generateDieSeries(
        dStart: Double,
        dEnd: Double,
        targetElongation: Double,
        finalPassMin: Double? = null,
        finalPassMax: Double? = null
    ): List<Double> {
        if (dStart <= 0.0 || dEnd <= 0.0 || dStart <= dEnd || targetElongation <= 0.0) {
            return listOf(dStart, dEnd)
        }

        val stepFactor = sqrt(1.0 + targetElongation / 100.0)
        if (stepFactor <= 1.0) {
            return listOf(dStart, dEnd)
        }

        val theoreticalPassCount = ceil(ln(dStart / dEnd) / ln(stepFactor)).toInt()
        var passCount = if (theoreticalPassCount < 1) 1 else theoreticalPassCount

        // If final pass range provided, test if adjusting pass count helps
        if (finalPassMin != null && finalPassMax != null && finalPassMin <= finalPassMax) {
            for (testCount in passCount downTo 1) {
                val ratio = (dEnd / dStart).pow(1.0 / testCount)
                val testLastPassElongation = (1.0 / (ratio * ratio) - 1.0) * 100.0
                if (testLastPassElongation in finalPassMin..finalPassMax) {
                    passCount = testCount
                    break
                }
            }
        }

        val result = mutableListOf<Double>()
        result.add(roundTo3(dStart))

        val geometricRatio = (dEnd / dStart).pow(1.0 / passCount)
        for (i in 1 until passCount) {
            val d = dStart * geometricRatio.pow(i.toDouble())
            result.add(roundTo3(d))
        }

        // Ensure final die is exactly dEnd
        result.add(roundTo3(dEnd))

        // Ensure strictly decreasing and >= dEnd
        return result.distinct().sortedDescending()
    }

    /**
     * Suggests intermediate geometric dies for any pass whose elongation differs
     * from the target elongation by more than 2%.
     */
    fun suggestIntermediateDies(
        dies: List<Double>,
        targetElongation: Double
    ): List<SuggestedIntermediatePass> {
        if (dies.size < 2 || targetElongation <= 0.0) return emptyList()

        val suggestions = mutableListOf<SuggestedIntermediatePass>()
        val stepRatioTarget = sqrt(1.0 + targetElongation / 100.0)

        for (i in 0 until dies.size - 1) {
            val from = dies[i]
            val to = dies[i + 1]
            val elongation = calculateElongation(from, to)

            if (abs(elongation - targetElongation) > 2.0 && from > to) {
                // Calculate how many sub-steps are needed
                val subPassCount = (ln(from / to) / ln(stepRatioTarget)).roundToInt().coerceAtLeast(2)
                val subStepRatio = (to / from).pow(1.0 / subPassCount)

                val intermediateList = mutableListOf<Double>()
                intermediateList.add(from)
                for (step in 1 until subPassCount) {
                    val d = from * subStepRatio.pow(step.toDouble())
                    intermediateList.add(roundTo3(d))
                }
                intermediateList.add(to)

                suggestions.add(
                    SuggestedIntermediatePass(
                        passIndex = i + 1,
                        fromDie = from,
                        toDie = to,
                        currentElongation = elongation,
                        proposedDies = intermediateList
                    )
                )
            }
        }
        return suggestions
    }

    /**
     * Applies suggested intermediate passes to the overall die schedule.
     */
    fun applySuggestedDies(
        currentDies: List<Double>,
        suggestions: List<SuggestedIntermediatePass>
    ): List<Double> {
        if (currentDies.size < 2 || suggestions.isEmpty()) return currentDies

        val suggestionsMap = suggestions.associateBy { it.passIndex }
        val newSchedule = mutableListOf<Double>()

        for (i in 0 until currentDies.size - 1) {
            val passNum = i + 1
            val suggestion = suggestionsMap[passNum]

            if (suggestion != null) {
                // Add all except the last one (next pass or end will handle it)
                for (j in 0 until suggestion.proposedDies.size - 1) {
                    newSchedule.add(suggestion.proposedDies[j])
                }
            } else {
                newSchedule.add(currentDies[i])
            }
        }
        newSchedule.add(currentDies.last())

        return newSchedule.distinct()
    }

    /**
     * Checks all passes against target elongation limits.
     */
    fun checkTargets(
        passes: List<PassResult>,
        targetMin: Double,
        targetMax: Double
    ): List<TargetCheckResult> {
        return passes.map { pass ->
            val outOfRange = pass.elongationPercent < targetMin || pass.elongationPercent > targetMax
            TargetCheckResult(
                passNumber = pass.passNumber,
                fromDie = pass.fromDie,
                toDie = pass.toDie,
                elongation = pass.elongationPercent,
                targetMin = targetMin,
                targetMax = targetMax,
                isOutOfRange = outOfRange
            )
        }
    }

    /**
     * Calculates linear mass in grams per metre for a round wire:
     * Area (cm^2) = PI * (d_mm / 20)^2
     * Volume per metre (cm^3) = Area (cm^2) * 100 cm
     * Mass (g/m) = Volume (cm^3) * Density (g/cm^3)
     */
    fun calculateLinearMassGramsPerMetre(diameterMm: Double, material: WireMaterial): Double {
        if (diameterMm <= 0.0) return 0.0
        val radiusCm = (diameterMm / 10.0) / 2.0
        val areaCm2 = PI * radiusCm * radiusCm
        val volumeCm3PerMetre = areaCm2 * 100.0
        return volumeCm3PerMetre * material.densityGPerCm3
    }

    /**
     * Calculates wire weight in kg from diameter, length in metres, and material density.
     */
    fun calculateWeightKgFromLength(diameterMm: Double, lengthMetres: Double, material: WireMaterial): WireWeightLengthResult {
        val linearMassGPerM = calculateLinearMassGramsPerMetre(diameterMm, material)
        val totalWeightKg = (linearMassGPerM * lengthMetres) / 1000.0
        return WireWeightLengthResult(
            diameterMm = diameterMm,
            lengthMetres = lengthMetres,
            weightKg = totalWeightKg,
            linearMassGPerM = linearMassGPerM,
            materialName = material.displayName
        )
    }

    /**
     * Calculates wire length in metres from diameter, total weight in kg, and material density.
     */
    fun calculateLengthMetresFromWeight(diameterMm: Double, weightKg: Double, material: WireMaterial): WireWeightLengthResult {
        val linearMassGPerM = calculateLinearMassGramsPerMetre(diameterMm, material)
        val totalLengthM = if (linearMassGPerM > 0.0) (weightKg * 1000.0) / linearMassGPerM else 0.0
        return WireWeightLengthResult(
            diameterMm = diameterMm,
            lengthMetres = totalLengthM,
            weightKg = weightKg,
            linearMassGPerM = linearMassGPerM,
            materialName = material.displayName
        )
    }
}
