package com.sauryah.lihas.calculator.domain.engine

import com.sauryah.lihas.calculator.domain.model.Token
import com.sauryah.lihas.calculator.domain.model.TokenType
import java.util.ArrayDeque

class ExpressionParser {

    /**
     * Converts a list of infix Tokens into a Reverse Polish Notation (RPN) queue
     * using the Shunting-Yard algorithm.
     *
     * Automatically balances unmatched opening parentheses for smooth evaluation.
     */
    fun parseToRpn(tokens: List<Token>): List<Token> {
        if (tokens.isEmpty()) return emptyList()

        val output = mutableListOf<Token>()
        val operatorStack = ArrayDeque<Token>()

        for (token in tokens) {
            when (token.type) {
                TokenType.NUMBER -> {
                    output.add(token)
                }

                TokenType.PERCENT -> {
                    // Postfix unary operator - highest precedence, outputs directly after operand
                    output.add(token)
                }

                TokenType.UNARY_MINUS -> {
                    // Unary prefix operator: right-associative
                    while (operatorStack.isNotEmpty()) {
                        val top = operatorStack.peek() ?: break
                        if (top.type != TokenType.LEFT_PAREN &&
                            precedence(top.type) > precedence(token.type)
                        ) {
                            output.add(operatorStack.pop())
                        } else {
                            break
                        }
                    }
                    operatorStack.push(token)
                }

                TokenType.PLUS, TokenType.MINUS, TokenType.MULTIPLY, TokenType.DIVIDE -> {
                    // Binary operators: left-associative
                    while (operatorStack.isNotEmpty()) {
                        val top = operatorStack.peek() ?: break
                        if (top.type != TokenType.LEFT_PAREN &&
                            (precedence(top.type) > precedence(token.type) ||
                                    (precedence(top.type) == precedence(token.type) && isLeftAssociative(token.type)))
                        ) {
                            output.add(operatorStack.pop())
                        } else {
                            break
                        }
                    }
                    operatorStack.push(token)
                }

                TokenType.LEFT_PAREN -> {
                    operatorStack.push(token)
                }

                TokenType.RIGHT_PAREN -> {
                    var foundLeft = false
                    while (operatorStack.isNotEmpty()) {
                        val top = operatorStack.pop()
                        if (top.type == TokenType.LEFT_PAREN) {
                            foundLeft = true
                            break
                        } else {
                            output.add(top)
                        }
                    }
                    // If no matching left paren was found, we simply ignore the orphan right paren
                }
            }
        }

        // Pop remaining operators (ignoring left parens)
        while (operatorStack.isNotEmpty()) {
            val top = operatorStack.pop()
            if (top.type != TokenType.LEFT_PAREN) {
                output.add(top)
            }
        }

        return output
    }

    private fun precedence(type: TokenType): Int {
        return when (type) {
            TokenType.PERCENT -> 4
            TokenType.UNARY_MINUS -> 3
            TokenType.MULTIPLY, TokenType.DIVIDE -> 2
            TokenType.PLUS, TokenType.MINUS -> 1
            else -> 0
        }
    }

    private fun isLeftAssociative(type: TokenType): Boolean {
        return when (type) {
            TokenType.UNARY_MINUS -> false
            else -> true
        }
    }
}
