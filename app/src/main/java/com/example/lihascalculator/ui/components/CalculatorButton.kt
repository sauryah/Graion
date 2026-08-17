package com.example.lihascalculator.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.theme.CalculatorColors
import com.example.lihascalculator.theme.CalculatorTheme
import com.example.lihascalculator.ui.util.FeedbackHelper

private val ButtonShape = RoundedCornerShape(24.dp)

private val PressSpringSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMedium
)

@Composable
fun CalculatorButton(
    symbol: String,
    type: CalculatorButtonType,
    modifier: Modifier = Modifier,
    contentDesc: String = symbol,
    hapticsEnabled: Boolean = true,
    soundEnabled: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val currentOnClick by rememberUpdatedState(onClick)
    val currentHaptics by rememberUpdatedState(hapticsEnabled)
    val currentSound by rememberUpdatedState(soundEnabled)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Crisp tactile scale without sluggish bounce
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = PressSpringSpec,
        label = "btn_scale"
    )

    val colors = CalculatorTheme.colors
    val backgroundColor = getBackgroundColor(type, colors)
    val contentColor = getContentColor(type, colors)
    val fontSize = getFontSize(type)
    val fontWeight = getFontWeight(type)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(ButtonShape)
            .background(
                color = if (isPressed) {
                    if (colors.isDark) backgroundColor.copy(alpha = 0.85f) else backgroundColor.copy(alpha = 0.9f)
                } else {
                    backgroundColor
                },
                shape = ButtonShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    FeedbackHelper.performHaptics(view, context, currentHaptics)
                    FeedbackHelper.performSound(context, currentSound)
                    currentOnClick()
                }
            )
            .semantics {
                role = Role.Button
                contentDescription = contentDesc
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = contentColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center
        )
    }
}

private fun getBackgroundColor(type: CalculatorButtonType, colors: CalculatorColors): Color {
    return when (type) {
        CalculatorButtonType.NUMBER -> colors.numberButton
        CalculatorButtonType.FUNCTION -> colors.functionButton
        CalculatorButtonType.OPERATOR -> colors.operatorButton
        CalculatorButtonType.EQUALS -> colors.equalsButton
    }
}

private fun getContentColor(type: CalculatorButtonType, colors: CalculatorColors): Color {
    return when (type) {
        CalculatorButtonType.NUMBER -> colors.numberText
        CalculatorButtonType.FUNCTION -> colors.functionText
        CalculatorButtonType.OPERATOR -> colors.operatorText
        CalculatorButtonType.EQUALS -> colors.equalsText
    }
}

private fun getFontSize(type: CalculatorButtonType): TextUnit {
    return when (type) {
        CalculatorButtonType.NUMBER -> 28.sp
        CalculatorButtonType.FUNCTION -> 22.sp
        CalculatorButtonType.OPERATOR -> 30.sp
        CalculatorButtonType.EQUALS -> 32.sp
    }
}

private fun getFontWeight(type: CalculatorButtonType): FontWeight {
    return when (type) {
        CalculatorButtonType.NUMBER -> FontWeight.Medium
        CalculatorButtonType.FUNCTION -> FontWeight.SemiBold
        CalculatorButtonType.OPERATOR -> FontWeight.Normal
        CalculatorButtonType.EQUALS -> FontWeight.SemiBold
    }
}

