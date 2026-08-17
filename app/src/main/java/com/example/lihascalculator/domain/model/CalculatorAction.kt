package com.example.lihascalculator.domain.model

enum class CalculatorOperator(val symbol: String, val displaySymbol: String) {
    ADD("+", "+"),
    SUBTRACT("-", "−"),
    MULTIPLY("*", "×"),
    DIVIDE("/", "÷");

    companion object {
        fun fromChar(char: Char): CalculatorOperator? {
            return when (char) {
                '+' -> ADD
                '-', '−' -> SUBTRACT
                '*', '×' -> MULTIPLY
                '/', '÷' -> DIVIDE
                else -> null
            }
        }
    }
}

sealed interface CalculatorAction {
    data class Number(val number: Int) : CalculatorAction
    data object Decimal : CalculatorAction
    data class Operator(val operator: CalculatorOperator) : CalculatorAction
    data object Clear : CalculatorAction
    data object Delete : CalculatorAction
    data object Calculate : CalculatorAction
    data object Parentheses : CalculatorAction
    data object Percentage : CalculatorAction
    data object ToggleSign : CalculatorAction
    data class SetExpression(val expression: String) : CalculatorAction
    data class UseResult(val result: String) : CalculatorAction
}
