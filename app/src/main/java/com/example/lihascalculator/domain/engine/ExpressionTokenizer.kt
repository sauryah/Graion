package com.sauryah.lihas.calculator.domain.engine

import com.sauryah.lihas.calculator.domain.model.Token
import com.sauryah.lihas.calculator.domain.model.TokenType
import java.math.BigDecimal

class ExpressionTokenizer {

    fun tokenize(rawExpression: String): List<Token> {
        val normalized = rawExpression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(" ", "")

        if (normalized.isEmpty()) {
            return emptyList()
        }

        val rawTokens = mutableListOf<Token>()
        var index = 0
        val length = normalized.length

        while (index < length) {
            val char = normalized[index]

            when {
                char.isDigit() || char == '.' -> {
                    val start = index
                    var hasDot = (char == '.')

                    index++
                    while (index < length) {
                        val nextChar = normalized[index]
                        if (nextChar.isDigit()) {
                            index++
                        } else if (nextChar == '.') {
                            if (hasDot) {
                                // Extra dot in same number token - treat as end of number or skip
                                break
                            }
                            hasDot = true
                            index++
                        } else {
                            break
                        }
                    }

                    var numStr = normalized.substring(start, index)
                    if (numStr == ".") {
                        numStr = "0"
                    } else if (numStr.startsWith(".")) {
                        numStr = "0$numStr"
                    } else if (numStr.endsWith(".")) {
                        numStr = numStr.dropLast(1)
                    }

                    val value = try {
                        BigDecimal(numStr)
                    } catch (e: Exception) {
                        BigDecimal.ZERO
                    }
                    rawTokens.add(Token(TokenType.NUMBER, numStr, value))
                }

                char == '+' -> {
                    rawTokens.add(Token(TokenType.PLUS, "+"))
                    index++
                }

                char == '-' -> {
                    // Determine whether this is unary minus or binary minus
                    val isUnary = if (rawTokens.isEmpty()) {
                        true
                    } else {
                        val lastType = rawTokens.last().type
                        lastType == TokenType.PLUS ||
                                lastType == TokenType.MINUS ||
                                lastType == TokenType.MULTIPLY ||
                                lastType == TokenType.DIVIDE ||
                                lastType == TokenType.POWER ||
                                lastType == TokenType.LEFT_PAREN ||
                                lastType == TokenType.UNARY_MINUS
                    }

                    if (isUnary) {
                        rawTokens.add(Token(TokenType.UNARY_MINUS, "-"))
                    } else {
                        rawTokens.add(Token(TokenType.MINUS, "-"))
                    }
                    index++
                }

                char == '*' -> {
                    rawTokens.add(Token(TokenType.MULTIPLY, "*"))
                    index++
                }

                char == '/' -> {
                    rawTokens.add(Token(TokenType.DIVIDE, "/"))
                    index++
                }

                char == '%' -> {
                    rawTokens.add(Token(TokenType.PERCENT, "%"))
                    index++
                }

                char == '^' -> {
                    rawTokens.add(Token(TokenType.POWER, "^"))
                    index++
                }

                char == '√' -> {
                    rawTokens.add(Token(TokenType.SQRT, "√"))
                    index++
                }

                char == 'π' -> {
                    rawTokens.add(Token(TokenType.NUMBER, "π", BigDecimal("3.14159265358979323846264338327950288")))
                    index++
                }

                char == 'e' -> {
                    rawTokens.add(Token(TokenType.NUMBER, "e", BigDecimal("2.71828182845904523536028747135266250")))
                    index++
                }

                char == '(' -> {
                    rawTokens.add(Token(TokenType.LEFT_PAREN, "("))
                    index++
                }

                char == ')' -> {
                    rawTokens.add(Token(TokenType.RIGHT_PAREN, ")"))
                    index++
                }

                else -> {
                    // Ignore unexpected characters or advance
                    index++
                }
            }
        }

        // Now perform implicit multiplication insertion
        // Examples: 2(3) -> 2 * (3), (2)(3) -> (2) * (3), (2)3 -> (2) * 3, 5%2 -> 5% * 2, 2π -> 2 * π
        val finalTokens = mutableListOf<Token>()
        for (i in rawTokens.indices) {
            val current = rawTokens[i]
            if (i > 0) {
                val prev = rawTokens[i - 1]
                val needsImplicitMult =
                    (prev.type == TokenType.NUMBER && current.type == TokenType.LEFT_PAREN) ||
                            (prev.type == TokenType.RIGHT_PAREN && current.type == TokenType.LEFT_PAREN) ||
                            (prev.type == TokenType.RIGHT_PAREN && current.type == TokenType.NUMBER) ||
                            (prev.type == TokenType.NUMBER && current.type == TokenType.NUMBER) ||
                            (prev.type == TokenType.PERCENT && current.type == TokenType.NUMBER) ||
                            (prev.type == TokenType.PERCENT && current.type == TokenType.LEFT_PAREN)

                if (needsImplicitMult) {
                    finalTokens.add(Token(TokenType.MULTIPLY, "*"))
                }
            }
            finalTokens.add(current)
        }

        return finalTokens
    }
}
