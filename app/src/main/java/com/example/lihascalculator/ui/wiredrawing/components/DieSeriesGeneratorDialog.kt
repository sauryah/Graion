package com.example.lihascalculator.ui.wiredrawing.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.engine.wiredrawing.WireDrawingCalculatorEngine
import com.example.lihascalculator.theme.CalculatorColors

@Composable
fun DieSeriesGeneratorDialog(
    isOpen: Boolean,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onGenerate: (dStart: Double, dEnd: Double, targetElongation: Double, finalMin: Double?, finalMax: Double?) -> Unit
) {
    if (!isOpen) return

    var dStartText by remember { mutableStateOf("2.500") }
    var dEndText by remember { mutableStateOf("0.500") }
    var targetElongationText by remember { mutableStateOf("20.0") }
    var finalMinText by remember { mutableStateOf("") }
    var finalMaxText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Generate Constant-Elongation Series",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Generate a geometrically stepped sequence of die diameters between starting and ending sizes.",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dStartText,
                        onValueChange = { dStartText = it; errorMessage = null },
                        label = { Text("Start Die (mm)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = dEndText,
                        onValueChange = { dEndText = it; errorMessage = null },
                        label = { Text("Final Die (mm)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetElongationText,
                    onValueChange = { targetElongationText = it; errorMessage = null },
                    label = { Text("Target Elongation % (e.g. 20.0)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Optional Final-Pass Elongation Range (%):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = finalMinText,
                        onValueChange = { finalMinText = it },
                        label = { Text("Min %") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = finalMaxText,
                        onValueChange = { finalMaxText = it },
                        label = { Text("Max %") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dStart = dStartText.toDoubleOrNull()
                    val dEnd = dEndText.toDoubleOrNull()
                    val elongation = targetElongationText.toDoubleOrNull()

                    if (dStart == null || dEnd == null || elongation == null || dStart <= 0 || dEnd <= 0 || dStart <= dEnd || elongation <= 0) {
                        errorMessage = "Please enter valid numbers (Start > End > 0, Elongation > 0)"
                        return@Button
                    }

                    val finalMin = finalMinText.toDoubleOrNull()
                    val finalMax = finalMaxText.toDoubleOrNull()

                    onGenerate(dStart, dEnd, elongation, finalMin, finalMax)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary)
            ) {
                Text("Generate & Apply", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}
