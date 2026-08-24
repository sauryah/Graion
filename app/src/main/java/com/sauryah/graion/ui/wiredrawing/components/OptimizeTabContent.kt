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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.graion.theme.CalculatorColors
import com.sauryah.graion.ui.wiredrawing.WireDrawingState

import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Speed

fun LazyListScope.optimizeTabContent(
    state: WireDrawingState,
    colors: CalculatorColors,
    onOpenGenerateSeries: () -> Unit,
    onOpenTargetCheck: () -> Unit,
    onOpenSuggestDies: () -> Unit,
    onOpenWireWeight: () -> Unit,
    onOpenKinematics: () -> Unit
) {
    item {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
            Text(
                text = "Optimize",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "Generate new die sequences, check target tolerances, and resolve inconsistent passes.",
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }
    }

    // 1. Generate Series Card
    item {
        OptimizationToolCard(
            title = "GENERATE SERIES",
            subtitle = "Create a constant-elongation schedule from target start/end diameters.",
            badge = "SERIES GENERATOR",
            icon = Icons.Default.AutoAwesome,
            colors = colors,
            onClick = onOpenGenerateSeries
        )
    }

    // 2. Target Check Card
    item {
        val outCount = state.targetCheckResults.count { it.isOutOfRange }
        val badgeText = if (state.isValidSchedule) {
            if (outCount > 0) "$outCount OUT OF RANGE" else "ALL IN RANGE"
        } else {
            "LIMIT CHECKER"
        }

        OptimizationToolCard(
            title = "TARGET CHECK",
            subtitle = "Verify all passes against acceptable elongation tolerance limits (e.g. 15%–22%).",
            badge = badgeText,
            icon = Icons.Default.GpsFixed,
            colors = colors,
            onClick = onOpenTargetCheck
        )
    }

    // 3. Suggest Dies Card
    item {
        val suggCount = state.suggestedPasses.size
        val badgeText = if (state.isValidSchedule && suggCount > 0) "$suggCount IMPROVEMENTS" else "DIE OPTIMIZER"

        OptimizationToolCard(
            title = "SUGGEST DIES",
            subtitle = "Find passes deviating from target elongation and insert intermediate step dies.",
            badge = badgeText,
            icon = Icons.Default.AutoFixHigh,
            colors = colors,
            onClick = onOpenSuggestDies
        )
    }

    // 4. Wire Weight & Length Calculator Card
    item {
        OptimizationToolCard(
            title = "WIRE WEIGHT & LENGTH",
            subtitle = "Calculate total coil weight, linear mass in g/m, and length for Cu, Al, Steel, Brass & Alloys.",
            badge = "WEIGHT & LENGTH",
            icon = Icons.Default.Scale,
            colors = colors,
            onClick = onOpenWireWeight
        )
    }

    // 5. Line Speed & Kinematics Card
    item {
        OptimizationToolCard(
            title = "LINE SPEED & KINEMATICS",
            subtitle = "Calculate capstan drawing speeds, slip ratios, and production throughput (kg/hr & tonnes/shift).",
            badge = "KINEMATICS",
            icon = Icons.Default.Speed,
            colors = colors,
            onClick = onOpenKinematics
        )
    }
}

@Composable
private fun OptimizationToolCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    colors: CalculatorColors,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(colors.accentPrimary.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(colors.background.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = colors.textSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configure & Run",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accentPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = colors.accentPrimary
                )
            }
        }
    }
}
