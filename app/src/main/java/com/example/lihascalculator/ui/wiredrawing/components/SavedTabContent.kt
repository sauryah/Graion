package com.sauryah.lihas.calculator.ui.wiredrawing.components

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.lihas.calculator.domain.engine.wiredrawing.WireDrawingExportHelper
import com.sauryah.lihas.calculator.domain.model.wiredrawing.SavedSchedule
import com.sauryah.lihas.calculator.theme.CalculatorColors
import com.sauryah.lihas.calculator.ui.util.ShareHelper
import com.sauryah.lihas.calculator.ui.wiredrawing.WireDrawingState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SavedScheduleDateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

fun LazyListScope.savedTabContent(
    state: WireDrawingState,
    colors: CalculatorColors,
    context: Context,
    onOpenSaveDialog: () -> Unit,
    onOpenCompareDialog: () -> Unit,
    onLoadSchedule: (SavedSchedule) -> Unit,
    onDeleteSchedule: (Long) -> Unit
) {
    item {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
            Text(
                text = "Saved & Export",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "Manage saved schedules, compare configurations, and export reports.",
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }
    }

    // 1. Quick Actions Row (Save current schedule & Compare)
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenSaveDialog,
                enabled = state.isValidSchedule,
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Current", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            OutlinedButton(
                onClick = onOpenCompareDialog,
                enabled = state.isValidSchedule && state.savedSchedules.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Icon(imageVector = Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.textPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Compare", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    // 2. Saved Schedules List Section
    item {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SAVED SCHEDULES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
                letterSpacing = 1.sp
            )
            Text(
                text = "${state.savedSchedules.size} saved",
                fontSize = 11.sp,
                color = colors.textSecondary
            )
        }
    }

    if (state.savedSchedules.isEmpty()) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved schedules yet. Tap 'Save Current' to store your active schedule.",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }
    } else {
        items(state.savedSchedules, key = { it.id }) { schedule ->
            SavedScheduleItemCard(
                schedule = schedule,
                colors = colors,
                onOpen = { onLoadSchedule(schedule) },
                onDelete = { onDeleteSchedule(schedule.id) }
            )
        }
    }

    // 3. Export Section Card
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EXPORT SCHEDULE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Export the active pass calculation table and summary statistics to your preferred format:",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (state.isValidSchedule) {
                                val csv = WireDrawingExportHelper.generateCsv(state.passes, state.stats)
                                ShareHelper.shareContent(
                                    context = context,
                                    title = "Export Schedule (CSV)",
                                    content = csv,
                                    mimeType = "text/csv"
                                )
                            }
                        },
                        enabled = state.isValidSchedule,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.textPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "CSV", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            if (state.isValidSchedule) {
                                val report = WireDrawingExportHelper.generateTextReport(state.passes, state.stats)
                                ShareHelper.shareContent(
                                    context = context,
                                    title = "Export Engineering Report",
                                    content = report,
                                    mimeType = "text/plain"
                                )
                            }
                        },
                        enabled = state.isValidSchedule,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.textPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Report", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedScheduleItemCard(
    schedule: SavedSchedule,
    colors: CalculatorColors,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val formattedDate = remember(schedule.timestamp) {
        SavedScheduleDateFormatter.format(Date(schedule.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${schedule.dies.size} dies (${String.format(Locale.US, "%.3f → %.3f mm", schedule.dies.firstOrNull() ?: 0.0, schedule.dies.lastOrNull() ?: 0.0)})",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = colors.accentPrimary
                )
                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = colors.textSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "OPEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.accentPrimary)
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
