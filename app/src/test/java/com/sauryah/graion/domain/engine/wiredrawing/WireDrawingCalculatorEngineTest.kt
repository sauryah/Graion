package com.sauryah.graion.domain.engine.wiredrawing

import com.sauryah.graion.domain.model.wiredrawing.QualityRating
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
    fun testEmptySchedule() {
        val passes = WireDrawingCalculatorEngine.calculatePasses(emptyList())
        assertTrue(passes.isEmpty())

        val passesOneDie = WireDrawingCalculatorEngine.calculatePasses(listOf(2.5))
        assertTrue(passesOneDie.isEmpty())

        val stats = WireDrawingCalculatorEngine.calculateStatistics(emptyList(), emptyList())
        assertEquals(0, stats.totalPasses)
    }
}
