package com.sauryah.graion.ui.wiredrawing.components

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.South
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.sauryah.graion.domain.model.wiredrawing.PassResult
import com.sauryah.graion.theme.CalculatorColors
import com.sauryah.graion.ui.wiredrawing.WireDrawingState
import java.util.Locale

fun LazyListScope.calculateTabContent(
    state: WireDrawingState,
    colors: CalculatorColors,
    onOpenEditDies: () -> Unit,
    onCalculate: () -> Unit,
    onPassClick: (PassResult) -> Unit,
    onEditPassClick: (PassResult) -> Unit
) {
    // 1. Header & Subtitle
    item {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)) {
            Text(
                text = "Wire Drawing",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "Die Calculator",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.accentPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Design and analyze your wire drawing die schedule.",
                fontSize = 13.sp,
                color = colors.textSecondary
            )
        }
    }

    // 2. Primary Die Sequence Input Card
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DIE SEQUENCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(colors.accentPrimary.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${state.dies.size} DIES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sequence Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .clickable { onOpenEditDies() }
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (state.dies.isEmpty()) "No dies entered. Tap to add dies." else state.dieSequencePreview,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = if (state.dies.isEmpty()) colors.textSecondary else colors.textPrimary,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenEditDies,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Edit Dies", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = onCalculate,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.2f).height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "CALCULATE", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    }
                }
            }
        }
    }

    // 3. Results Section (Only when calculated >= 2 dies)
    if (state.isValidSchedule) {
        // Summary Card
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
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${state.stats.totalPasses} PASSES",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = String.format(Locale.US, "%.3f mm → %.3f mm", state.stats.startingDie, state.stats.finalDie),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(colors.accentPrimary.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Avg Elongation",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textSecondary
                            )
                            Text(
                                text = String.format(Locale.US, "%.2f%%", state.stats.avgElongationPercent),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPrimary
                            )
                        }
                    }
                }
            }
        }

        // Pass Schedule Table Card (High Density Columnar Grid)
        item {
            PassScheduleTableCard(
                passes = state.passes,
                stats = state.stats,
                colors = colors,
                onPassClick = onPassClick,
                onEditPassClick = onEditPassClick
            )
        }
    }
}

@Composable
private fun PassScheduleTableCard(
    passes: List<PassResult>,
    stats: com.sauryah.graion.domain.model.wiredrawing.WireDrawingStats,
    colors: CalculatorColors,
    onPassClick: (PassResult) -> Unit,
    onEditPassClick: (PassResult) -> Unit
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DIE SCHEDULE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "Tap row for CAD/Details",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontally Scrollable Table Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column {
                    // Column Header Row
                    Row(
                        modifier = Modifier
                            .background(
                                colors.surfaceVariant.copy(alpha = 0.7f),
                                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeaderCell(text = "PASS", width = 48.dp, colors = colors, align = TextAlign.Center)
                        HeaderCell(text = "DIES (mm)", width = 118.dp, colors = colors, align = TextAlign.Center)
                        HeaderCell(text = "ELONG %", width = 76.dp, colors = colors, align = TextAlign.End)
                        HeaderCell(text = "RED %", width = 68.dp, colors = colors, align = TextAlign.End)
                        HeaderCell(text = "RATIO", width = 58.dp, colors = colors, align = TextAlign.End)
                        HeaderCell(text = "EDIT", width = 42.dp, colors = colors, align = TextAlign.Center)
                    }

                    // Table Data Rows
                    passes.forEachIndexed { index, pass ->
                        val isZebra = index % 2 == 1
                        val rowBg = if (isZebra) colors.background.copy(alpha = 0.5f) else Color.Transparent

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg)
                                .clickable { onPassClick(pass) }
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pass #
                            Text(
                                text = String.format(Locale.US, "#%02d", pass.passNumber),
                                modifier = Modifier.width(48.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPrimary,
                                textAlign = TextAlign.Center
                            )

                            // From -> To
                            Row(
                                modifier = Modifier.width(118.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.3f", pass.fromDie),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = colors.accentPrimary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(10.dp).padding(horizontal = 1.dp)
                                )
                                Text(
                                    text = String.format(Locale.US, "%.3f", pass.toDie),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }

                            // Elongation %
                            val isElongHigh = pass.elongationPercent > 35.0
                            val isElongLow = pass.elongationPercent < 10.0
                            val elongColor = when {
                                isElongHigh -> Color(0xFFEF4444)
                                isElongLow -> Color(0xFFF59E0B)
                                else -> colors.accentPrimary
                            }
                            Text(
                                text = String.format(Locale.US, "%.2f%%", pass.elongationPercent),
                                modifier = Modifier.width(76.dp),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = elongColor,
                                textAlign = TextAlign.End
                            )

                            // Reduction %
                            Text(
                                text = String.format(Locale.US, "%.2f%%", pass.areaReductionPercent),
                                modifier = Modifier.width(68.dp),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = colors.textPrimary,
                                textAlign = TextAlign.End
                            )

                            // Ratio
                            Text(
                                text = String.format(Locale.US, "%.3f", pass.reductionRatio),
                                modifier = Modifier.width(58.dp),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = colors.textSecondary,
                                textAlign = TextAlign.End
                            )

                            // Quick Edit Action
                            Box(
                                modifier = Modifier
                                    .width(42.dp)
                                    .clickable { onEditPassClick(pass) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit pass #${pass.passNumber}",
                                    tint = colors.accentPrimary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Total / Average Summary Footer Row
                    Row(
                        modifier = Modifier
                            .background(
                                colors.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                            )
                            .padding(vertical = 9.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AVG / TOT",
                            modifier = Modifier.width(166.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = String.format(Locale.US, "%.2f%%", stats.avgElongationPercent),
                            modifier = Modifier.width(76.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentPrimary,
                            textAlign = TextAlign.End
                        )

                        Text(
                            text = String.format(Locale.US, "%.2f%%", stats.overallAreaReductionPercent),
                            modifier = Modifier.width(68.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            textAlign = TextAlign.End
                        )

                        Text(
                            text = String.format(Locale.US, "%.3f", stats.overallReductionRatio),
                            modifier = Modifier.width(58.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.End
                        )

                        Spacer(modifier = Modifier.width(42.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    colors: CalculatorColors,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 2.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = colors.textSecondary,
        textAlign = align,
        letterSpacing = 0.5.sp
    )
}

