package com.example.lihascalculator.ui.wiredrawing.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.model.wiredrawing.SavedSchedule
import com.example.lihascalculator.theme.CalculatorColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SaveScheduleDialog(
    isOpen: Boolean,
    currentDies: List<Double>,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    if (!isOpen) return

    var nameText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Die Schedule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Save this schedule (${currentDies.size} dies) for future loading or comparison.",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Schedule Name (e.g. Copper 2.5mm to 0.3mm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameText.isNotBlank()) {
                        onSave(nameText)
                    }
                },
                enabled = nameText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary)
            ) {
                Text("Save Schedule", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}

@Composable
fun LoadScheduleDialog(
    isOpen: Boolean,
    schedules: List<SavedSchedule>,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onLoad: (SavedSchedule) -> Unit,
    onDelete: (Long) -> Unit
) {
    if (!isOpen) return

    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Load Saved Schedule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            if (schedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved schedules yet. Use 'Save' to persist a schedule.",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(schedules, key = { it.id }) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onLoad(item) }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${item.dies.size} Dies (${String.format(Locale.US, "%.3f → %.3f mm", item.dies.firstOrNull() ?: 0.0, item.dies.lastOrNull() ?: 0.0)})",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = colors.accentPrimary
                                    )
                                    Text(
                                        text = dateFormatter.format(Date(item.timestamp)),
                                        fontSize = 10.sp,
                                        color = colors.textSecondary
                                    )
                                }

                                IconButton(
                                    onClick = { onDelete(item.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete schedule",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = colors.textSecondary)
            }
        }
    )
}
