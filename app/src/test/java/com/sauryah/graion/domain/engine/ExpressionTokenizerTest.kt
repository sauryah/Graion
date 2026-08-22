package com.sauryah.graion.domain.engine

import com.sauryah.graion.domain.model.TokenType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class ExpressionTokenizerTest {

    private lateinit var tokenizer: ExpressionTokenizer

    @Before
    fun setup() {
        tokenizer = ExpressionTokenizer()
    }

    @Test
    fun testEmptyAndBlankExpressions() {
        assertEquals(0, tokenizer.tokenize("").size)
        assertEquals(0, tokenizer.tokenize("   ").size)
    }

    @Test
    fun testBasicArithmeticTokenization() {
        val tokens = tokenizer.tokenize("12 + 34.56 * 78 / 9")
        assertEquals(7, tokens.size)
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("12", tokens[0].text)
        assertEquals(TokenType.PLUS, tokens[1].type)
        assertEquals(TokenType.NUMBER, tokens[2].type)
        assertEquals("34.56", tokens[2].text)
        assertEquals(TokenType.MULTIPLY, tokens[3].type)
        assertEquals(TokenType.NUMBER, tokens[4].type)
        assertEquals("78", tokens[4].text)
        assertEquals(TokenType.DIVIDE, tokens[5].type)
        assertEquals(TokenType.NUMBER, tokens[6].type)
        assertEquals("9", tokens[6].text)
    }

    @Test
    fun testUnicodeOperatorNormalization() {
        val tokens = tokenizer.tokenize("10 × 5 ÷ 2 − 1")
        assertEquals(7, tokens.size)
        assertEquals(TokenType.MULTIPLY, tokens[1].type)
        assertEquals(TokenType.DIVIDE, tokens[3].type)
        assertEquals(TokenType.MINUS, tokens[5].type)
    }

    @Test
    fun testUnaryMinusDetection() {
        // Leading minus is unary
        val t1 = tokenizer.tokenize("-5 + 10")
        assertEquals(TokenType.UNARY_MINUS, t1[0].type)
        assertEquals(TokenType.NUMBER, t1[1].type)
        assertEquals(TokenType.PLUS, t1[2].type)

        // Minus after operator is unary
        val t2 = tokenizer.tokenize("3 * -2")
        assertEquals(TokenType.NUMBER, t2[0].type)
        assertEquals(TokenType.MULTIPLY, t2[1].type)
        assertEquals(TokenType.UNARY_MINUS, t2[2].type)
        assertEquals(TokenType.NUMBER, t2[3].type)

        // Minus after left paren is unary
        val t3 = tokenizer.tokenize("(-5)")
        assertEquals(TokenType.LEFT_PAREN, t3[0].type)
        assertEquals(TokenType.UNARY_MINUS, t3[1].type)
        assertEquals(TokenType.NUMBER, t3[2].type)
        assertEquals(TokenType.RIGHT_PAREN, t3[3].type)
    }

    @Test
    fun testImplicitMultiplication() {
        // 2(3) -> 2 * ( 3 )
        val t1 = tokenizer.tokenize("2(3)")
        assertEquals(listOf(TokenType.NUMBER, TokenType.MULTIPLY, TokenType.LEFT_PAREN, TokenType.NUMBER, TokenType.RIGHT_PAREN), t1.map { it.type })

        // (2)(3) -> ( 2 ) * ( 3 )
        val t2 = tokenizer.tokenize("(2)(3)")
        assertEquals(listOf(TokenType.LEFT_PAREN, TokenType.NUMBER, TokenType.RIGHT_PAREN, TokenType.MULTIPLY, TokenType.LEFT_PAREN, TokenType.NUMBER, TokenType.RIGHT_PAREN), t2.map { it.type })

        // 2π -> 2 * π
        val t3 = tokenizer.tokenize("2π")
        assertEquals(listOf(TokenType.NUMBER, TokenType.MULTIPLY, TokenType.NUMBER), t3.map { it.type })

        // 2sin(30) -> 2 * sin ( 30 )
        val t4 = tokenizer.tokenize("2sin(30)")
        assertEquals(listOf(TokenType.NUMBER, TokenType.MULTIPLY, TokenType.SIN, TokenType.LEFT_PAREN, TokenType.NUMBER, TokenType.RIGHT_PAREN), t4.map { it.type })

        // 5%2 -> 5 % * 2
        val t5 = tokenizer.tokenize("5%2")
        assertEquals(listOf(TokenType.NUMBER, TokenType.PERCENT, TokenType.MULTIPLY, TokenType.NUMBER), t5.map { it.type })
    }

    @Test
    fun testConstantsAndSpecialTokens() {
        val tokens = tokenizer.tokenize("π + e + 9√ + 2^3")
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("π", tokens[0].text)
        assertEquals(BigDecimal("3.14159265358979323846264338327950288"), tokens[0].value)

        assertEquals(TokenType.NUMBER, tokens[2].type)
        assertEquals("e", tokens[2].text)

        assertEquals(TokenType.SQRT, tokens[5].type)
        assertEquals(TokenType.POWER, tokens[8].type)
    }

    @Test
    fun testLeadingAndTrailingDots() {
        val t1 = tokenizer.tokenize(".5 + 5.")
        assertEquals("0.5", t1[0].text)
        assertEquals("5", t1[2].text)
    }
}
