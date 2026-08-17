package com.example.lihascalculator.ui.wiredrawing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.lihascalculator.domain.model.wiredrawing.PassResult
import com.example.lihascalculator.theme.CalculatorColors
import java.util.Locale

@Composable
fun EditDieDialog(
    pass: PassResult?,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onApply: (passNumber: Int, newToDie: Double) -> Unit
) {
    if (pass == null) return

    var toDieText by remember(pass) {
        mutableStateOf(String.format(Locale.US, "%.3f", pass.toDie))
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PASS ${String.format(Locale.US, "%02d", pass.passNumber)}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Edit the outlet die diameter for this pass.",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // From Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(text = "From (Inlet)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.textSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.US, "%.3f mm", pass.fromDie),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // To Input
                OutlinedTextField(
                    value = toDieText,
                    onValueChange = {
                        toDieText = it
                        errorMessage = null
                    },
                    label = { Text("To (Outlet mm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val num = toDieText.toDoubleOrNull()
                    if (num == null || num <= 0.0) {
                        errorMessage = "Please enter a valid positive diameter."
                        return@Button
                    }
                    onApply(pass.passNumber, num)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "APPLY", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = colors.textSecondary)
            }
        }
    )
}
