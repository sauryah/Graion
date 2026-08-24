package com.sauryah.graion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sauryah.graion.domain.model.CalculatorAction
import com.sauryah.graion.domain.model.CalculatorConstant
import com.sauryah.graion.domain.model.CalculatorFunction
import com.sauryah.graion.domain.model.CalculatorOperator

@Composable
fun CalculatorKeypad(
    modifier: Modifier = Modifier,
    hapticsEnabled: Boolean = true,
    soundEnabled: Boolean = false,
    onAction: (CalculatorAction) -> Unit
) {
    var functionPage by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row -1: Scientific keys (4-Page cyclic navigation)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (functionPage) {
                0 -> {
                    CalculatorButton(
                        symbol = "sin",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Sine",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.SIN)) }
                    )
                    CalculatorButton(
                        symbol = "cos",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Cosine",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.COS)) }
                    )
                    CalculatorButton(
                        symbol = "tan",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Tangent",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.TAN)) }
                    )
                    CalculatorButton(
                        symbol = "ln",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Natural Logarithm",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.LN)) }
                    )
                }

                1 -> {
                    CalculatorButton(
                        symbol = "asin",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Arc Sine",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.ASIN)) }
                    )
                    CalculatorButton(
                        symbol = "acos",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Arc Cosine",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.ACOS)) }
                    )
                    CalculatorButton(
                        symbol = "atan",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Arc Tangent",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.ATAN)) }
                    )
                    CalculatorButton(
                        symbol = "!",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Factorial",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.FACTORIAL)) }
                    )
                }

                2 -> {
                    CalculatorButton(
                        symbol = "π",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Pi",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Constant(CalculatorConstant.PI)) }
                    )
                    CalculatorButton(
                        symbol = "e",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Euler's number",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Constant(CalculatorConstant.EULER)) }
                    )
                    CalculatorButton(
                        symbol = "√",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Square Root",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.SquareRoot) }
                    )
                    CalculatorButton(
                        symbol = "∛",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Cube Root",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.CBRT)) }
                    )
                }

                else -> {
                    CalculatorButton(
                        symbol = "log",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Base-10 Logarithm",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.LOG)) }
                    )
                    CalculatorButton(
                        symbol = "abs",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Absolute Value",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Function(CalculatorFunction.ABS)) }
                    )
                    CalculatorButton(
                        symbol = "^",
                        type = CalculatorButtonType.OPERATOR,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Power",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Operator(CalculatorOperator.POWER)) }
                    )
                    CalculatorButton(
                        symbol = "%",
                        type = CalculatorButtonType.FUNCTION,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentDesc = "Percentage",
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onAction(CalculatorAction.Percentage) }
                    )
                }
            }

            CalculatorButton(
                symbol = "𝑓x",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Switch scientific function page (Page ${functionPage + 1} of 4)",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { functionPage = (functionPage + 1) % 4 }
            )
        }

        // Row 0: MC | MR | M- | M+
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                symbol = "MC",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Memory Clear",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.MemoryClear) }
            )
            CalculatorButton(
                symbol = "MR",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Memory Recall",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.MemoryRecall) }
            )
            CalculatorButton(
                symbol = "M-",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Memory Subtract",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.MemorySubtract) }
            )
            CalculatorButton(
                symbol = "M+",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Memory Add",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.MemoryAdd) }
            )
        }

        // Row 1: AC | ( ) | % | ÷
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                symbol = "AC",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "All Clear",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Clear) }
            )
            CalculatorButton(
                symbol = "( )",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Parentheses",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Parentheses) }
            )
            CalculatorButton(
                symbol = "%",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Percentage",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Percentage) }
            )
            CalculatorButton(
                symbol = "÷",
                type = CalculatorButtonType.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Divide",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Operator(CalculatorOperator.DIVIDE)) }
            )
        }

        // Row 2: 7 | 8 | 9 | ×
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                symbol = "7",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Seven",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(7)) }
            )
            CalculatorButton(
                symbol = "8",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Eight",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(8)) }
            )
            CalculatorButton(
                symbol = "9",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Nine",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(9)) }
            )
            CalculatorButton(
                symbol = "×",
                type = CalculatorButtonType.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Multiply",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Operator(CalculatorOperator.MULTIPLY)) }
            )
        }

        // Row 3: 4 | 5 | 6 | −
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                symbol = "4",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Four",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(4)) }
            )
            CalculatorButton(
                symbol = "5",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Five",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(5)) }
            )
            CalculatorButton(
                symbol = "6",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Six",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(6)) }
            )
            CalculatorButton(
                symbol = "−",
                type = CalculatorButtonType.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Subtract",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Operator(CalculatorOperator.SUBTRACT)) }
            )
        }

        // Row 4: 1 | 2 | 3 | +
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                symbol = "1",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "One",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(1)) }
            )
            CalculatorButton(
                symbol = "2",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Two",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(2)) }
            )
            CalculatorButton(
                symbol = "3",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Three",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(3)) }
            )
            CalculatorButton(
                symbol = "+",
                type = CalculatorButtonType.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Add",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Operator(CalculatorOperator.ADD)) }
            )
        }

        // Row 5: +/- | 0 | . | =
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                symbol = "+/-",
                type = CalculatorButtonType.FUNCTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Toggle Sign",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.ToggleSign) }
            )
            CalculatorButton(
                symbol = "0",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Zero",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Number(0)) }
            )
            CalculatorButton(
                symbol = ".",
                type = CalculatorButtonType.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Decimal Point",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Decimal) }
            )
            CalculatorButton(
                symbol = "=",
                type = CalculatorButtonType.EQUALS,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentDesc = "Equals",
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onClick = { onAction(CalculatorAction.Calculate) }
            )
        }
    }
}
