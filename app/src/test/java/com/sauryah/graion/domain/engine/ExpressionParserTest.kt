package com.sauryah.graion.domain.engine

import com.sauryah.graion.domain.model.TokenType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExpressionParserTest {

    private lateinit var tokenizer: ExpressionTokenizer
    private lateinit var parser: ExpressionParser

    @Before
    fun setup() {
        tokenizer = ExpressionTokenizer()
        parser = ExpressionParser()
    }

    @Test
    fun testEmptyTokenList() {
        val rpn = parser.parseToRpn(emptyList())
        assertEquals(0, rpn.size)
    }

    @Test
    fun testOperatorPrecedenceRpn() {
        // Infix: 2 + 3 * 4 -> RPN: 2 3 4 * +
        val tokens = tokenizer.tokenize("2 + 3 * 4")
        val rpn = parser.parseToRpn(tokens)
        val expectedTypes = listOf(
            TokenType.NUMBER,
            TokenType.NUMBER,
            TokenType.NUMBER,
            TokenType.MULTIPLY,
            TokenType.PLUS
        )
        assertEquals(expectedTypes, rpn.map { it.type })
        assertEquals(listOf("2", "3", "4", "*", "+"), rpn.map { it.text })
    }

    @Test
    fun testParenthesesOverridingPrecedence() {
        // Infix: (2 + 3) * 4 -> RPN: 2 3 + 4 *
        val tokens = tokenizer.tokenize("(2 + 3) * 4")
        val rpn = parser.parseToRpn(tokens)
        val expectedTypes = listOf(
            TokenType.NUMBER,
            TokenType.NUMBER,
            TokenType.PLUS,
            TokenType.NUMBER,
            TokenType.MULTIPLY
        )
        assertEquals(expectedTypes, rpn.map { it.type })
        assertEquals(listOf("2", "3", "+", "4", "*"), rpn.map { it.text })
    }

    @Test
    fun testPowerAssociativity() {
        // Infix: 2 ^ 3 ^ 2 -> Right associative: 2 ^ (3 ^ 2) -> RPN: 2 3 2 ^ ^
        val tokens = tokenizer.tokenize("2 ^ 3 ^ 2")
        val rpn = parser.parseToRpn(tokens)
        assertEquals(listOf("2", "3", "2", "^", "^"), rpn.map { it.text })
    }

    @Test
    fun testFunctionsInRpn() {
        // Infix: sin(30) + 1 -> RPN: 30 sin 1 +
        val tokens = tokenizer.tokenize("sin(30) + 1")
        val rpn = parser.parseToRpn(tokens)
        assertEquals(listOf("30", "sin", "1", "+"), rpn.map { it.text })
    }

    @Test
    fun testUnbalancedOpenParenthesesAutoBalancing() {
        // Infix: ((2 + 3) * (4 + 1 -> RPN: 2 3 + 4 1 + *
        val tokens = tokenizer.tokenize("((2 + 3) * (4 + 1")
        val rpn = parser.parseToRpn(tokens)
        assertEquals(listOf("2", "3", "+", "4", "1", "+", "*"), rpn.map { it.text })
    }
}
