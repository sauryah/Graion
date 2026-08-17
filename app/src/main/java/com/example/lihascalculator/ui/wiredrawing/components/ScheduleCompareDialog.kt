package com.example.lihascalculator.ui.wiredrawing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.model.wiredrawing.PassResult
import com.example.lihascalculator.domain.model.wiredrawing.SavedSchedule
import com.example.lihascalculator.domain.model.wiredrawing.WireDrawingStats
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun ScheduleCompareDialog(
    isOpen: Boolean,
    currentStats: WireDrawingStats,
    currentPasses: List<PassResult>,
    savedSchedules: List<SavedSchedule>,
    comparedSchedule: SavedSchedule?,
    comparedStats: WireDrawingStats?,
    comparedPasses: List<PassResult>,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onSelectScheduleToCompare: (SavedSchedule?) -> Unit
) {
    if (!isOpen) return

    var selectedForCompare by remember(comparedSchedule) { mutableStateOf(comparedSchedule) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = null,
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Compare Schedules",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Select a saved schedule to compare against your active drawing schedule:",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Schedule Picker
                if (savedSchedules.isEmpty()) {
                    Text(
                        text = "No saved schedules available to compare. Save a schedule first.",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        savedSchedules.forEach { s ->
                            val isSel = selectedForCompare?.id == s.id
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSel) colors.accentPrimary.copy(alpha = 0.2f) else colors.background.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSel) colors.accentPrimary else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedForCompare = s
                                        onSelectScheduleToCompare(s)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = s.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) colors.accentPrimary else colors.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (comparedStats != null) {
                    // Comparison Metrics Table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.background.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CompareMetricRow("METRIC", "CURRENT", "COMPARED", colors, isHeader = true)
                        CompareMetricRow("Total Passes", "${currentStats.totalPasses}", "${comparedStats.totalPasses}", colors)
                        CompareMetricRow("Start Die", String.format(Locale.US, "%.3f", currentStats.startingDie), String.format(Locale.US, "%.3f", comparedStats.startingDie), colors)
                        CompareMetricRow("Final Die", String.format(Locale.US, "%.3f", currentStats.finalDie), String.format(Locale.US, "%.3f", comparedStats.finalDie), colors)
                        CompareMetricRow("Avg Elongation", String.format(Locale.US, "%.2f%%", currentStats.avgElongationPercent), String.format(Locale.US, "%.2f%%", comparedStats.avgElongationPercent), colors)
                        CompareMetricRow("Max Elongation", String.format(Locale.US, "%.2f%%", currentStats.maxElongationPercent), String.format(Locale.US, "%.2f%%", comparedStats.maxElongationPercent), colors)
                        CompareMetricRow("Min Elongation", String.format(Locale.US, "%.2f%%", currentStats.minElongationPercent), String.format(Locale.US, "%.2f%%", comparedStats.minElongationPercent), colors)
                        CompareMetricRow("Avg Reduction", String.format(Locale.US, "%.2f%%", currentStats.avgAreaReductionPercent), String.format(Locale.US, "%.2f%%", comparedStats.avgAreaReductionPercent), colors)
                        CompareMetricRow("Overall Reduction", String.format(Locale.US, "%.2f%%", currentStats.overallAreaReductionPercent), String.format(Locale.US, "%.2f%%", comparedStats.overallAreaReductionPercent), colors)
                        CompareMetricRow("Overall Ratio", String.format(Locale.US, "%.3f", currentStats.overallReductionRatio), String.format(Locale.US, "%.3f", comparedStats.overallReductionRatio), colors)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = colors.accentPrimary)
            }
        },
        dismissButton = {
            if (comparedSchedule != null) {
                TextButton(onClick = { onSelectScheduleToCompare(null) }) {
                    Text("Clear Compare", color = Color(0xFFEF4444))
                }
            }
        }
    )
}

@Composable
private fun CompareMetricRow(
    label: String,
    currentVal: String,
    compareVal: String,
    colors: CalculatorColors,
    isHeader: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Medium,
            color = if (isHeader) colors.textSecondary else colors.textPrimary,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = currentVal,
            fontSize = 11.sp,
            fontFamily = if (isHeader) FontFamily.Default else FontFamily.Monospace,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = if (isHeader) colors.textSecondary else colors.accentPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = compareVal,
            fontSize = 11.sp,
            fontFamily = if (isHeader) FontFamily.Default else FontFamily.Monospace,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = if (isHeader) colors.textSecondary else colors.textPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
