package com.example.lihascalculator.ui.wiredrawing.components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.South
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.model.wiredrawing.PassResult
import com.example.lihascalculator.theme.CalculatorColors
import com.example.lihascalculator.ui.wiredrawing.WireDrawingState
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

        // Pass List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DRAWING PASSES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Tap pass for details",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }
        }

        // Pass Cards List (Mobile-friendly card per pass!)
        items(state.passes, key = { it.passNumber }) { pass ->
            PassCard(
                pass = pass,
                colors = colors,
                onClick = { onPassClick(pass) },
                onEditClick = { onEditPassClick(pass) }
            )
        }
    }
}

@Composable
private fun PassCard(
    pass: PassResult,
    colors: CalculatorColors,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PASS ${String.format(Locale.US, "%02d", pass.passNumber)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentPrimary,
                    letterSpacing = 0.5.sp
                )

                // Edit Action
                Box(
                    modifier = Modifier
                        .background(colors.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .clickable { onEditClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Die",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Edit",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Die Transition Flow (Large & Readable!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format(Locale.US, "%.3f mm", pass.fromDie),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.South,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colors.accentPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = String.format(Locale.US, "%.3f mm", pass.toDie),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(label = "Elongation", value = String.format(Locale.US, "%.2f%%", pass.elongationPercent), colors = colors, isPrimary = true)
                MetricColumn(label = "Reduction", value = String.format(Locale.US, "%.2f%%", pass.areaReductionPercent), colors = colors)
                MetricColumn(label = "Ratio", value = String.format(Locale.US, "%.3f", pass.reductionRatio), colors = colors)
            }
        }
    }
}

@Composable
private fun MetricColumn(
    label: String,
    value: String,
    colors: CalculatorColors,
    isPrimary: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            color = colors.textSecondary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPrimary) colors.accentPrimary else colors.textPrimary
        )
    }
}
