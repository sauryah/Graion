package com.example.lihascalculator.domain.engine

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatter {

    private const val MAX_DISPLAY_DIGITS = 12
    private const val SCIENTIFIC_THRESHOLD_HIGH = 1e15
    private const val SCIENTIFIC_THRESHOLD_LOW = 1e-6

    /**
     * Formats a BigDecimal result for clean UI display.
     * Removes unnecessary trailing zeros, avoids IEEE precision noise,
     * and uses scientific notation for extremely large or small numbers.
     */
    fun formatResult(value: BigDecimal): String {
        // Strip trailing zeros
        val stripped = value.stripTrailingZeros()

        // Handle zero explicitly to avoid -0 or 0E-8
        if (stripped.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }

        val doubleVal = kotlin.math.abs(stripped.toDouble())

        // Check if scientific notation is needed
        if (doubleVal >= SCIENTIFIC_THRESHOLD_HIGH || (doubleVal > 0 && doubleVal < SCIENTIFIC_THRESHOLD_LOW)) {
            val symbols = DecimalFormatSymbols(Locale.US)
            val sciFormat = DecimalFormat("0.######E0", symbols)
            return sciFormat.format(stripped).replace("E", "e")
        }

        // For regular numbers, standard string with scale adjustment if needed
        val plain = stripped.toPlainString()
        if (plain.length > 15 && plain.contains('.')) {
            // Trim precision if excessively long
            val rounded = stripped.setScale(MAX_DISPLAY_DIGITS, RoundingMode.HALF_UP).stripTrailingZeros()
            return rounded.toPlainString()
        }

        return plain
    }

    /**
     * Formats an expression for display with proper operator symbols:
     * * -> ×, / -> ÷, - -> −, + -> +
     */
    fun formatDisplayExpression(expression: String): String {
        if (expression.isEmpty()) return "0"
        return expression
            .replace("*", " × ")
            .replace("/", " ÷ ")
            .replace("+", " + ")
            .replace("-", " − ")
            .replace("  ", " ")
    }
}

