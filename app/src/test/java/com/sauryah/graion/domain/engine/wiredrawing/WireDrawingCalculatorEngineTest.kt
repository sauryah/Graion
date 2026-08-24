package com.sauryah.graion.domain.engine.wiredrawing

import com.sauryah.graion.domain.model.wiredrawing.QualityRating
import com.sauryah.graion.domain.model.wiredrawing.WireMaterial
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class WireDrawingCalculatorEngineTest {

    @Test
    fun testAreaCalculation() {
        val diameter = 2.0
        val expected = WireDrawingCalculatorEngine.roundTo3(PI * 4.0 / 4.0) // PI = 3.142
        assertEquals(expected, WireDrawingCalculatorEngine.calculateArea(diameter), 0.001)

        assertEquals(0.0, WireDrawingCalculatorEngine.calculateArea(0.0), 0.001)
        assertEquals(0.0, WireDrawingCalculatorEngine.calculateArea(-1.0), 0.001)
    }

    @Test
    fun testElongationCalculation() {
        val dBefore = 2.490
        val dAfter = 2.217
        // (2.490^2 / 2.217^2 - 1) * 100
        val expected = WireDrawingCalculatorEngine.roundTo3(((2.490 * 2.490) / (2.217 * 2.217) - 1.0) * 100.0)
        val elongation = WireDrawingCalculatorEngine.calculateElongation(dBefore, dAfter)
        assertEquals(expected, elongation, 0.001)
    }

    @Test
    fun testAreaReductionCalculation() {
        val dBefore = 2.490
        val dAfter = 2.217
        val expected = WireDrawingCalculatorEngine.roundTo3((1.0 - (2.217 * 2.217) / (2.490 * 2.490)) * 100.0)
        val reduction = WireDrawingCalculatorEngine.calculateAreaReduction(dBefore, dAfter)
        assertEquals(expected, reduction, 0.001)
    }

    @Test
    fun testReductionRatioCalculation() {
        val dBefore = 2.0
        val dAfter = 1.0
        val ratio = WireDrawingCalculatorEngine.calculateReductionRatio(dBefore, dAfter)
        assertEquals(4.0, ratio, 0.001)
    }

    @Test
    fun testInverseFormulas() {
        val dBefore = 2.0
        val elongation = 100.0 // 4x area -> 2x length
        val dAfterElong = WireDrawingCalculatorEngine.calculateDiameterFromElongation(dBefore, elongation)
        assertEquals(1.414, dAfterElong, 0.001)

        val reduction = 50.0 // 50% reduction
        val dAfterRed = WireDrawingCalculatorEngine.calculateDiameterFromAreaReduction(dBefore, reduction)
        assertEquals(1.414, dAfterRed, 0.001)

        val ratio = 2.0
        val dAfterRatio = WireDrawingCalculatorEngine.calculateDiameterFromRatio(dBefore, ratio)
        assertEquals(1.414, dAfterRatio, 0.001)
    }

    @Test
    fun testInverseFormulaEdgeCases() {
        val dBefore = 2.0

        // Elongation <= -100 -> die unchanged
        assertEquals(2.0, WireDrawingCalculatorEngine.calculateDiameterFromElongation(dBefore, -100.0), 0.001)
        assertEquals(2.0, WireDrawingCalculatorEngine.calculateDiameterFromElongation(dBefore, -150.0), 0.001)

        // Reduction >= 100 -> die = 0
        assertEquals(0.0, WireDrawingCalculatorEngine.calculateDiameterFromAreaReduction(dBefore, 100.0), 0.001)
        assertEquals(0.0, WireDrawingCalculatorEngine.calculateDiameterFromAreaReduction(dBefore, 120.0), 0.001)

        // Ratio <= 0 -> die unchanged
        assertEquals(2.0, WireDrawingCalculatorEngine.calculateDiameterFromRatio(dBefore, 0.0), 0.001)
        assertEquals(2.0, WireDrawingCalculatorEngine.calculateDiameterFromRatio(dBefore, -2.0), 0.001)
    }

    @Test
    fun testBasicPassCalculation() {
        val dies = listOf(2.490, 2.217, 1.974)
        val passes = WireDrawingCalculatorEngine.calculatePasses(dies)

        assertEquals(2, passes.size)
        assertEquals(1, passes[0].passNumber)
        assertEquals(2.490, passes[0].fromDie, 0.001)
        assertEquals(2.217, passes[0].toDie, 0.001)

        assertEquals(2, passes[1].passNumber)
        assertEquals(2.217, passes[1].fromDie, 0.001)
        assertEquals(1.974, passes[1].toDie, 0.001)
    }

    @Test
    fun testStatisticsCalculation() {
        val dies = listOf(2.490, 2.217, 1.974)
        val passes = WireDrawingCalculatorEngine.calculatePasses(dies)
        val stats = WireDrawingCalculatorEngine.calculateStatistics(dies, passes)

        assertEquals(2, stats.totalPasses)
        assertEquals(2.490, stats.startingDie, 0.001)
        assertEquals(1.974, stats.finalDie, 0.001)
        assertTrue(stats.avgElongationPercent > 0.0)
        assertTrue(stats.overallAreaReductionPercent > 0.0)
    }

    @Test
    fun testConsistencyRating() {
        // Uniform elongation passes -> deviation ~0 -> 5 stars / EXCELLENT
        val dies = WireDrawingCalculatorEngine.generateDieSeries(2.500, 0.500, 20.0)
        val passes = WireDrawingCalculatorEngine.calculatePasses(dies)
        val consistency = WireDrawingCalculatorEngine.calculateConsistency(passes)

        assertTrue(consistency.maxDeviation <= 1.0)
        assertEquals(QualityRating.EXCELLENT, consistency.rating)
        assertEquals(5, consistency.stars)

        // Empty passes -> N/A, 0 stars
        val emptyConsistency = WireDrawingCalculatorEngine.calculateConsistency(emptyList())
        assertEquals(QualityRating.NOT_APPLICABLE, emptyConsistency.rating)
        assertEquals(0, emptyConsistency.stars)
    }

    @Test
    fun testSeriesGenerator() {
        val dStart = 2.500
        val dEnd = 0.500
        val targetElongation = 20.0

        val series = WireDrawingCalculatorEngine.generateDieSeries(dStart, dEnd, targetElongation)

        assertTrue(series.size > 2)
        assertEquals(2.500, series.first(), 0.001)
        assertEquals(0.500, series.last(), 0.001)

        // Ensure strictly decreasing
        for (i in 0 until series.size - 1) {
            assertTrue(series[i] > series[i + 1])
        }
    }

    @Test
    fun testSeriesGeneratorWithFinalPassRange() {
        val dStart = 2.500
        val dEnd = 0.500
        val targetElongation = 20.0
        val finalMin = 18.0
        val finalMax = 22.0

        val series = WireDrawingCalculatorEngine.generateDieSeries(dStart, dEnd, targetElongation, finalMin, finalMax)
        assertTrue(series.isNotEmpty())
        assertEquals(2.500, series.first(), 0.001)
        assertEquals(0.500, series.last(), 0.001)
    }

    @Test
    fun testDieSuggester() {
        // Two dies with large gap (elongation ~ 100%) when target is 20%
        val dies = listOf(2.000, 1.000)
        val suggestions = WireDrawingCalculatorEngine.suggestIntermediateDies(dies, 20.0)

        assertEquals(1, suggestions.size)
        assertEquals(1, suggestions[0].passIndex)
        assertTrue(suggestions[0].proposedDies.size > 2)
        assertEquals(2.000, suggestions[0].proposedDies.first(), 0.001)
        assertEquals(1.000, suggestions[0].proposedDies.last(), 0.001)

        // Apply suggestions
        val newDies = WireDrawingCalculatorEngine.applySuggestedDies(dies, suggestions)
        assertTrue(newDies.size > 2)
        assertEquals(2.000, newDies.first(), 0.001)
        assertEquals(1.000, newDies.last(), 0.001)
    }

    @Test
    fun testInputParserWithSeparatorsAndSmartQuotes() {
        // Smart quotes, commas, tabs, newlines, pipes
        val rawInput = "“2.490”, 2.217\t1.974;\n1.757 | ‘1.564’"
        val (dies, errors) = WireDrawingCalculatorEngine.parseInputText(rawInput)

        assertTrue(errors.isEmpty())
        assertEquals(listOf(2.490, 2.217, 1.974, 1.757, 1.564), dies)

        // Invalid numeric
        val (invalidDies, invalidErrors) = WireDrawingCalculatorEngine.parseInputText("2.490, abc, -1.5, 0")
        assertEquals(1, invalidDies.size) // only 2.490
        assertEquals(3, invalidErrors.size) // 'abc', '-1.5', '0'
    }

    @Test
    fun testWireWeightAndLengthCalculations() {
        // Copper wire of 2.0 mm diameter, 1000 metres length:
        // Area = PI * (0.1 cm)^2 = 0.0314159 cm^2
        // Volume for 1000m (100,000 cm) = 3141.59 cm^3
        // Weight for Copper (density 8.96 g/cm3) = 3141.59 * 8.96 = 28148.6 g = 28.148 kg
        val copperResult = WireDrawingCalculatorEngine.calculateWeightKgFromLength(2.0, 1000.0, WireMaterial.COPPER)
        assertEquals(28.148, copperResult.weightKg, 0.05)
        assertEquals(28.148, copperResult.linearMassGPerM, 0.05)

        // Inverse calculation: given 28.148 kg, should give back ~1000m
        val lengthResult = WireDrawingCalculatorEngine.calculateLengthMetresFromWeight(2.0, copperResult.weightKg, WireMaterial.COPPER)
        assertEquals(1000.0, lengthResult.lengthMetres, 0.5)

        // Aluminum wire of 2.0 mm diameter: density 2.70 g/cm3 -> Weight should be roughly 2.70/8.96 of copper
        val alResult = WireDrawingCalculatorEngine.calculateWeightKgFromLength(2.0, 1000.0, WireMaterial.ALUMINUM_EC)
        assertEquals(8.482, alResult.weightKg, 0.05)
    }

    @Test
    fun testOptimalDieGeometryCalculations() {
        // From 2.490 mm to 2.217 mm with standard friction mu = 0.05 on Copper
        val geom = WireDrawingCalculatorEngine.calculateOptimalDieGeometry(2.490, 2.217, 0.05, WireMaterial.COPPER)
        assertTrue(geom.optimalApproachAngleDeg in 10.0..20.0)
        assertEquals(35.0, geom.bearingLengthRatioPercent, 0.1)
        assertEquals(WireDrawingCalculatorEngine.roundTo3(2.217 * 0.35), geom.recommendedBearingLengthMm, 0.001)

        // Steel die bearing ratio should be 50%
        val steelGeom = WireDrawingCalculatorEngine.calculateOptimalDieGeometry(2.490, 2.217, 0.05, WireMaterial.CARBON_STEEL_HIGH)
        assertEquals(50.0, steelGeom.bearingLengthRatioPercent, 0.1)
        assertEquals(WireDrawingCalculatorEngine.roundTo3(2.217 * 0.50), steelGeom.recommendedBearingLengthMm, 0.001)
    }

    @Test
    fun testMachineKinematics() {
        val dies = listOf(2.490, 2.217, 1.974)
        val passes = WireDrawingCalculatorEngine.calculatePasses(dies)
        val finishSpeed = 10.0 // 10 m/s finish wire speed

        val kinematics = WireDrawingCalculatorEngine.calculateMachineKinematics(passes, finishSpeed, WireMaterial.COPPER)
        assertEquals(10.0, kinematics.finishSpeedMPerS, 0.001)
        assertTrue(kinematics.inletSpeedMPerS < kinematics.finishSpeedMPerS)
        assertTrue(kinematics.productionRateKgPerHour > 0.0)
        assertEquals(2, kinematics.passSpeeds.size)
        // Last pass wire speed should match finish speed
        assertEquals(10.0, kinematics.passSpeeds.last().wireSpeedMPerS, 0.001)
        // Pass 1 wire speed should be lower than finish speed
        assertTrue(kinematics.passSpeeds.first().wireSpeedMPerS < 10.0)
    }

    @Test
    fun testEmptySchedule() {
        val passes = WireDrawingCalculatorEngine.calculatePasses(emptyList())
        assertTrue(passes.isEmpty())

        val passesOneDie = WireDrawingCalculatorEngine.calculatePasses(listOf(2.5))
        assertTrue(passesOneDie.isEmpty())

        val stats = WireDrawingCalculatorEngine.calculateStatistics(emptyList(), emptyList())
        assertEquals(0, stats.totalPasses)
    }
}
