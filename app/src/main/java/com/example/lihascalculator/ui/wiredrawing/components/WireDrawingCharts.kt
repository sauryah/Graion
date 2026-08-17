package com.example.lihascalculator.ui.wiredrawing.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.model.wiredrawing.PassResult
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun WireDrawingCharts(
    passes: List<PassResult>,
    selectedPassIndex: Int,
    colors: CalculatorColors,
    onSelectPass: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Elongation Chart Card
        SingleLineChartCard(
            title = "ELONGATION % PER PASS",
            unitLabel = "%",
            passes = passes,
            selectedPassIndex = selectedPassIndex,
            lineColor = colors.accentPrimary,
            colors = colors,
            valueSelector = { it.elongationPercent },
            onSelectPass = onSelectPass
        )

        // Area Reduction Chart Card
        SingleLineChartCard(
            title = "AREA REDUCTION % PER PASS",
            unitLabel = "%",
            passes = passes,
            selectedPassIndex = selectedPassIndex,
            lineColor = Color(0xFF10B981),
            colors = colors,
            valueSelector = { it.areaReductionPercent },
            onSelectPass = onSelectPass
        )
    }
}

@Composable
private fun SingleLineChartCard(
    title: String,
    unitLabel: String,
    passes: List<PassResult>,
    selectedPassIndex: Int,
    lineColor: Color,
    colors: CalculatorColors,
    valueSelector: (PassResult) -> Double,
    onSelectPass: (Int) -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = lineColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                val selPass = passes.getOrNull(selectedPassIndex)
                if (selPass != null) {
                    val selVal = valueSelector(selPass)
                    Text(
                        text = "Pass #${selPass.passNumber}: ${String.format(Locale.US, "%.2f", selVal)}$unitLabel",
                        color = lineColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (passes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(colors.background, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No pass data", color = colors.textSecondary, fontSize = 12.sp)
                }
            } else {
                val values = passes.map { valueSelector(it) }
                val minVal = (values.minOrNull() ?: 0.0).coerceAtLeast(0.0)
                val maxVal = ((values.maxOrNull() ?: 30.0) * 1.15).coerceAtLeast(minVal + 5.0)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .pointerInput(passes) {
                                detectTapGestures { offset ->
                                    val count = passes.size
                                    if (count > 0) {
                                        val segWidth = size.width / count
                                        val tappedIdx = (offset.x / segWidth).toInt().coerceIn(0, count - 1)
                                        onSelectPass(tappedIdx)
                                    }
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height

                        // Horizontal grid lines (3 levels)
                        for (level in 0..2) {
                            val y = h * (level / 2f)
                            drawLine(
                                color = colors.surfaceVariant.copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
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
                            Offset(x, y)
                        }

                        // Draw connecting path
                        if (points.size > 1) {
                            val path = Path().apply {
                                moveTo(points[0].x, points[0].y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }
                            drawPath(path = path, color = lineColor, style = Stroke(width = 3.dp.toPx()))

                            // Draw shaded area under path
                            val fillPath = Path().apply {
                                moveTo(points[0].x, h)
                                lineTo(points[0].x, points[0].y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                                lineTo(points.last().x, h)
                                close()
                            }
                            drawPath(path = fillPath, color = lineColor.copy(alpha = 0.12f))
                        }

                        // Draw points
                        points.forEachIndexed { idx, pt ->
                            val isSelected = idx == selectedPassIndex
                            val ptRadius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx()

                            if (isSelected) {
                                drawCircle(color = lineColor.copy(alpha = 0.35f), radius = ptRadius + 4.dp.toPx(), center = pt)
                                drawCircle(color = Color.White, radius = ptRadius, center = pt)
                                drawCircle(color = lineColor, radius = ptRadius - 2.dp.toPx(), center = pt)
                            } else {
                                drawCircle(color = lineColor, radius = ptRadius, center = pt)
                            }
                        }
                    }
                }
            }
        }
    }
}
