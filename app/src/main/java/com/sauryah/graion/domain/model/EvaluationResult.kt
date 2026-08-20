package com.sauryah.graion.domain.model

import java.math.BigDecimal

sealed interface EvaluationResult {
    data class Success(
        val value: BigDecimal,
        val formatted: String
    ) : EvaluationResult

    sealed interface Error : EvaluationResult {
        val userMessage: String

        data object DivisionByZero : Error {
            override val userMessage: String = "Cannot divide by zero"
        }

        data object MalformedExpression : Error {
            override val userMessage: String = "Invalid expression"
        }

        data object EmptyExpression : Error {
            override val userMessage: String = ""
        }

        data object Overflow : Error {
            override val userMessage: String = "Value too large"
        }

        data class CustomError(
            override val userMessage: String
        ) : Error
    }
}
