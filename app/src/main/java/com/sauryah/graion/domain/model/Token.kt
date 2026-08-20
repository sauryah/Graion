package com.sauryah.graion.domain.model

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
    SIN,
    COS,
    TAN,
    LN,
    LOG,
    LEFT_PAREN,
    RIGHT_PAREN,
    UNARY_MINUS
}

data class Token(
    val type: TokenType,
    val text: String,
    val value: BigDecimal? = null
)
