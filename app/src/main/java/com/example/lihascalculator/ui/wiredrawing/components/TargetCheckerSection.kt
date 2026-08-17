package com.example.lihascalculator.ui.wiredrawing.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.lihascalculator.domain.model.wiredrawing.TargetCheckResult
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun TargetCheckerSection(
    targetChecks: List<TargetCheckResult>,
    minTarget: Double,
    maxTarget: Double,
    colors: CalculatorColors,
    onLimitsChange: (min: Double, max: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var minText by remember(minTarget) { mutableStateOf(String.format(Locale.US, "%.1f", minTarget)) }
    var maxText by remember(maxTarget) { mutableStateOf(String.format(Locale.US, "%.1f", maxTarget)) }

    val outOfRangeCount = targetChecks.count { it.isOutOfRange }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TARGET ELONGATION LIMIT CHECKER",
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (outOfRangeCount > 0) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (outOfRangeCount > 0) "$outOfRangeCount OUT OF RANGE" else "ALL IN RANGE",
                        color = if (outOfRangeCount > 0) Color(0xFFEF4444) else Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                        if (min != null && max != null && min > 0) {
                            onLimitsChange(min, max)
                        }
                    },
                    label = { Text("Min Elongation %") },
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
                        if (min != null && max != null && max > 0) {
                            onLimitsChange(min, max)
                        }
                    },
                    label = { Text("Max Elongation %") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Passes List
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                targetChecks.forEach { check ->
                    val bg = if (check.isOutOfRange) Color(0xFFEF4444).copy(alpha = 0.08f) else colors.background.copy(alpha = 0.4f)
                    val borderModifier = if (check.isOutOfRange) Modifier.border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(6.dp)) else Modifier

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(borderModifier)
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
                                    text = "PASS #${check.passNumber}: ${String.format(Locale.US, "%.3f → %.3f mm", check.fromDie, check.toDie)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                    color = colors.textPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Elongation: ${String.format(Locale.US, "%.2f%%", check.elongation)}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (check.isOutOfRange) Color(0xFFEF4444) else colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (check.isOutOfRange) "OUT OF RANGE" else "OK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (check.isOutOfRange) Color(0xFFEF4444) else Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
