package com.sauryah.lihas.calculator.ui.wiredrawing.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.lihas.calculator.domain.model.wiredrawing.PassResult
import com.sauryah.lihas.calculator.theme.CalculatorColors
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DieCadVisualizer(
    pass: PassResult?,
    colors: CalculatorColors,
    modifier: Modifier = Modifier
) {
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
            // Header
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DIE CAD BLUEPRINT CROSS-SECTION",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                if (pass != null) {
                    Box(
                        modifier = Modifier
                            .background(colors.accentPrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PASS #${pass.passNumber}",
                            color = colors.accentPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (pass == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(colors.background, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Select a pass to inspect CAD cross-section",
                        color = colors.textSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                val dIn = pass.fromDie
                val dOut = pass.toDie

                // 2D CAD Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color(0xFF090B10), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    val blueprintCyan = Color(0xFF38BDF8)
                    val carbideColor = Color(0xFF334155)
                    val wireColor = Color(0xFFF59E0B)
                    val casingBorderColor = Color(0xFF64748B)

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .semantics {
                                contentDescription = "Die blueprint cross-section for pass #${pass.passNumber}, inlet ${pass.fromDie} mm, outlet ${pass.toDie} mm"
                            }
                    ) {
                        val w = size.width
                        val h = size.height
                        val midY = h / 2f

                        // Grid lines for engineering blueprint
                        val gridSpacing = 20.dp.toPx()
                        for (x in 0..(w / gridSpacing).toInt()) {
                            drawLine(
                                color = Color(0xFF1E293B).copy(alpha = 0.5f),
                                start = Offset(x * gridSpacing, 0f),
                                end = Offset(x * gridSpacing, h),
                                strokeWidth = 1f
                            )
                        }
                        for (y in 0..(h / gridSpacing).toInt()) {
                            drawLine(
                                color = Color(0xFF1E293B).copy(alpha = 0.5f),
                                start = Offset(0f, y * gridSpacing),
                                end = Offset(w, y * gridSpacing),
                                strokeWidth = 1f
                            )
                        }

                        // Centerline (dashed)
                        drawLine(
                            color = blueprintCyan.copy(alpha = 0.6f),
                            start = Offset(0f, midY),
                            end = Offset(w, midY),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f, 3f, 6f), 0f)
                        )

                        // Coordinates for die geometry
                        val xInlet = w * 0.15f
                        val xApproach = w * 0.45f
                        val xBearingEnd = w * 0.65f
                        val xExit = w * 0.85f

                        val halfInlet = (h * 0.35f).coerceAtLeast(30f)
                        val ratio = (dOut / dIn).toFloat().coerceIn(0.2f, 0.95f)
                        val halfOutlet = halfInlet * ratio

                        // Draw Upper Die Profile
                        val upperDiePath = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(w, 0f)
                            lineTo(w, midY - halfOutlet - 20f)
                            lineTo(xExit, midY - halfOutlet - 10f)
                            lineTo(xBearingEnd, midY - halfOutlet)
                            lineTo(xApproach, midY - halfOutlet)
                            lineTo(xInlet, midY - halfInlet)
                            lineTo(0f, midY - halfInlet)
                            close()
                        }

                        // Draw Lower Die Profile
                        val lowerDiePath = Path().apply {
                            moveTo(0f, h)
                            lineTo(w, h)
                            lineTo(w, midY + halfOutlet + 20f)
                            lineTo(xExit, midY + halfOutlet + 10f)
                            lineTo(xBearingEnd, midY + halfOutlet)
                            lineTo(xApproach, midY + halfOutlet)
                            lineTo(xInlet, midY + halfInlet)
                            lineTo(0f, midY + halfInlet)
                            close()
                        }

                        drawPath(upperDiePath, color = carbideColor.copy(alpha = 0.8f))
                        drawPath(lowerDiePath, color = carbideColor.copy(alpha = 0.8f))
                        drawPath(upperDiePath, color = casingBorderColor, style = Stroke(width = 2f))
                        drawPath(lowerDiePath, color = casingBorderColor, style = Stroke(width = 2f))

                        // Draw Drawn Wire flowing through die
                        val wirePath = Path().apply {
                            moveTo(0f, midY - halfInlet + 2f)
                            lineTo(xInlet, midY - halfInlet + 2f)
                            lineTo(xApproach, midY - halfOutlet + 2f)
                            lineTo(w, midY - halfOutlet + 2f)
                            lineTo(w, midY + halfOutlet - 2f)
                            lineTo(xApproach, midY + halfOutlet - 2f)
                            lineTo(xInlet, midY + halfInlet - 2f)
                            lineTo(0f, midY + halfInlet - 2f)
                            close()
                        }
                        drawPath(wirePath, color = wireColor.copy(alpha = 0.35f))
                        drawPath(wirePath, color = wireColor, style = Stroke(width = 2f))

                        // Dimension lines: Inlet Diameter
                        val dimInletX = xInlet - 15f
                        drawLine(
                            color = blueprintCyan,
                            start = Offset(dimInletX, midY - halfInlet),
                            end = Offset(dimInletX, midY + halfInlet),
                            strokeWidth = 1.5f
                        )
                        drawLine(color = blueprintCyan, start = Offset(dimInletX - 6f, midY - halfInlet), end = Offset(dimInletX + 6f, midY - halfInlet), strokeWidth = 1.5f)
                        drawLine(color = blueprintCyan, start = Offset(dimInletX - 6f, midY + halfInlet), end = Offset(dimInletX + 6f, midY + halfInlet), strokeWidth = 1.5f)

                        // Dimension lines: Outlet Diameter
                        val dimOutletX = xBearingEnd + 15f
                        drawLine(
                            color = blueprintCyan,
                            start = Offset(dimOutletX, midY - halfOutlet),
                            end = Offset(dimOutletX, midY + halfOutlet),
                            strokeWidth = 1.5f
                        )
                        drawLine(color = blueprintCyan, start = Offset(dimOutletX - 6f, midY - halfOutlet), end = Offset(dimOutletX + 6f, midY - halfOutlet), strokeWidth = 1.5f)
                        drawLine(color = blueprintCyan, start = Offset(dimOutletX - 6f, midY + halfOutlet), end = Offset(dimOutletX + 6f, midY + halfOutlet), strokeWidth = 1.5f)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // CAD Spec details footer
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    SpecChip("DIE ID", "PASS-${pass.passNumber}", colors, Modifier.weight(1f))
                    SpecChip("INLET (d_in)", String.format(Locale.US, "%.3f mm", pass.fromDie), colors, Modifier.weight(1f))
                    SpecChip("OUTLET (d_out)", String.format(Locale.US, "%.3f mm", pass.toDie), colors, Modifier.weight(1f), valueColor = colors.accentPrimary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    SpecChip("CASING", "Standard Carbide", colors, Modifier.weight(1f))
                    SpecChip("APPROACH ANGLE", "2α = 14°", colors, Modifier.weight(1f))
                    SpecChip("STATUS", "RUNNING", colors, Modifier.weight(1f), valueColor = Color(0xFF10B981))
                }
            }
        }
    }
}

@Composable
private fun SpecChip(
    label: String,
    value: String,
    colors: CalculatorColors,
    modifier: Modifier = Modifier,
    valueColor: Color? = null
) {
    Box(
        modifier = modifier
            .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor ?: colors.textPrimary
            )
        }
    }
}
