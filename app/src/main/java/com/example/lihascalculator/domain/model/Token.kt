package com.sauryah.lihas.calculator.domain.model

import java.math.BigDecimal

enum class TokenType {
    NUMBER,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    POWER,
    PERCENT,
    SQRT,
    LEFT_PAREN,
    RIGHT_PAREN,
    UNARY_MINUS
}

data class Token(
    val type: TokenType,
    val text: String,
    val value: BigDecimal? = null
)
