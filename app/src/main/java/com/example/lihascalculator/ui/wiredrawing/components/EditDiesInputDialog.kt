package com.example.lihascalculator.ui.wiredrawing.components

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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

@Composable
fun EditDiesInputDialog(
    isOpen: Boolean,
    state: WireDrawingState,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onInputChanged: (String) -> Unit,
    onCalculate: () -> Unit,
    onPasteExample: () -> Unit,
    onClear: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Die Sequence",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter a sequence of die diameters separated by commas, spaces, or newlines.",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = state.inputText,
                    onValueChange = onInputChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    placeholder = {
                        Text("2.490, 2.217, 1.974, 1.757...", color = colors.textSecondary.copy(alpha = 0.5f), fontSize = 13.sp)
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

                if (state.validationErrors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEF4444).copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        state.validationErrors.take(2).forEach { err ->
                            Text(
                                text = "• $err",
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Helper Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData = clipboard.primaryClip
                                if (clipData != null && clipData.itemCount > 0) {
                                    val pasted = clipData.getItemAt(0).text?.toString() ?: ""
                                    if (pasted.isNotBlank()) onInputChanged(pasted)
                                }
                            } catch (_: Exception) {}
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.textSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Paste", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onPasteExample,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.textSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Example", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onClear,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.9f).height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.textSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Clear", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCalculate,
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Calculate", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}
