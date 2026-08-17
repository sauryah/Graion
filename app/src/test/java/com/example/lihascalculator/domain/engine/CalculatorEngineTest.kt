package com.example.lihascalculator.domain.engine

import com.example.lihascalculator.domain.model.EvaluationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setup() {
        engine = CalculatorEngine()
    }

    @Test
    fun testAddition() {
        val result = engine.evaluate("2 + 3")
        assertTrue(result is EvaluationResult.Success)
        assertEquals("5", (result as EvaluationResult.Success).formatted)
    }

    @Test
    fun testSubtraction() {
        val result = engine.evaluate("10 - 4")
        assertTrue(result is EvaluationResult.Success)
        assertEquals("6", (result as EvaluationResult.Success).formatted)

        val unicodeResult = engine.evaluate("15 − 7")
        assertTrue(unicodeResult is EvaluationResult.Success)
        assertEquals("8", (unicodeResult as EvaluationResult.Success).formatted)
    }

    @Test
    fun testMultiplication() {
        val result = engine.evaluate("6 * 7")
        assertTrue(result is EvaluationResult.Success)
        assertEquals("42", (result as EvaluationResult.Success).formatted)

        val unicodeResult = engine.evaluate("10.5 × 2")
        assertTrue(unicodeResult is EvaluationResult.Success)
        assertEquals("21", (unicodeResult as EvaluationResult.Success).formatted)
    }

    @Test
    fun testDivision() {
        val result = engine.evaluate("100 / 4")
        assertTrue(result is EvaluationResult.Success)
        assertEquals("25", (result as EvaluationResult.Success).formatted)

        val unicodeResult = engine.evaluate("100 ÷ 4")
        assertTrue(unicodeResult is EvaluationResult.Success)
        assertEquals("25", (unicodeResult as EvaluationResult.Success).formatted)
    }

    @Test
    fun testOperatorPrecedence() {
        // 2 + 3 * 4 should be 14, not 20
        val result1 = engine.evaluate("2 + 3 × 4")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("14", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("10 - 2 × 3")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("4", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate("20 / 4 + 2 * 3")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("11", (result3 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testParentheses() {
        // (2 + 3) * 4 should be 20
        val result1 = engine.evaluate("(2 + 3) × 4")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("20", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("((2 + 3) * (4 + 1)) / 5")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("5", (result2 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testImplicitMultiplication() {
        val result1 = engine.evaluate("2(3 + 4)")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("14", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("(2 + 3)(4)")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("20", (result2 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testDecimalsAndPrecision() {
        val result1 = engine.evaluate("10.5 × 2")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("21", (result1 as EvaluationResult.Success).formatted)

        // 0.1 + 0.2 must be 0.3 without IEEE float precision artifacts
        val result2 = engine.evaluate("0.1 + 0.2")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("0.3", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate(".5 + .5")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("1", (result3 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testNegativeNumbers() {
        val result1 = engine.evaluate("-5 + 10")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("5", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("3 * -2")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("-6", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate("(-5) * 4")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("-20", (result3 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testPercentages() {
        val result1 = engine.evaluate("50%")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("0.5", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("100 * 50%")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("50", (result2 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testDivisionByZero() {
        val result = engine.evaluate("100 ÷ 0")
        assertTrue(result is EvaluationResult.Error.DivisionByZero)
        assertEquals("Cannot divide by zero", (result as EvaluationResult.Error).userMessage)
    }

    @Test
    fun testRepeatedEquals() {
        // First evaluate 5 + 3 = 8
        val result1 = engine.evaluate("5 + 3")
        assertTrue(result1 is EvaluationResult.Success)
        val value1 = (result1 as EvaluationResult.Success).value
        assertEquals("8", result1.formatted)

        // Repeat equals on 8 -> should do 8 + 3 = 11
        val result2 = engine.repeatLastOperation(value1)
        assertTrue(result2 is EvaluationResult.Success)
        val value2 = (result2 as EvaluationResult.Success).value
        assertEquals("11", result2.formatted)

        // Repeat equals on 11 -> should do 11 + 3 = 14
        val result3 = engine.repeatLastOperation(value2)
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("14", (result3 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testLargeNumbers() {
        val result = engine.evaluate("1000000 * 1000000")
        assertTrue(result is EvaluationResult.Success)
        assertEquals("1000000000000", (result as EvaluationResult.Success).formatted)
    }

    @Test
    fun testPreviewEvaluation() {
        val preview = engine.evaluatePreview("2 + 3 *")
        assertTrue(preview is EvaluationResult.Success)
        assertEquals("5", (preview as EvaluationResult.Success).formatted)
    }
}
