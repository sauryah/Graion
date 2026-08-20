package com.sauryah.lihas.calculator.ui.wiredrawing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.lihas.calculator.domain.model.wiredrawing.ConsistencyResult
import com.sauryah.lihas.calculator.domain.model.wiredrawing.PassResult
import com.sauryah.lihas.calculator.domain.model.wiredrawing.WireDrawingStats
import com.sauryah.lihas.calculator.theme.CalculatorColors
import com.sauryah.lihas.calculator.ui.wiredrawing.AnalysisChartType
import com.sauryah.lihas.calculator.ui.wiredrawing.WireDrawingState
import java.util.Locale

fun LazyListScope.analysisTabContent(
    state: WireDrawingState,
    colors: CalculatorColors,
    onSelectChartType: (AnalysisChartType) -> Unit,
    onSelectPass: (Int) -> Unit,
    onOpenConsistencyDetail: () -> Unit
) {
    item {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
            Text(
                text = "Analysis",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "Engineering charts, pass statistics, consistency, and CAD models.",
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }
    }

    if (!state.isValidSchedule) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please calculate a schedule on the Calculate tab first.",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            }
        }
        return
    }

    // 1. Chart Section with Segmented Control
    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Segmented Control Tabs (Elongation | Area Reduction)
                TabRow(
                    selectedTabIndex = state.analysisChartType.ordinal,
                    containerColor = colors.background.copy(alpha = 0.6f),
                    contentColor = colors.accentPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[state.analysisChartType.ordinal]),
                            color = colors.accentPrimary,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier.background(colors.background.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                ) {
                    AnalysisChartType.entries.forEach { type ->
                        val isSelected = state.analysisChartType == type
                        Tab(
                            selected = isSelected,
                            onClick = { onSelectChartType(type) },
                            text = {
                                Text(
                                    text = type.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.accentPrimary else colors.textSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chart Canvas
                when (state.analysisChartType) {
                    AnalysisChartType.ELONGATION -> {
                        SingleAnalysisChart(
                            unit = "%",
                            passes = state.passes,
                            selectedPassIndex = state.selectedPassIndex,
                            lineColor = colors.accentPrimary,
                            colors = colors,
                            valueSelector = { it.elongationPercent },
                            onSelectPass = onSelectPass
                        )
                    }
                    AnalysisChartType.AREA_REDUCTION -> {
                        SingleAnalysisChart(
                            unit = "%",
                            passes = state.passes,
                            selectedPassIndex = state.selectedPassIndex,
                            lineColor = Color(0xFF10B981),
                            colors = colors,
                            valueSelector = { it.areaReductionPercent },
                            onSelectPass = onSelectPass
                        )
                    }
                }
            }
        }
    }

    // 2. Compact Consistency Card
    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
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
                            text = "PASS CONSISTENCY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= state.consistency.stars) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (i <= state.consistency.stars) Color(0xFFF59E0B) else colors.surfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.consistency.rating.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Text(
                        text = "${String.format(Locale.US, "%.2f", state.consistency.maxDeviation)}% max variation from avg",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                TextButton(onClick = onOpenConsistencyDetail) {
                    Text(text = "View details", fontSize = 12.sp, color = colors.accentPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // 3. Key Statistics Card
    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "KEY STATISTICS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                StatRow("Total passes", "${state.stats.totalPasses}", colors)
                StatRow("Starting die", String.format(Locale.US, "%.3f mm", state.stats.startingDie), colors)
                StatRow("Final die", String.format(Locale.US, "%.3f mm", state.stats.finalDie), colors)
                StatRow("Avg elongation", String.format(Locale.US, "%.2f%%", state.stats.avgElongationPercent), colors, isPrimary = true)
                StatRow("Max elongation", String.format(Locale.US, "%.2f%%", state.stats.maxElongationPercent), colors)
                StatRow("Min elongation", String.format(Locale.US, "%.2f%%", state.stats.minElongationPercent), colors)
                StatRow("Avg area reduction", String.format(Locale.US, "%.2f%%", state.stats.avgAreaReductionPercent), colors)
                StatRow("Overall reduction", String.format(Locale.US, "%.2f%%", state.stats.overallAreaReductionPercent), colors)
                StatRow("Overall reduction ratio", String.format(Locale.US, "%.3f", state.stats.overallReductionRatio), colors)
            }
        }
    }

    // 4. CAD Die Visualization Section
    item {
        DieCadVisualizer(
            pass = state.selectedPass,
            colors = colors
        )
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    colors: CalculatorColors,
    isPrimary: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = colors.textSecondary)
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPrimary) colors.accentPrimary else colors.textPrimary
        )
    }
}

@Composable
private fun SingleAnalysisChart(
    unit: String,
    passes: List<PassResult>,
    selectedPassIndex: Int,
    lineColor: Color,
    colors: CalculatorColors,
    valueSelector: (PassResult) -> Double,
    onSelectPass: (Int) -> Unit
) {
    val selPass = passes.getOrNull(selectedPassIndex)

    Column {
        if (selPass != null) {
            val v = valueSelector(selPass)
            Text(
                text = "Selected Pass #${selPass.passNumber}: ${String.format(Locale.US, "%.2f", v)}$unit",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = lineColor
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            val values = passes.map { valueSelector(it) }
            val minVal = (values.minOrNull() ?: 0.0).coerceAtLeast(0.0)
            val maxVal = ((values.maxOrNull() ?: 30.0) * 1.15).coerceAtLeast(minVal + 5.0)

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .semantics {
                        contentDescription = "Analysis trend chart displaying values across ${passes.size} drawing passes"
                    }
            ) {
                val w = size.width
                val h = size.height

                // Grid lines
                for (i in 0..2) {
                    val y = h * (i / 2f)
                    drawLine(
                        color = colors.surfaceVariant.copy(alpha = 0.6f),
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(w, y),
                        strokeWidth = 1f
                    )
                }

                val n = passes.size
                val stepX = if (n > 1) w / (n - 1) else w / 2f

                val points = passes.mapIndexed { idx, p ->
                    val v = valueSelector(p)
                    val normY = ((v - minVal) / (maxVal - minVal)).toFloat().coerceIn(0f, 1f)
                    val x = if (n > 1) idx * stepX else w / 2f
                    val y = h - (normY * h)
                    androidx.compose.ui.geometry.Offset(x, y)
                }

                if (points.size > 1) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
                    }
                    drawPath(path = path, color = lineColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()))

                    val fillPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points[0].x, h)
                        lineTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
                        lineTo(points.last().x, h)
                        close()
                    }
                    drawPath(path = fillPath, color = lineColor.copy(alpha = 0.12f))
                }

                points.forEachIndexed { idx, pt ->
                    val isSelected = idx == selectedPassIndex
                    val r = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx()
                    if (isSelected) {
                        drawCircle(color = lineColor.copy(alpha = 0.35f), radius = r + 4.dp.toPx(), center = pt)
                        drawCircle(color = Color.White, radius = r, center = pt)
                        drawCircle(color = lineColor, radius = r - 2.dp.toPx(), center = pt)
                    } else {
                        drawCircle(color = lineColor, radius = r, center = pt)
                    }
                }
            }
        }
    }
}
