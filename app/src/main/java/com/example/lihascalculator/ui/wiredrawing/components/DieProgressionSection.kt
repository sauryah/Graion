package com.example.lihascalculator.ui.wiredrawing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DieProgressionSection(
    dies: List<Double>,
    colors: CalculatorColors,
    onEditDie: (index: Int, newDiameter: Double) -> Unit,
    onAddDie: (diameter: Double) -> Unit,
    onRemoveDie: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var addDieText by remember { mutableStateOf("") }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editDieText by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatListNumbered,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DIE PROGRESSION CHAIN",
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(colors.accentPrimary, RoundedCornerShape(6.dp))
                        .clickable { isAddDialogOpen = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Die",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Add Die",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                dies.forEachIndexed { index, die ->
                    Box(
                        modifier = Modifier
                            .background(colors.background.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .clickable {
                                editingIndex = index
                                editDieText = String.format(Locale.US, "%.3f", die)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}: ",
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = String.format(Locale.US, "%.3f", die),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove die",
                                tint = colors.textSecondary.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { onRemoveDie(index) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Die Dialog
    if (isAddDialogOpen) {
        AlertDialog(
            onDismissRequest = { isAddDialogOpen = false },
            title = { Text("Add Die Diameter (mm)") },
            text = {
                OutlinedTextField(
                    value = addDieText,
                    onValueChange = { addDieText = it },
                    label = { Text("Diameter (mm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val d = addDieText.toDoubleOrNull()
                        if (d != null && d > 0.0) {
                            onAddDie(d)
                            addDieText = ""
                            isAddDialogOpen = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddDialogOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Die Dialog
    if (editingIndex != null) {
        val idx = editingIndex!!
        val currentDie = dies.getOrNull(idx) ?: 0.0
        AlertDialog(
            onDismissRequest = { editingIndex = null },
            title = { Text("Edit Die #${idx + 1}") },
            text = {
                OutlinedTextField(
                    value = editDieText,
                    onValueChange = { editDieText = it },
                    label = { Text("Diameter (mm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val d = editDieText.toDoubleOrNull()
                        if (d != null && d > 0.0) {
                            onEditDie(idx, d)
                            editingIndex = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingIndex = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
