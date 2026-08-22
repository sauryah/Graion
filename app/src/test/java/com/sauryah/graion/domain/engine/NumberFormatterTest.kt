package com.sauryah.graion.domain.engine

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class NumberFormatterTest {

    @Test
    fun `formatResult handles whole numbers cleanly`() {
        assertEquals("0", NumberFormatter.formatResult(BigDecimal("0")))
        assertEquals("0", NumberFormatter.formatResult(BigDecimal("0.000000")))
        assertEquals("42", NumberFormatter.formatResult(BigDecimal("42")))
        assertEquals("42", NumberFormatter.formatResult(BigDecimal("42.000")))
        assertEquals("-100", NumberFormatter.formatResult(BigDecimal("-100.0")))
    }

    @Test
    fun `formatResult handles decimals without trailing zeros`() {
        assertEquals("3.14159", NumberFormatter.formatResult(BigDecimal("3.14159000")))
        assertEquals("0.5", NumberFormatter.formatResult(BigDecimal("0.5000000")))
        assertEquals("0.125", NumberFormatter.formatResult(BigDecimal("0.125000")))
    }

    @Test
    fun `formatResult uses scientific notation for very large numbers`() {
        val large = BigDecimal("100000000000000000") // 10^17
        val formatted = NumberFormatter.formatResult(large)
        assertEquals("1e17", formatted)
    }

    @Test
    fun `formatResult uses scientific notation for microscopic numbers`() {
        val tiny = BigDecimal("0.00000005") // 5 * 10^-8
        val formatted = NumberFormatter.formatResult(tiny)
        assertEquals("5e-8", formatted)
    }

    @Test
    fun `formatDisplayExpression replaces arithmetic operators with proper typography`() {
        assertEquals("0", NumberFormatter.formatDisplayExpression(""))
        assertEquals("2 × 3", NumberFormatter.formatDisplayExpression("2*3").trim())
        assertEquals("10 ÷ 2", NumberFormatter.formatDisplayExpression("10/2").trim())
        assertEquals("5 + 5", NumberFormatter.formatDisplayExpression("5+5").trim())
        assertEquals("9 − 4", NumberFormatter.formatDisplayExpression("9-4").trim())
    }
}
