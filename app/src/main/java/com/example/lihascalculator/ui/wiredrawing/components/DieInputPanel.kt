package com.example.lihascalculator.ui.wiredrawing.components

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lihascalculator.theme.CalculatorColors
import com.example.lihascalculator.ui.wiredrawing.WireDrawingState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DieInputPanel(
    state: WireDrawingState,
    colors: CalculatorColors,
    onInputChanged: (String) -> Unit,
    onCalculate: () -> Unit,
    onPasteExample: () -> Unit,
    onClear: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onOpenSeriesGenerator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DIE DIAMETER SEQUENCE",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(colors.accentPrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${state.dies.size} DIES",
                            color = colors.accentPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Undo / Redo Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onUndo,
                        enabled = state.canUndo,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo die change",
                            tint = if (state.canUndo) colors.textPrimary else colors.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onRedo,
                        enabled = state.canRedo,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo die change",
                            tint = if (state.canRedo) colors.textPrimary else colors.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multiline Text Field
            OutlinedTextField(
                value = state.inputText,
                onValueChange = onInputChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                placeholder = {
                    Text(
                        text = "Enter die diameters (e.g. 2.490, 2.217, 1.974...)",
                        color = colors.textSecondary.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = colors.textPrimary,
                    lineHeight = 18.sp
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentPrimary,
                    unfocusedBorderColor = colors.surfaceVariant,
                    focusedContainerColor = colors.background.copy(alpha = 0.6f),
                    unfocusedContainerColor = colors.background.copy(alpha = 0.4f)
                )
            )

            // Validation Errors Display
            if (state.validationErrors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEF4444).copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    state.validationErrors.take(3).forEach { err ->
                        Text(
                            text = "• $err",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buttons Flow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Calculate Primary Button
                Button(
                    onClick = onCalculate,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Calculate",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                // Paste Button
                OutlinedButton(
                    onClick = {
                        try {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = clipboard.primaryClip
                            if (clipData != null && clipData.itemCount > 0) {
                                val pasted = clipData.getItemAt(0).text?.toString() ?: ""
                                if (pasted.isNotBlank()) {
                                    onInputChanged(pasted)
                                }
                            }
                        } catch (_: Exception) {
                            // Clipboard failures fail silently as per specification
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Paste", fontSize = 13.sp)
                }

                // Example Button
                OutlinedButton(
                    onClick = onPasteExample,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DataObject,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Example", fontSize = 13.sp)
                }

                // Generate Series Button
                OutlinedButton(
                    onClick = onOpenSeriesGenerator,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accentPrimary),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.accentPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Generate Series", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                // Clear Button
                OutlinedButton(
                    onClick = onClear,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textSecondary),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Clear", fontSize = 13.sp)
                }
            }
        }
    }
}
