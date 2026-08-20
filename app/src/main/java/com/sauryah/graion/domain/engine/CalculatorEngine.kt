package com.sauryah.graion.domain.engine

import com.sauryah.graion.domain.model.CalculatorOperator
import com.sauryah.graion.domain.model.EvaluationResult
import com.sauryah.graion.domain.model.Token
import com.sauryah.graion.domain.model.TokenType
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
    private var lastOperandIsPercent: Boolean = false

    /**
     * Fully evaluates an expression string and returns the EvaluationResult.
     * Caches the last operation so repeated equals can continue the calculation.
     */
    fun evaluate(rawExpression: String): EvaluationResult {
        return evaluateInternal(rawExpression, cacheLastOperation = true)
    }

    private fun evaluateInternal(rawExpression: String, cacheLastOperation: Boolean): EvaluationResult {
        val trimmed = rawExpression.trim()
        if (trimmed.isEmpty()) {
            return EvaluationResult.Error.EmptyExpression
        }

        val tokens = tokenizer.tokenize(trimmed)
        if (tokens.isEmpty()) {
            return EvaluationResult.Error.EmptyExpression
        }

        // Only cache on an explicit equals evaluation. Live previews must not
        // pollute the repeated-equals state with intermediate expressions.
        if (cacheLastOperation) {
            extractAndCacheLastOperation(tokens)
        }

        val rpn = parser.parseToRpn(tokens)
        return evaluateRpn(rpn)
    }

    /**
     * Executes repeated equals using the cached last operation.
     * E.g. If last was 5 + 3 = 8, repeating equals on "8" calculates 8 + 3 = 11.
     * Percent operands stay relative to the running value: 200 + 10% = 220, = 242.
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
                CalculatorOperator.ADD -> {
                    if (lastOperandIsPercent) {
                        currentValue.add(percentOf(currentValue, operand), mathContext)
                    } else {
                        currentValue.add(operand, mathContext)
                    }
                }
                CalculatorOperator.SUBTRACT -> {
                    if (lastOperandIsPercent) {
                        currentValue.subtract(percentOf(currentValue, operand), mathContext)
                    } else {
                        currentValue.subtract(operand, mathContext)
                    }
                }
                CalculatorOperator.MULTIPLY -> {
                    if (lastOperandIsPercent) {
                        currentValue.multiply(percentOf(BigDecimal.ONE, operand), mathContext)
                    } else {
                        currentValue.multiply(operand, mathContext)
                    }
                }
                CalculatorOperator.DIVIDE -> {
                    val divisor = if (lastOperandIsPercent) percentOf(BigDecimal.ONE, operand) else operand
                    if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                        return EvaluationResult.Error.DivisionByZero
                    }
                    currentValue.divide(divisor, mathContext)
                }
                CalculatorOperator.POWER -> {
                    power(currentValue, operand) ?: return EvaluationResult.Error.MalformedExpression
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

    private fun percentOf(value: BigDecimal, percent: BigDecimal): BigDecimal {
        return value.multiply(percent, mathContext).divide(BigDecimal("100"), mathContext)
    }

    private fun trigResult(compute: () -> Double): BigDecimal {
        val result = compute()
        return if (result.isNaN() || result.isInfinite()) {
            BigDecimal.ZERO
        } else {
            BigDecimal.valueOf(result)
        }
    }

    private fun power(base: BigDecimal, exponent: BigDecimal): BigDecimal? {
        return try {
            val isIntegerExponent = exponent.stripTrailingZeros().scale() <= 0
            val intExponent = exponent.toInt()
            if (isIntegerExponent && intExponent in -1000..1000) {
                when {
                    intExponent == 0 -> BigDecimal.ONE
                    intExponent > 0 -> base.pow(intExponent, mathContext)
                    else -> BigDecimal.ONE.divide(base.pow(-intExponent, mathContext), mathContext)
                }
            } else {
                val result = Math.pow(base.toDouble(), exponent.toDouble())
                if (result.isNaN() || result.isInfinite()) {
                    null
                } else {
                    BigDecimal.valueOf(result)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Evaluates the expression for live preview as the user types.
     * If the expression ends with an operator, it evaluates the part before it.
     * Never mutates the repeated-equals cache.
     */
    fun evaluatePreview(rawExpression: String): EvaluationResult? {
        val trimmed = rawExpression.trim()
        if (trimmed.isEmpty()) return null

        // Try evaluating as-is
        val directResult = evaluateInternal(trimmed, cacheLastOperation = false)
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
            val fallbackResult = evaluateInternal(sanitized, cacheLastOperation = false)
            if (fallbackResult is EvaluationResult.Success) {
                return fallbackResult
            }
        }

        return null
    }

    fun clearRepeatedOperation() {
        lastOperator = null
        lastOperand = null
        lastOperandIsPercent = false
    }

    /**
     * Evaluates RPN token list using BigDecimal stack.
     *
     * Percent operands follow calculator convention:
     *   a + b% = a + a*b/100, a - b% = a - a*b/100, a * b% = a*b/100, a / b% = a/(b/100)
     * A standalone b% still evaluates to b/100.
     */
    private fun evaluateRpn(rpn: List<Token>): EvaluationResult {
        if (rpn.isEmpty()) {
            return EvaluationResult.Error.EmptyExpression
        }

        val stack = ArrayDeque<StackValue>()

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
                    stack.push(StackValue(value, fromPercent = false))
                }

                TokenType.PERCENT -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    val percentVal = top.value.divide(BigDecimal("100"), mathContext)
                    stack.push(StackValue(percentVal, fromPercent = true))
                }

                TokenType.SQRT -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    if (top.value.signum() < 0) return EvaluationResult.Error.MalformedExpression
                    val sqrtVal = BigDecimal.valueOf(kotlin.math.sqrt(top.value.toDouble()))
                    stack.push(StackValue(sqrtVal, fromPercent = false))
                }

                TokenType.POWER -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    val result = power(a.value, b.value) ?: return EvaluationResult.Error.MalformedExpression
                    stack.push(StackValue(result, fromPercent = false))
                }

                TokenType.UNARY_MINUS -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    stack.push(StackValue(top.value.negate(), top.fromPercent))
                }

                TokenType.PLUS -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    // b.value is already divided by 100 by the PERCENT token
                    val result = if (b.fromPercent && !a.fromPercent) {
                        a.value.add(a.value.multiply(b.value, mathContext), mathContext)
                    } else {
                        a.value.add(b.value, mathContext)
                    }
                    stack.push(StackValue(result, fromPercent = false))
                }

                TokenType.MINUS -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    // b.value is already divided by 100 by the PERCENT token
                    val result = if (b.fromPercent && !a.fromPercent) {
                        a.value.subtract(a.value.multiply(b.value, mathContext), mathContext)
                    } else {
                        a.value.subtract(b.value, mathContext)
                    }
                    stack.push(StackValue(result, fromPercent = false))
                }

                TokenType.MULTIPLY -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(StackValue(a.value.multiply(b.value, mathContext), fromPercent = false))
                }

                TokenType.DIVIDE -> {
                    if (stack.size < 2) return EvaluationResult.Error.MalformedExpression
                    val b = stack.pop()
                    val a = stack.pop()
                    if (b.value.compareTo(BigDecimal.ZERO) == 0) {
                        return EvaluationResult.Error.DivisionByZero
                    }
                    try {
                        stack.push(StackValue(a.value.divide(b.value, mathContext), fromPercent = false))
                    } catch (e: ArithmeticException) {
                        return EvaluationResult.Error.DivisionByZero
                    }
                }

                TokenType.SIN -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    stack.push(StackValue(trigResult { Math.sin(Math.toRadians(top.value.toDouble())) }, fromPercent = false))
                }

                TokenType.COS -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    stack.push(StackValue(trigResult { Math.cos(Math.toRadians(top.value.toDouble())) }, fromPercent = false))
                }

                TokenType.TAN -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    stack.push(StackValue(trigResult { Math.tan(Math.toRadians(top.value.toDouble())) }, fromPercent = false))
                }

                TokenType.LN -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    if (top.value.signum() <= 0) return EvaluationResult.Error.MalformedExpression
                    stack.push(StackValue(trigResult { Math.log(top.value.toDouble()) }, fromPercent = false))
                }

                TokenType.LOG -> {
                    if (stack.isEmpty()) return EvaluationResult.Error.MalformedExpression
                    val top = stack.pop()
                    if (top.value.signum() <= 0) return EvaluationResult.Error.MalformedExpression
                    stack.push(StackValue(trigResult { Math.log10(top.value.toDouble()) }, fromPercent = false))
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
            value = finalValue.value,
            formatted = NumberFormatter.formatResult(finalValue.value)
        )
    }

    private fun extractAndCacheLastOperation(tokens: List<Token>) {
        if (tokens.size < 2) return

        val lastToken = tokens.last()
        val secondLast = tokens[tokens.size - 2]

        // Handle percent operand: "200 + 10%" -> cache ADD with percent operand 10
        if (lastToken.type == TokenType.PERCENT && secondLast.type == TokenType.NUMBER && secondLast.value != null) {
            val op = tokens.getOrNull(tokens.size - 3)?.let { operatorOf(it.type) }
            if (op != null) {
                lastOperator = op
                lastOperand = secondLast.value
                lastOperandIsPercent = true
            }
            return
        }

        if (lastToken.type == TokenType.NUMBER && lastToken.value != null) {
            val op = operatorOf(secondLast.type)
            if (op != null) {
                lastOperator = op
                lastOperand = lastToken.value
                lastOperandIsPercent = false
            }
        }
    }

    private fun operatorOf(type: TokenType): CalculatorOperator? {
        return when (type) {
            TokenType.PLUS -> CalculatorOperator.ADD
            TokenType.MINUS -> CalculatorOperator.SUBTRACT
            TokenType.MULTIPLY -> CalculatorOperator.MULTIPLY
            TokenType.DIVIDE -> CalculatorOperator.DIVIDE
            else -> null
        }
    }

    private data class StackValue(
        val value: BigDecimal,
        val fromPercent: Boolean
    )
}
