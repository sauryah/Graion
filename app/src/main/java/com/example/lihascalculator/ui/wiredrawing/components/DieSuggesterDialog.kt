package com.example.lihascalculator.ui.wiredrawing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.model.wiredrawing.SuggestedIntermediatePass
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun DieSuggesterDialog(
    isOpen: Boolean,
    suggestions: List<SuggestedIntermediatePass>,
    targetElongation: Double,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onTargetChange: (Double) -> Unit,
    onApplySuggestions: () -> Unit
) {
    if (!isOpen) return

    var targetText by remember(targetElongation) {
        mutableStateOf(String.format(Locale.US, "%.1f", targetElongation))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = null,
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Intermediate Die Suggester",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Automatically detect large reductions and insert intermediate steps to smooth elongation.",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = targetText,
                    onValueChange = {
                        targetText = it
                        val d = it.toDoubleOrNull()
                        if (d != null && d > 0.0) onTargetChange(d)
                    },
                    label = { Text("Target Elongation %") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (suggestions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF10B981).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All passes are consistent (within ±2% of target ${String.format(Locale.US, "%.1f", targetElongation)}%). No intermediate dies needed.",
                            color = Color(0xFF10B981),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "${suggestions.size} improvement(s) found:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(suggestions, key = { it.passIndex }) { sugg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "PASS #${sugg.passIndex}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.accentPrimary
                                        )
                                        Text(
                                            text = "Elongation: ${String.format(Locale.US, "%.1f%%", sugg.currentElongation)}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFF59E0B),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "Current: ${String.format(Locale.US, "%.3f → %.3f", sugg.fromDie, sugg.toDie)}",
                                        fontSize = 11.sp,
                                        color = colors.textSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    val proposedStr = sugg.proposedDies.joinToString(" → ") { String.format(Locale.US, "%.3f", it) }
                                    Text(
                                        text = "Suggested: $proposedStr",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (suggestions.isNotEmpty()) {
                Button(
                    onClick = onApplySuggestions,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("APPLY TO SCHEDULE", fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = colors.accentPrimary)
                }
            }
        },
        dismissButton = {
            if (suggestions.isNotEmpty()) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        }
    )
}
