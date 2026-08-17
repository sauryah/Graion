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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.model.wiredrawing.ConsistencyResult
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun ConsistencyDetailDialog(
    isOpen: Boolean,
    consistency: ConsistencyResult,
    colors: CalculatorColors,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pass Consistency Rating",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Consistency evaluates the uniformity of drawing elongation across all reduction passes.",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Rating", fontSize = 13.sp, color = colors.textSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= consistency.stars) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = if (i <= consistency.stars) Color(0xFFF59E0B) else colors.surfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = consistency.rating.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Average Elongation", fontSize = 13.sp, color = colors.textSecondary)
                            Text(
                                text = String.format(Locale.US, "%.2f%%", consistency.avgElongation),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Maximum Deviation", fontSize = 13.sp, color = colors.textSecondary)
                            Text(
                                text = "±${String.format(Locale.US, "%.2f%%", consistency.maxDeviation)}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Standard Scale:\n• ≤ 1.0% = 5 Stars (Excellent)\n• ≤ 2.0% = 4 Stars (Very Good)\n• ≤ 3.0% = 3 Stars (Good)\n• ≤ 5.0% = 2 Stars (Fair)\n• > 5.0% = 1 Star (Poor)",
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = colors.accentPrimary)
            }
        },
        dismissButton = {}
    )
}
