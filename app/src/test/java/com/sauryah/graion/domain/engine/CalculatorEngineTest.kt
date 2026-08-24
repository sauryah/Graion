package com.sauryah.graion.domain.engine

import com.sauryah.graion.domain.model.AngleMode
import com.sauryah.graion.domain.model.EvaluationResult
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
    fun testPercentArithmetic() {
        // Calculator convention: a + b% = a + a*b/100
        val result1 = engine.evaluate("200 + 10%")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("220", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("200 - 10%")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("180", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate("100 / 50%")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("200", (result3 as EvaluationResult.Success).formatted)

        // Chained percent stays relative to running value
        val result4 = engine.evaluate("200 + 10% + 5%")
        assertTrue(result4 is EvaluationResult.Success)
        assertEquals("231", (result4 as EvaluationResult.Success).formatted)

        // Percent of an existing expression via parentheses is a plain value
        val result5 = engine.evaluate("(200 + 10)%")
        assertTrue(result5 is EvaluationResult.Success)
        assertEquals("2.1", (result5 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testPercentRepeatedEquals() {
        // 200 + 10% = 220, repeat equals -> 220 + 10% = 242
        val result1 = engine.evaluate("200 + 10%")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("220", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.repeatLastOperation(result1.value)
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("242", (result2 as EvaluationResult.Success).formatted)

        // 100 * 50% = 50, repeat -> 50 * 50% = 25
        engine.clearRepeatedOperation()
        val result3 = engine.evaluate("100 * 50%")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("50", (result3 as EvaluationResult.Success).formatted)

        val result4 = engine.repeatLastOperation(result3.value)
        assertTrue(result4 is EvaluationResult.Success)
        assertEquals("25", (result4 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testPreviewDoesNotPolluteRepeatedEqualsCache() {
        // Live preview must not register the last operation for repeated equals
        engine.evaluatePreview("5 + 3")

        val repeat = engine.repeatLastOperation(BigDecimal("8"))
        assertTrue(repeat is EvaluationResult.Success)
        assertEquals("8", (repeat as EvaluationResult.Success).formatted)

        // Only an explicit equals evaluation registers the operation
        val result = engine.evaluate("5 + 3")
        assertTrue(result is EvaluationResult.Success)
        assertEquals("8", (result as EvaluationResult.Success).formatted)

        val repeat2 = engine.repeatLastOperation(BigDecimal("8"))
        assertTrue(repeat2 is EvaluationResult.Success)
        assertEquals("11", (repeat2 as EvaluationResult.Success).formatted)
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
    fun testPower() {
        val result1 = engine.evaluate("2^10")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("1024", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("2^3^2")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("512", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate("2^-3")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("0.125", (result3 as EvaluationResult.Success).formatted)

        val result4 = engine.evaluate("2^0.5")
        assertTrue(result4 is EvaluationResult.Success)
        assertEquals("1.414213562373", (result4 as EvaluationResult.Success).formatted)

        val result5 = engine.evaluate("10 * 2^3")
        assertTrue(result5 is EvaluationResult.Success)
        assertEquals("80", (result5 as EvaluationResult.Success).formatted)

        val result6 = engine.evaluate("2^0")
        assertTrue(result6 is EvaluationResult.Success)
        assertEquals("1", (result6 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testSquareRoot() {
        val result1 = engine.evaluate("9√")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("3", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("4 + 9√")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("7", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate("2√")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("1.414213562373", (result3 as EvaluationResult.Success).formatted)

        val result4 = engine.evaluate("-4√")
        assertTrue(result4 is EvaluationResult.Success)
        assertEquals("-2", (result4 as EvaluationResult.Success).formatted)

        val result5 = engine.evaluate("(-4)√")
        assertTrue(result5 is EvaluationResult.Error)
    }

    @Test
    fun testConstants() {
        val result1 = engine.evaluate("π")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("3.14159265359", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("2π")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("6.28318530718", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate("e")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("2.718281828459", (result3 as EvaluationResult.Success).formatted)

        val result4 = engine.evaluate("2e")
        assertTrue(result4 is EvaluationResult.Success)
        assertEquals("5.436563656918", (result4 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testTrigFunctions() {
        // Degree mode
        val result1 = engine.evaluate("sin(30)")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("0.5", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("cos(60)")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("0.5", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate("tan(45)")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("1", (result3 as EvaluationResult.Success).formatted)

        val result4 = engine.evaluate("sin(90)")
        assertTrue(result4 is EvaluationResult.Success)
        assertEquals("1", (result4 as EvaluationResult.Success).formatted)

        val resultCos90 = engine.evaluate("cos(90)")
        assertTrue(resultCos90 is EvaluationResult.Success)
        assertEquals("0", (resultCos90 as EvaluationResult.Success).formatted)

        val resultSin180 = engine.evaluate("sin(180)")
        assertTrue(resultSin180 is EvaluationResult.Success)
        assertEquals("0", (resultSin180 as EvaluationResult.Success).formatted)

        val resultTan90 = engine.evaluate("tan(90)")
        assertTrue(resultTan90 is EvaluationResult.Error.Undefined)

        val resultTan270 = engine.evaluate("tan(270)")
        assertTrue(resultTan270 is EvaluationResult.Error.Undefined)

        // Nested + implicit multiplication
        val result5 = engine.evaluate("2sin(30)")
        assertTrue(result5 is EvaluationResult.Success)
        assertEquals("1", (result5 as EvaluationResult.Success).formatted)

        val result6 = engine.evaluate("sin(cos(0))")
        assertTrue(result6 is EvaluationResult.Success)
        assertEquals("0.017452406437", (result6 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testLogarithms() {
        val result1 = engine.evaluate("ln(e)")
        assertTrue(result1 is EvaluationResult.Success)
        assertEquals("1", (result1 as EvaluationResult.Success).formatted)

        val result2 = engine.evaluate("log(100)")
        assertTrue(result2 is EvaluationResult.Success)
        assertEquals("2", (result2 as EvaluationResult.Success).formatted)

        val result3 = engine.evaluate("log(1000)")
        assertTrue(result3 is EvaluationResult.Success)
        assertEquals("3", (result3 as EvaluationResult.Success).formatted)

        // Log of non-positive is an error
        val result4 = engine.evaluate("ln(0)")
        assertTrue(result4 is EvaluationResult.Error)

        val result5 = engine.evaluate("log(-5)")
        assertTrue(result5 is EvaluationResult.Error)
    }

    @Test
    fun testTrigonometricRadiansMode() {
        val resultSinPi = engine.evaluate("sin(π)", AngleMode.RADIANS)
        assertTrue(resultSinPi is EvaluationResult.Success)
        assertEquals("0", (resultSinPi as EvaluationResult.Success).formatted)

        val resultSinHalfPi = engine.evaluate("sin(π / 2)", AngleMode.RADIANS)
        assertTrue(resultSinHalfPi is EvaluationResult.Success)
        assertEquals("1", (resultSinHalfPi as EvaluationResult.Success).formatted)

        val resultCosPi = engine.evaluate("cos(π)", AngleMode.RADIANS)
        assertTrue(resultCosPi is EvaluationResult.Success)
        assertEquals("-1", (resultCosPi as EvaluationResult.Success).formatted)
    }

    @Test
    fun testFactorial() {
        val result0 = engine.evaluate("0!")
        assertTrue(result0 is EvaluationResult.Success)
        assertEquals("1", (result0 as EvaluationResult.Success).formatted)

        val result5 = engine.evaluate("5!")
        assertTrue(result5 is EvaluationResult.Success)
        assertEquals("120", (result5 as EvaluationResult.Success).formatted)

        val resultComplex = engine.evaluate("3! + 4!")
        assertTrue(resultComplex is EvaluationResult.Success)
        assertEquals("30", (resultComplex as EvaluationResult.Success).formatted)
    }

    @Test
    fun testCubeRoot() {
        val result8 = engine.evaluate("8∛")
        assertTrue(result8 is EvaluationResult.Success)
        assertEquals("2", (result8 as EvaluationResult.Success).formatted)

        val result27 = engine.evaluate("cbrt(27)")
        assertTrue(result27 is EvaluationResult.Success)
        assertEquals("3", (result27 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testAbsoluteValue() {
        val resultNeg = engine.evaluate("abs(-25.5)")
        assertTrue(resultNeg is EvaluationResult.Success)
        assertEquals("25.5", (resultNeg as EvaluationResult.Success).formatted)

        val resultPos = engine.evaluate("abs(10)")
        assertTrue(resultPos is EvaluationResult.Success)
        assertEquals("10", (resultPos as EvaluationResult.Success).formatted)
    }

    @Test
    fun testInverseTrigonometry() {
        val asin1 = engine.evaluate("asin(1)", AngleMode.DEGREES)
        assertTrue(asin1 is EvaluationResult.Success)
        assertEquals("90", (asin1 as EvaluationResult.Success).formatted)

        val acos1 = engine.evaluate("acos(1)", AngleMode.DEGREES)
        assertTrue(acos1 is EvaluationResult.Success)
        assertEquals("0", (acos1 as EvaluationResult.Success).formatted)

        val atan1 = engine.evaluate("atan(1)", AngleMode.DEGREES)
        assertTrue(atan1 is EvaluationResult.Success)
        assertEquals("45", (atan1 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testPreviewEvaluation() {
        val preview = engine.evaluatePreview("2 + 3 *")
        assertTrue(preview is EvaluationResult.Success)
        assertEquals("5", (preview as EvaluationResult.Success).formatted)
    }
}
