package com.example.lihascalculator.domain.engine

import com.example.lihascalculator.domain.model.CalculatorOperator
import com.example.lihascalculator.domain.model.EvaluationResult
import com.example.lihascalculator.domain.model.Token
import com.example.lihascalculator.domain.model.TokenType
import java.math.BigDecimal
import java.math.MathContext
import java.util.ArrayDeque

class CalculatorEngine(
    private val tokenizer: ExpressionTokenizer = ExpressionTokenizer(),
    private val parser: ExpressionParser = ExpressionParser()
) {

    private val mathContext = MathContext.DECIMAL128

    // State to support repeated equals (e.g. 5 + 3 = 8, = 11, = 14)
    private var lastOperator: CalculatorOperator? = null
    private var lastOperand: BigDecimal? = null

    /**
     * Fully evaluates an expression string and returns the EvaluationResult.
     */
    fun evaluate(rawExpression: String): EvaluationResult {
        val trimmed = rawExpression.trim()
        if (trimmed.isEmpty()) {
            return EvaluationResult.Error.EmptyExpression
        }

        val tokens = tokenizer.tokenize(trimmed)
        if (tokens.isEmpty()) {
            return EvaluationResult.Error.EmptyExpression
        }

        // Cache last operation for repeated equals if this is a binary expression
        extractAndCacheLastOperation(tokens)

        val rpn = parser.parseToRpn(tokens)
        return evaluateRpn(rpn)
    }

    /**
     * Executes repeated equals using the cached last operation.
     * E.g. If last was 5 + 3 = 8, repeating equals on "8" calculates 8 + 3 = 11.
     */
    fun repeatLastOperation(currentValue: BigDecimal): EvaluationResult {
        val op = lastOperator
        val operand = lastOperand

        if (op == null || operand == null) {
            return EvaluationResult.Success(
                value = currentValue,
                formatted = NumberFormatter.formatResult(currentValue)
            )
        }

        return try {
            val result = when (op) {
                CalculatorOperator.ADD -> currentValue.add(operand, mathContext)
                CalculatorOperator.SUBTRACT -> currentValue.subtract(operand, mathContext)
                CalculatorOperator.MULTIPLY -> currentValue.multiply(operand, mathContext)
                CalculatorOperator.DIVIDE -> {
                    if (operand.compareTo(BigDecimal.ZERO) == 0) {
                        return EvaluationResult.Error.DivisionByZero
                    }
                    currentValue.divide(operand, mathContext)
                }
            }
            EvaluationResult.Success(
                value = result,
                formatted = NumberFormatter.formatResult(result)
            )
        } catch (e: ArithmeticException) {
            EvaluationResult.Error.DivisionByZero
        } catch (e: Exception) {
            EvaluationResult.Error.MalformedExpression
        }
    }

    /**
     * Evaluates the expression for live preview as the user types.
     * If the expression ends with an operator, it evaluates the part before it.
     */
    fun evaluatePreview(rawExpression: String): EvaluationResult? {
        val trimmed = rawExpression.trim()
        if (trimmed.isEmpty()) return null

        // Try evaluating as-is
        val directResult = evaluate(trimmed)
        if (directResult is EvaluationResult.Success) {
            return directResult
        }

        // If direct evaluation failed, strip trailing operator/paren and test again
        var sanitized = trimmed
        while (sanitized.isNotEmpty() && (
                    sanitized.endsWith("+") ||
                            sanitized.endsWith("-") ||
                            sanitized.endsWith("−") ||
                            sanitized.endsWith("*") ||
                            sanitized.endsWith("×") ||
                            sanitized.endsWith("/") ||
                            sanitized.endsWith("÷") ||
                            sanitized.endsWith("(")
                    )) {
            sanitized = sanitized.dropLast(1).trim()
        }

        if (sanitized.isNotEmpty() && sanitized != trimmed) {
            val fallbackResult = evaluate(sanitized)
            if (fallbackResult is EvaluationResult.Success) {
                return fallbackResult
            }
        }

        return null
    }

    fun clearRepeatedOperation() {
        lastOperator = null
        lastOperand = null
    }

    /**
     * Evaluates RPN token list using BigDecimal stack.
     */
    private fun evaluateRpn(rpn: List<Token>): EvaluationResult {
        if (rpn.isEmpty()) {
            return EvaluationResult.Error.EmptyExpression
        }

        val stack = ArrayDeque<BigDecimal>()

        for (token in rpn) {
            when (token.type) {
                TokenType.NUMBER -> {
                    val value = token.value ?: run {
                        try {
                            BigDecimal(token.text)
                        } catch (e: Exception) {
                            BigDecimal.ZERO
                        }
                    }
                    stack.push(value)
                }

                TokenType.PERCENT -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    val percentVal = top.divide(BigDecimal("100"), mathContext)
                    stack.push(percentVal)
                }

                TokenType.UNARY_MINUS -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    stack.push(top.negate())
                }

                TokenType.PLUS -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a.add(b, mathContext))
                }

                TokenType.MINUS -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a.subtract(b, mathContext))
                }

                TokenType.MULTIPLY -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a.multiply(b, mathContext))
                }

                TokenType.DIVIDE -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    if (b.compareTo(BigDecimal.ZERO) == 0) {
                        return EvaluationResult.Error.DivisionByZero
                    }
                    try {
                        stack.push(a.divide(b, mathContext))
                    } catch (e: ArithmeticException) {
                        return EvaluationResult.Error.DivisionByZero
                    }
                }

                TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN -> {
                    // Parens should not be in RPN evaluation stream
                    return EvaluationResult.Error.MalformedExpression
                }
            }
        }

        if (stack.size != 1) {
            return EvaluationResult.Error.MalformedExpression
        }

        val finalValue = stack.pop()
        return EvaluationResult.Success(
            value = finalValue,
            formatted = NumberFormatter.formatResult(finalValue)
        )
    }

    private fun extractAndCacheLastOperation(tokens: List<Token>) {
        if (tokens.size >= 3) {
            val lastToken = tokens.last()
            val secondLast = tokens[tokens.size - 2]
            if (lastToken.type == TokenType.NUMBER && lastToken.value != null) {
                val op = when (secondLast.type) {
                    TokenType.PLUS -> CalculatorOperator.ADD
                    TokenType.MINUS -> CalculatorOperator.SUBTRACT
                    TokenType.MULTIPLY -> CalculatorOperator.MULTIPLY
                    TokenType.DIVIDE -> CalculatorOperator.DIVIDE
                    else -> null
                }
                if (op != null) {
                    lastOperator = op
                    lastOperand = lastToken.value
                }
            }
        }
    }
}
