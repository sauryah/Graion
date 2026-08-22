package com.sauryah.graion.ui.wiredrawing.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.graion.domain.model.wiredrawing.PassResult
import com.sauryah.graion.theme.CalculatorColors
import com.sauryah.graion.ui.wiredrawing.AnalysisChartType
import com.sauryah.graion.ui.wiredrawing.WireDrawingState
import java.util.Locale

fun LazyListScope.analysisTabContent(
    state: WireDrawingState,
    colors: CalculatorColors,
    onSelectChartType: (AnalysisChartType) -> Unit,
    onSelectPass: (Int) -> Unit,
    onOpenConsistencyDetail: () -> Unit
) {
    // 1. Header
    item {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
            Text(
                text = "Analytics & Insights",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Telemetry trends, process consistency, and CAD die geometry.",
                fontSize = 13.sp,
                color = colors.textSecondary
            )
        }
    }

    if (!state.isValidSchedule) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = colors.textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Schedule Calculated",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Calculate a die sequence on the Calculate tab to view telemetry.",
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        return
    }

    // 2. Executive 2x2 KPI Metric Cards Grid
    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row (Avg Elongation | Total Reduction)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    title = "AVG ELONGATION",
                    value = String.format(Locale.US, "%.2f%%", state.stats.avgElongationPercent),
                    subtitle = "Range: ${String.format(Locale.US, "%.2f%% – %.2f%%", state.stats.minElongationPercent, state.stats.maxElongationPercent)}",
                    accentColor = colors.accentPrimary,
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )

                KpiMetricCard(
                    title = "TOTAL REDUCTION",
                    value = String.format(Locale.US, "%.2f%%", state.stats.overallAreaReductionPercent),
                    subtitle = "Ratio: ${String.format(Locale.US, "%.2f×", state.stats.overallReductionRatio)} (Avg: ${String.format(Locale.US, "%.2f%%", state.stats.avgAreaReductionPercent)})",
                    accentColor = Color(0xFF10B981),
                    icon = Icons.Default.PieChart,
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom Row (Die Span | Quality Rating)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    title = "DIE SPAN",
                    value = "${state.stats.totalPasses} Passes",
                    subtitle = String.format(Locale.US, "%.3f → %.3f mm", state.stats.startingDie, state.stats.finalDie),
                    accentColor = Color(0xFF38BDF8),
                    icon = Icons.Default.Straighten,
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )

                KpiMetricCard(
                    title = "QUALITY RATING",
                    value = "${state.consistency.stars}.0 ★ ${state.consistency.rating.name}",
                    subtitle = "Dev: ±${String.format(Locale.US, "%.2f%%", state.consistency.maxDeviation)} • Details",
                    accentColor = Color(0xFFF59E0B),
                    icon = Icons.Default.Verified,
                    colors = colors,
                    onClick = onOpenConsistencyDetail,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // 3. Interactive Multi-Metric Trend Chart Card
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
                // Header with Chart Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PROGRESSION TELEMETRY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Tap node to inspect",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chart Type Selector Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnalysisChartType.entries.forEach { type ->
                        val isSelected = state.analysisChartType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) colors.accentPrimary.copy(alpha = 0.15f) else colors.background.copy(alpha = 0.6f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) colors.accentPrimary else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectChartType(type) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) colors.accentPrimary else colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Node Inspection Tooltip Banner
                val selectedPass = state.selectedPass
                if (selectedPass != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.background.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(colors.accentPrimary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pass #${selectedPass.passNumber}: ${String.format(Locale.US, "%.3f → %.3f mm", selectedPass.fromDie, selectedPass.toDie)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        val activeValStr = when (state.analysisChartType) {
                            AnalysisChartType.ELONGATION -> "${String.format(Locale.US, "%.2f%%", selectedPass.elongationPercent)} elong"
                            AnalysisChartType.AREA_REDUCTION -> "${String.format(Locale.US, "%.2f%%", selectedPass.areaReductionPercent)} red"
                            AnalysisChartType.DIE_DIAMETER -> "${String.format(Locale.US, "%.3f mm", selectedPass.toDie)} die"
                        }

                        Text(
                            text = activeValStr,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Interactive Canvas Chart
                InteractiveTelemetryChart(
                    state = state,
                    chartType = state.analysisChartType,
                    selectedPassIndex = state.selectedPassIndex,
                    colors = colors,
                    onSelectPass = onSelectPass
                )
            }
        }
    }

    // 4. CAD Die Cross-Section Blueprint Card with Stepper Controls
    item {
        CadBlueprintSection(
            state = state,
            colors = colors,
            onSelectPass = onSelectPass
        )
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    colors: CalculatorColors,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(accentColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = colors.textSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun InteractiveTelemetryChart(
    state: WireDrawingState,
    chartType: AnalysisChartType,
    selectedPassIndex: Int,
    colors: CalculatorColors,
    onSelectPass: (Int) -> Unit
) {
    val passes = state.passes
    if (passes.isEmpty()) return

    val values = when (chartType) {
        AnalysisChartType.ELONGATION -> passes.map { it.elongationPercent }
        AnalysisChartType.AREA_REDUCTION -> passes.map { it.areaReductionPercent }
        AnalysisChartType.DIE_DIAMETER -> passes.map { it.toDie }
    }

    val primaryLineColor = when (chartType) {
        AnalysisChartType.ELONGATION -> colors.accentPrimary
        AnalysisChartType.AREA_REDUCTION -> Color(0xFF10B981)
        AnalysisChartType.DIE_DIAMETER -> Color(0xFF38BDF8)
    }

    val unitSuffix = when (chartType) {
        AnalysisChartType.ELONGATION, AnalysisChartType.AREA_REDUCTION -> "%"
        AnalysisChartType.DIE_DIAMETER -> "mm"
    }

    val minVal = (values.minOrNull() ?: 0.0).coerceAtLeast(0.0)
    val maxVal = ((values.maxOrNull() ?: 30.0) * 1.15).coerceAtLeast(minVal + 2.0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .semantics {
                    contentDescription = "Telemetry chart for ${chartType.title} across ${passes.size} passes"
                }
        ) {
            val w = size.width
            val h = size.height
            val chartLeft = 36.dp.toPx()
            val chartBottom = h - 22.dp.toPx()
            val chartWidth = w - chartLeft
            val chartHeight = chartBottom

            // 1. Draw Safe / Recommended Drawing Zone for Elongation / Reduction (18% - 28%)
            if (chartType == AnalysisChartType.ELONGATION || chartType == AnalysisChartType.AREA_REDUCTION) {
                val safeLow = 18.0
                val safeHigh = 28.0
                val normLowY = ((safeLow - minVal) / (maxVal - minVal)).toFloat().coerceIn(0f, 1f)
                val normHighY = ((safeHigh - minVal) / (maxVal - minVal)).toFloat().coerceIn(0f, 1f)

                val yTop = chartHeight - (normHighY * chartHeight)
                val yBottom = chartHeight - (normLowY * chartHeight)

                drawRect(
                    color = Color(0xFF10B981).copy(alpha = 0.08f),
                    topLeft = Offset(chartLeft, yTop),
                    size = Size(chartWidth, yBottom - yTop)
                )
            }

            // 2. Horizontal Gridlines & Y-Axis Reference Ticks
            val gridSteps = 3
            for (i in 0..gridSteps) {
                val fraction = i / gridSteps.toFloat()
                val y = chartHeight - (fraction * chartHeight)
                val tickValue = minVal + fraction * (maxVal - minVal)

                drawLine(
                    color = colors.surfaceVariant.copy(alpha = 0.6f),
                    start = Offset(chartLeft, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
            }

            // 3. Calculate data point coordinates
            val n = passes.size
            val stepX = if (n > 1) chartWidth / (n - 1) else chartWidth / 2f

            val points = values.mapIndexed { idx, v ->
                val normY = ((v - minVal) / (maxVal - minVal)).toFloat().coerceIn(0f, 1f)
                val x = chartLeft + (if (n > 1) idx * stepX else chartWidth / 2f)
                val y = chartHeight - (normY * chartHeight)
                Offset(x, y)
            }

            // 4. Fill gradient below curve
            if (points.size > 1) {
                val fillPath = Path().apply {
                    moveTo(points[0].x, chartHeight)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, chartHeight)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryLineColor.copy(alpha = 0.28f), primaryLineColor.copy(alpha = 0.02f)),
                        startY = 0f,
                        endY = chartHeight
                    )
                )

                // 5. Draw Trend Line
                val linePath = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(
                    path = linePath,
                    color = primaryLineColor,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 6. Draw Nodes & Highlight Selected Node
            points.forEachIndexed { idx, pt ->
                val isSelected = idx == selectedPassIndex
                if (isSelected) {
                    drawCircle(color = primaryLineColor.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = pt)
                    drawCircle(color = Color.White, radius = 5.5.dp.toPx(), center = pt)
                    drawCircle(color = primaryLineColor, radius = 3.5.dp.toPx(), center = pt)
                } else {
                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = pt)
                    drawCircle(color = primaryLineColor, radius = 2.dp.toPx(), center = pt)
                }
            }
        }

        // X-Axis Pass Labels overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp)
                .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val displayPasses = if (passes.size <= 8) passes else listOf(passes.first(), passes[passes.size / 2], passes.last())
            displayPasses.forEach { pass ->
                Text(
                    text = "P${pass.passNumber}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun CadBlueprintSection(
    state: WireDrawingState,
    colors: CalculatorColors,
    onSelectPass: (Int) -> Unit
) {
    val passes = state.passes
    val currentIndex = state.selectedPassIndex.coerceIn(0, (passes.size - 1).coerceAtLeast(0))
    val currentPass = passes.getOrNull(currentIndex)

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
            // Header with Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DIE CAD BLUEPRINT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        letterSpacing = 1.sp
                    )
                }

                // Stepper: < Prev / Next >
                if (passes.size > 1) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onSelectPass((currentIndex - 1).coerceAtLeast(0)) },
                            enabled = currentIndex > 0,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Pass",
                                tint = if (currentIndex > 0) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f)
                            )
                        }

                        Text(
                            text = "${currentIndex + 1} / ${passes.size}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = { onSelectPass((currentIndex + 1).coerceAtMost(passes.size - 1)) },
                            enabled = currentIndex < passes.size - 1,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Pass",
                                tint = if (currentIndex < passes.size - 1) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CAD Visualizer Drawing
            DieCadVisualizer(
                pass = currentPass,
                colors = colors
            )
        }
    }
}
