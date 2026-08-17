package com.example.lihascalculator.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.theme.CalculatorTheme
import com.example.lihascalculator.ui.calculator.CalculatorState
import com.example.lihascalculator.ui.util.FeedbackHelper

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayArea(
    state: CalculatorState,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    onClearAllClick: () -> Unit = onDeleteClick
) {
    val context = LocalContext.current
    val view = LocalView.current
    val colors = CalculatorTheme.colors
    val expressionScrollState = rememberScrollState()
    val resultScrollState = rememberScrollState()

    // Scroll to end instantly without heavy competing animation jobs
    LaunchedEffect(state.expression) {
        if (expressionScrollState.maxValue > 0) {
            expressionScrollState.scrollTo(expressionScrollState.maxValue)
        }
    }
    LaunchedEffect(state.result, state.previewResult) {
        if (resultScrollState.maxValue > 0) {
            resultScrollState.scrollTo(resultScrollState.maxValue)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Top Expression Row + Backspace with Long-press Clear All
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(expressionScrollState),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = state.displayExpression,
                    color = if (state.isCalculated) colors.textSecondary else colors.textPrimary,
                    fontSize = if (state.isCalculated) 24.sp else 34.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.semantics {
                        contentDescription = "Expression: ${state.displayExpression}"
                    }
                )
            }

            AnimatedVisibility(
                visible = state.expression.isNotEmpty() && state.expression != "0",
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                val backspaceInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .combinedClickable(
                            interactionSource = backspaceInteraction,
                            indication = null,
                            onClick = {
                                FeedbackHelper.performHaptics(view, context, true)
                                onDeleteClick()
                            },
                            onLongClick = {
                                FeedbackHelper.performHaptics(view, context, true)
                                onClearAllClick()
                                Toast.makeText(context, "Cleared", Toast.LENGTH_SHORT).show()
                            }
                        )
                        .semantics {
                            role = Role.Button
                            contentDescription = "Backspace (Long press to clear all)"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Result / Live Preview Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(resultScrollState)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        val textToCopy = when {
                            state.isCalculated && state.result.isNotEmpty() -> state.result
                            !state.previewResult.isNullOrEmpty() -> state.previewResult
                            else -> state.expression
                        }

                        if (textToCopy.isNotEmpty() && textToCopy != "0") {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("Calculation Result", textToCopy)
                            clipboard?.setPrimaryClip(clip)
                            FeedbackHelper.performHaptics(view, context, true)
                            Toast.makeText(context, "Copied: $textToCopy", Toast.LENGTH_SHORT).show()
                        }
                    }
                ),
            contentAlignment = Alignment.CenterEnd
        ) {
            when {
                state.isError -> {
                    Text(
                        text = state.errorMessage ?: "Error",
                        color = colors.error,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.semantics {
                            contentDescription = "Error: ${state.errorMessage}"
                        }
                    )
                }

                state.isCalculated -> {
                    val displayText = "= " + state.result
                    val fontSize = calculateAdaptiveFontSize(displayText.length)

                    Text(
                        text = displayText,
                        color = colors.operatorText,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.semantics {
                            contentDescription = "Result: ${state.result}"
                        }
                    )
                }

                !state.previewResult.isNullOrEmpty() -> {
                    val previewText = "= " + state.previewResult
                    val fontSize = calculateAdaptiveFontSize(previewText.length)

                    Text(
                        text = previewText,
                        color = colors.textPreview,
                        fontSize = (fontSize * 0.85f).sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.semantics {
                            contentDescription = "Preview result: ${state.previewResult}"
                        }
                    )
                }

                else -> {
                    Text(
                        text = "",
                        fontSize = 50.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun calculateAdaptiveFontSize(length: Int): Int {
    return when {
        length <= 8 -> 52
        length <= 11 -> 42
        length <= 14 -> 34
        length <= 17 -> 28
        else -> 24
    }
}

