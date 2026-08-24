package com.sauryah.graion.ui.wiredrawing.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.graion.domain.engine.wiredrawing.WireDrawingCalculatorEngine
import com.sauryah.graion.domain.model.wiredrawing.WireMaterial
import com.sauryah.graion.theme.CalculatorColors
import java.util.Locale

@Composable
fun WireWeightDialog(
    isOpen: Boolean,
    initialDiameterMm: Double,
    colors: CalculatorColors,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var selectedMaterial by remember { mutableStateOf(WireMaterial.COPPER) }
    var diameterText by remember(initialDiameterMm) {
        mutableStateOf(if (initialDiameterMm > 0) String.format(Locale.US, "%.3f", initialDiameterMm) else "2.000")
    }
    var lengthText by remember { mutableStateOf("1000") }
    var weightText by remember { mutableStateOf("") }
    var isLengthMode by remember { mutableStateOf(true) } // true: Length -> Weight, false: Weight -> Length
    var materialDropdownExpanded by remember { mutableStateOf(false) }

    val diameter = diameterText.toDoubleOrNull() ?: 0.0
    val result = if (isLengthMode) {
        val length = lengthText.toDoubleOrNull() ?: 0.0
        WireDrawingCalculatorEngine.calculateWeightKgFromLength(diameter, length, selectedMaterial)
    } else {
        val weight = weightText.toDoubleOrNull() ?: 0.0
        WireDrawingCalculatorEngine.calculateLengthMetresFromWeight(diameter, weight, selectedMaterial)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Scale,
                    contentDescription = null,
                    tint = colors.accentPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Wire Weight & Length",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Material Selector
                Column {
                    Text(
                        text = "MATERIAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { materialDropdownExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = selectedMaterial.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "Density: ${selectedMaterial.densityGPerCm3} g/cm³",
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Material",
                                    tint = colors.textPrimary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = materialDropdownExpanded,
                            onDismissRequest = { materialDropdownExpanded = false }
                        ) {
                            WireMaterial.entries.forEach { material ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(material.displayName, fontWeight = FontWeight.Medium)
                                            Text("${material.densityGPerCm3} g/cm³", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        selectedMaterial = material
                                        materialDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Diameter Input
                OutlinedTextField(
                    value = diameterText,
                    onValueChange = { diameterText = it },
                    label = { Text("Wire Diameter (mm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Mode Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isLengthMode) colors.accentPrimary.copy(alpha = 0.15f) else colors.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { isLengthMode = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GIVEN LENGTH",
                            fontSize = 11.sp,
                            fontWeight = if (isLengthMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (isLengthMode) colors.accentPrimary else colors.textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (!isLengthMode) colors.accentPrimary.copy(alpha = 0.15f) else colors.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { isLengthMode = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GIVEN WEIGHT",
                            fontSize = 11.sp,
                            fontWeight = if (!isLengthMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isLengthMode) colors.accentPrimary else colors.textSecondary
                        )
                    }
                }

                if (isLengthMode) {
                    OutlinedTextField(
                        value = lengthText,
                        onValueChange = { lengthText = it },
                        label = { Text("Total Length (Metres)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Total Weight (Kilograms)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Calculation Result Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Weight:", fontSize = 12.sp, color = colors.textSecondary)
                            Text(
                                String.format(Locale.US, "%.3f kg (%.2f lbs)", result.weightKg, result.weightKg * 2.20462),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.accentPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Length:", fontSize = 12.sp, color = colors.textSecondary)
                            Text(
                                String.format(Locale.US, "%.1f m (%.1f ft)", result.lengthMetres, result.lengthMetres * 3.28084),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Linear Mass:", fontSize = 12.sp, color = colors.textSecondary)
                            Text(
                                String.format(Locale.US, "%.3f g/m", result.linearMassGPerM),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = colors.accentPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
