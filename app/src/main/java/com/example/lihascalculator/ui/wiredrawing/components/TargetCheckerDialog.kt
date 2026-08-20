package com.sauryah.lihas.calculator.ui.wiredrawing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import com.sauryah.lihas.calculator.domain.model.wiredrawing.TargetCheckResult
import com.sauryah.lihas.calculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun TargetCheckerDialog(
    isOpen: Boolean,
    targetChecks: List<TargetCheckResult>,
    minTarget: Double,
    maxTarget: Double,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onLimitsChange: (min: Double, max: Double) -> Unit
) {
    if (!isOpen) return

    var minText by remember(minTarget) { mutableStateOf(String.format(Locale.US, "%.1f", minTarget)) }
    var maxText by remember(maxTarget) { mutableStateOf(String.format(Locale.US, "%.1f", maxTarget)) }

    val outOfRangeCount = targetChecks.count { it.isOutOfRange }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = null,
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Target Elongation Checker",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Verify whether drawing passes fall within your acceptable elongation tolerance limits.",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Limits Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minText,
                        onValueChange = {
                            minText = it
                            val min = it.toDoubleOrNull()
                            val max = maxText.toDoubleOrNull()
                            if (min != null && max != null && min > 0) onLimitsChange(min, max)
                        },
                        label = { Text("Min %") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    )

                    OutlinedTextField(
                        value = maxText,
                        onValueChange = {
                            maxText = it
                            val min = minText.toDoubleOrNull()
                            val max = it.toDoubleOrNull()
                            if (min != null && max != null && max > 0) onLimitsChange(min, max)
                        },
                        label = { Text("Max %") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (outOfRangeCount > 0) Color(0xFFEF4444).copy(alpha = 0.12f) else Color(0xFF10B981).copy(alpha = 0.12f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (outOfRangeCount > 0) "$outOfRangeCount passes outside target (${minTarget}%–${maxTarget}%)" else "All passes are within ${minTarget}%–${maxTarget}%",
                        color = if (outOfRangeCount > 0) Color(0xFFEF4444) else Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Passes Results List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(targetChecks, key = { it.passNumber }) { check ->
                        val bg = if (check.isOutOfRange) Color(0xFFEF4444).copy(alpha = 0.08f) else colors.background.copy(alpha = 0.4f)
                        val border = if (check.isOutOfRange) Modifier.border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(6.dp)) else Modifier

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(border)
                                .background(bg, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (check.isOutOfRange) Icons.Default.Warning else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (check.isOutOfRange) Color(0xFFEF4444) else Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PASS #${check.passNumber}: ${String.format(Locale.US, "%.3f → %.3f", check.fromDie, check.toDie)}",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = colors.textPrimary
                                    )
                                }

                                Text(
                                    text = String.format(Locale.US, "%.2f%%", check.elongation),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (check.isOutOfRange) Color(0xFFEF4444) else colors.textPrimary
                                )
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
        },
        dismissButton = {}
    )
}
