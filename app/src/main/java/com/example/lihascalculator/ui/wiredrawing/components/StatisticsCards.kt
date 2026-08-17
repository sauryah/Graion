package com.example.lihascalculator.ui.wiredrawing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.model.wiredrawing.ConsistencyResult
import com.example.lihascalculator.domain.model.wiredrawing.WireDrawingStats
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatisticsCards(
    stats: WireDrawingStats,
    consistency: ConsistencyResult,
    colors: CalculatorColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Consistency Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SCHEDULE CONSISTENCY",
                            color = colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Quality: ${consistency.rating.title} (Max Dev: ±${String.format(Locale.US, "%.2f", consistency.maxDeviation)}%)",
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }

                // Stars Row
                Row {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= consistency.stars) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (i <= consistency.stars) Color(0xFFF59E0B) else colors.surfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Summary Statistics Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "OVERALL DRAWING STATISTICS",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    StatBox(
                        label = "TOTAL PASSES",
                        value = "${stats.totalPasses}",
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "STARTING DIE",
                        value = String.format(Locale.US, "%.3f mm", stats.startingDie),
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "FINAL DIE",
                        value = String.format(Locale.US, "%.3f mm", stats.finalDie),
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    StatBox(
                        label = "AVG ELONGATION",
                        value = String.format(Locale.US, "%.2f%%", stats.avgElongationPercent),
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        valueColor = colors.accentPrimary
                    )
                    StatBox(
                        label = "MAX ELONGATION",
                        value = String.format(Locale.US, "%.2f%%", stats.maxElongationPercent),
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "MIN ELONGATION",
                        value = String.format(Locale.US, "%.2f%%", stats.minElongationPercent),
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    StatBox(
                        label = "AVG REDUCTION",
                        value = String.format(Locale.US, "%.2f%%", stats.avgAreaReductionPercent),
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "OVERALL REDUCTION",
                        value = String.format(Locale.US, "%.2f%%", stats.overallAreaReductionPercent),
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "OVERALL RATIO",
                        value = String.format(Locale.US, "%.3f", stats.overallReductionRatio),
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    colors: CalculatorColors,
    modifier: Modifier = Modifier,
    valueColor: Color? = null
) {
    Box(
        modifier = modifier
            .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 10.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor ?: colors.textPrimary
            )
        }
    }
}
