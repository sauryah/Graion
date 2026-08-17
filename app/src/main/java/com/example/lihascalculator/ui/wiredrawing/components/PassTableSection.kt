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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.domain.model.wiredrawing.PassResult
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun PassTableSection(
    passes: List<PassResult>,
    selectedPassIndex: Int,
    colors: CalculatorColors,
    onSelectPass: (Int) -> Unit,
    onEditToDie: (passIndex: Int, newToDie: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingPassIndex by remember { mutableStateOf<Int?>(null) }
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
                Text(
                    text = "PASS SCHEDULE TABLE",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${passes.size} Passes • Tap row to select, 'To' to edit",
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val scrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .background(colors.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell("PASS", width = 60.dp, isHeader = true, colors = colors)
                        TableCell("FROM (mm)", width = 90.dp, isHeader = true, colors = colors)
                        TableCell("TO (mm) ✏️", width = 95.dp, isHeader = true, colors = colors)
                        TableCell("AREA IN", width = 95.dp, isHeader = true, colors = colors)
                        TableCell("AREA OUT", width = 95.dp, isHeader = true, colors = colors)
                        TableCell("REDUCTION", width = 95.dp, isHeader = true, colors = colors)
                        TableCell("ELONGATION", width = 100.dp, isHeader = true, colors = colors)
                        TableCell("RATIO", width = 80.dp, isHeader = true, colors = colors)
                    }

                    // Data Rows
                    passes.forEachIndexed { index, pass ->
                        val isSelected = index == selectedPassIndex
                        val rowBg = if (isSelected) {
                            colors.accentPrimary.copy(alpha = 0.18f)
                        } else if (index % 2 == 0) {
                            colors.background.copy(alpha = 0.4f)
                        } else {
                            Color.Transparent
                        }

                        val rowBorder = if (isSelected) {
                            Modifier.border(1.dp, colors.accentPrimary, RoundedCornerShape(4.dp))
                        } else {
                            Modifier
                        }

                        Row(
                            modifier = Modifier
                                .then(rowBorder)
                                .background(rowBg, RoundedCornerShape(4.dp))
                                .clickable { onSelectPass(index) }
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pass number
                            TableCell(
                                text = "#${pass.passNumber}",
                                width = 60.dp,
                                isBold = isSelected,
                                colors = colors,
                                textColor = if (isSelected) colors.accentPrimary else colors.textPrimary
                            )

                            // From die
                            TableCell(
                                text = String.format(Locale.US, "%.3f", pass.fromDie),
                                width = 90.dp,
                                colors = colors
                            )

                            // To die (Editable with tap)
                            Box(
                                modifier = Modifier
                                    .width(95.dp)
                                    .background(colors.accentPrimary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                    .clickable {
                                        editingPassIndex = index
                                        editDieText = String.format(Locale.US, "%.3f", pass.toDie)
                                    }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format(Locale.US, "%.3f", pass.toDie),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.accentPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit to-die",
                                        modifier = Modifier.width(10.dp),
                                        tint = colors.accentPrimary.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // Area Before
                            TableCell(
                                text = String.format(Locale.US, "%.3f", pass.areaBefore),
                                width = 95.dp,
                                colors = colors
                            )

                            // Area After
                            TableCell(
                                text = String.format(Locale.US, "%.3f", pass.areaAfter),
                                width = 95.dp,
                                colors = colors
                            )

                            // Reduction %
                            TableCell(
                                text = String.format(Locale.US, "%.2f%%", pass.areaReductionPercent),
                                width = 95.dp,
                                colors = colors
                            )

                            // Elongation %
                            TableCell(
                                text = String.format(Locale.US, "%.2f%%", pass.elongationPercent),
                                width = 100.dp,
                                isBold = true,
                                colors = colors,
                                textColor = if (pass.elongationPercent > 30.0) Color(0xFFEF4444) else colors.textPrimary
                            )

                            // Ratio
                            TableCell(
                                text = String.format(Locale.US, "%.3f", pass.reductionRatio),
                                width = 80.dp,
                                colors = colors
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit To-Die Dialog
    if (editingPassIndex != null) {
        val passIdx = editingPassIndex!!
        val currentPass = passes.getOrNull(passIdx)
        if (currentPass != null) {
            AlertDialog(
                onDismissRequest = { editingPassIndex = null },
                title = {
                    Text(
                        text = "Edit Pass #${currentPass.passNumber} Outlet Die",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Inlet Diameter (From): ${String.format(Locale.US, "%.3f", currentPass.fromDie)} mm",
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editDieText,
                            onValueChange = { editDieText = it },
                            label = { Text("New Outlet Diameter (To mm)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val newNum = editDieText.toDoubleOrNull()
                            if (newNum != null && newNum > 0.0) {
                                // Pass index is (passIdx + 1) in the dies array
                                onEditToDie(passIdx + 1, newNum)
                                editingPassIndex = null
                            }
                        }
                    ) {
                        Text("Update & Recalculate")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingPassIndex = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    colors: CalculatorColors,
    isHeader: Boolean = false,
    isBold: Boolean = false,
    textColor: Color? = null
) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 4.dp),
        fontFamily = if (isHeader) FontFamily.Default else FontFamily.Monospace,
        fontSize = if (isHeader) 11.sp else 12.sp,
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        color = textColor ?: if (isHeader) colors.textSecondary else colors.textPrimary,
        textAlign = TextAlign.End
    )
}
