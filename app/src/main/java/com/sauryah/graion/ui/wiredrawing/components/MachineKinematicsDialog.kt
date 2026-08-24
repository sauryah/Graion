package com.sauryah.graion.ui.wiredrawing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.graion.domain.engine.wiredrawing.WireDrawingCalculatorEngine
import com.sauryah.graion.domain.model.wiredrawing.PassResult
import com.sauryah.graion.domain.model.wiredrawing.WireMaterial
import com.sauryah.graion.theme.CalculatorColors
import java.util.Locale

@Composable
fun MachineKinematicsDialog(
    isOpen: Boolean,
    passes: List<PassResult>,
    colors: CalculatorColors,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var finishSpeedText by remember { mutableStateOf("20.0") }
    val finishSpeed = finishSpeedText.toDoubleOrNull() ?: 0.0

    val kinematics = WireDrawingCalculatorEngine.calculateMachineKinematics(
        passes = passes,
        finishSpeedMPerS = finishSpeed,
        material = WireMaterial.COPPER
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = colors.accentPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Line Speed & Kinematics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Calculates capstan drawing speeds per pass and production rate based on finish line speed.",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                OutlinedTextField(
                    value = finishSpeedText,
                    onValueChange = { finishSpeedText = it },
                    label = { Text("Finish Line Speed (m/s)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Production Summary Cards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Inlet Speed:", fontSize = 12.sp, color = colors.textSecondary)
                            Text(
                                String.format(Locale.US, "%.3f m/s (%.1f m/min)", kinematics.inletSpeedMPerS, kinematics.inletSpeedMPerS * 60.0),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Production Rate (Cu):", fontSize = 12.sp, color = colors.textSecondary)
                            Text(
                                String.format(Locale.US, "%.2f kg/hr", kinematics.productionRateKgPerHour),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.accentPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("8-Hour Shift Output:", fontSize = 12.sp, color = colors.textSecondary)
                            Text(
                                String.format(Locale.US, "%.3f Tonnes", kinematics.productionRateTonnesPer8HrShift),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textSecondary
                            )
                        }
                    }
                }

                // Per-Pass Speed Table
                if (kinematics.passSpeeds.isNotEmpty()) {
                    Text(
                        text = "CAPSTAN SPEED SCHEDULE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary,
                        letterSpacing = 0.5.sp
                    )

                    val tableScroll = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tableScroll)
                    ) {
                        Column {
                            // Header
                            Row(
                                modifier = Modifier
                                    .background(colors.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(vertical = 6.dp, horizontal = 6.dp)
                            ) {
                                TableCell("PASS", width = 50.dp, colors = colors, isHeader = true)
                                TableCell("DIE (mm)", width = 75.dp, colors = colors, isHeader = true)
                                TableCell("SPEED (m/s)", width = 90.dp, colors = colors, isHeader = true)
                                TableCell("SPEED (m/min)", width = 100.dp, colors = colors, isHeader = true)
                                TableCell("RATIO", width = 65.dp, colors = colors, isHeader = true)
                            }

                            // Rows
                            kinematics.passSpeeds.forEachIndexed { index, p ->
                                val rowBg = if (index % 2 == 0) colors.surfaceVariant.copy(alpha = 0.15f) else Color.Transparent
                                Row(
                                    modifier = Modifier
                                        .background(rowBg, RoundedCornerShape(4.dp))
                                        .padding(vertical = 4.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TableCell("#${p.passNumber}", width = 50.dp, colors = colors)
                                    TableCell(String.format(Locale.US, "%.3f", p.dieDiameterMm), width = 75.dp, colors = colors)
                                    TableCell(String.format(Locale.US, "%.3f", p.wireSpeedMPerS), width = 90.dp, colors = colors, isBold = true, textColor = colors.accentPrimary)
                                    TableCell(String.format(Locale.US, "%.1f", p.wireSpeedMPerMin), width = 100.dp, colors = colors)
                                    TableCell(String.format(Locale.US, "%.2fx", p.speedRatioToInlet), width = 65.dp, colors = colors)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = colors.accentPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    colors: CalculatorColors,
    isHeader: Boolean = false,
    isBold: Boolean = false,
    textColor: Color? = null
) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 2.dp),
        fontFamily = if (isHeader) FontFamily.Default else FontFamily.Monospace,
        fontSize = if (isHeader) 10.sp else 11.sp,
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        color = textColor ?: if (isHeader) colors.textSecondary else colors.textPrimary,
        textAlign = TextAlign.End
    )
}
