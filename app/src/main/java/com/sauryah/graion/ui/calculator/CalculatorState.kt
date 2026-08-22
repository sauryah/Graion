package com.sauryah.graion.ui.calculator

import androidx.compose.runtime.Immutable

@Immutable
data class CalculatorState(
    val expression: String = "0",
    val displayExpression: String = "0",
    val result: String = "",
    val previewResult: String? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isCalculated: Boolean = false,
    val memory: String? = null
) {
    companion object {
        fun initial(): CalculatorState = CalculatorState(
            expression = "0",
            displayExpression = "0"
        )
    }
}

