package com.sauryah.graion.domain.model

enum class CalculatorOperator(val symbol: String, val displaySymbol: String) {
    ADD("+", "+"),
    SUBTRACT("-", "−"),
    MULTIPLY("*", "×"),
    DIVIDE("/", "÷"),
    POWER("^", "^");

    companion object {
        fun fromChar(char: Char): CalculatorOperator? {
            return when (char) {
                '+' -> ADD
                '-', '−' -> SUBTRACT
                '*', '×' -> MULTIPLY
                '/', '÷' -> DIVIDE
                '^' -> POWER
                else -> null
            }
        }
    }
}

enum class CalculatorConstant(val symbol: String) {
    PI("π"),
    EULER("e")
}

enum class CalculatorFunction(val symbol: String, val displayName: String) {
    SIN("sin", "sin"),
    COS("cos", "cos"),
    TAN("tan", "tan"),
    ASIN("asin", "asin"),
    ACOS("acos", "acos"),
    ATAN("atan", "atan"),
    ABS("abs", "abs"),
    CBRT("cbrt", "∛"),
    FACTORIAL("!", "!"),
    LN("ln", "ln"),
    LOG("log", "log")
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
    data class Constant(val constant: CalculatorConstant) : CalculatorAction
    data class Function(val function: CalculatorFunction) : CalculatorAction
    data object SquareRoot : CalculatorAction
    data object MemoryAdd : CalculatorAction
    data object MemorySubtract : CalculatorAction
    data object MemoryRecall : CalculatorAction
    data object MemoryClear : CalculatorAction
    data class SetExpression(val expression: String) : CalculatorAction
    data class UseResult(val result: String) : CalculatorAction
    data object ToggleAngleMode : CalculatorAction
}
