package com.example.lihascalculator.ui.wiredrawing.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassDetailBottomSheet(
    pass: PassResult?,
    colors: CalculatorColors,
    onDismiss: () -> Unit,
    onEditDieClick: (PassResult) -> Unit,
    onViewCadClick: (PassResult) -> Unit
) {
    if (pass == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(colors.surfaceVariant, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PASS ${String.format(Locale.US, "%02d", pass.passNumber)} DETAILS",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .background(colors.accentPrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ACTIVE PASS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Diameter comparison box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "FROM (INLET)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.textSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%.3f mm", pass.fromDie),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = String.format(Locale.US, "%.3f mm²", pass.areaBefore),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                Text(text = "→", fontSize = 22.sp, color = colors.accentPrimary, fontWeight = FontWeight.Bold)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "TO (OUTLET)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.accentPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%.3f mm", pass.toDie),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentPrimary
                    )
                    Text(
                        text = String.format(Locale.US, "%.3f mm²", pass.areaAfter),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Details List
            DetailRow(label = "ELONGATION", value = String.format(Locale.US, "%.2f%%", pass.elongationPercent), colors = colors, isHighlight = true)
            DetailRow(label = "AREA REDUCTION", value = String.format(Locale.US, "%.2f%%", pass.areaReductionPercent), colors = colors)
            DetailRow(label = "REDUCTION RATIO", value = String.format(Locale.US, "%.3f", pass.reductionRatio), colors = colors)

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onViewCadClick(pass) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accentPrimary),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colors.accentPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "View CAD", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onEditDieClick(pass) },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Edit Die", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    colors: CalculatorColors,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) colors.accentPrimary else colors.textPrimary
        )
    }
}
