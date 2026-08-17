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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.lihascalculator.domain.model.wiredrawing.SuggestedIntermediatePass
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun DieSuggesterSection(
    suggestions: List<SuggestedIntermediatePass>,
    targetElongation: Double,
    colors: CalculatorColors,
    onTargetChange: (Double) -> Unit,
    onApplySuggestions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var targetText by remember(targetElongation) {
        mutableStateOf(String.format(Locale.US, "%.1f", targetElongation))
    }

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
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INTERMEDIATE DIE SUGGESTER",
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                if (suggestions.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF59E0B).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${suggestions.size} TO OPTIMIZE",
                            color = Color(0xFFF59E0B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Elongation Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = targetText,
                    onValueChange = {
                        targetText = it
                        val d = it.toDoubleOrNull()
                        if (d != null && d > 0.0) {
                            onTargetChange(d)
                        }
                    },
                    label = { Text("Target Elongation %") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                if (suggestions.isNotEmpty()) {
                    Button(
                        onClick = onApplySuggestions,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply to Schedule", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (suggestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All passes are within ±2% of target elongation (${String.format(Locale.US, "%.1f", targetElongation)}%). No intermediate dies needed.",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    suggestions.forEach { sugg ->
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
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.accentPrimary
                                    )
                                    Text(
                                        text = "Current Elongation: ${String.format(Locale.US, "%.2f%%", sugg.currentElongation)}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFF59E0B)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Current: ${String.format(Locale.US, "%.3f", sugg.fromDie)} → ${String.format(Locale.US, "%.3f", sugg.toDie)}",
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                    fontFamily = FontFamily.Monospace
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                val proposedStr = sugg.proposedDies.joinToString(" → ") { String.format(Locale.US, "%.3f", it) }
                                Text(
                                    text = "Proposed: $proposedStr",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
