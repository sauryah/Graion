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
    CBRT,
    FACTORIAL,
    SIN,
    COS,
    TAN,
    ASIN,
    ACOS,
    ATAN,
    LN,
    LOG,
    ABS,
    LEFT_PAREN,
    RIGHT_PAREN,
    UNARY_MINUS
}

data class Token(
    val type: TokenType,
    val text: String,
    val value: BigDecimal? = null
)
