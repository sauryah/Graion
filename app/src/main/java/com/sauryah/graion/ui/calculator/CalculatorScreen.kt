package com.sauryah.graion.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.graion.domain.model.CalculatorAction
import com.sauryah.graion.domain.model.UserPreferences
import com.sauryah.graion.theme.CalculatorTheme
import com.sauryah.graion.ui.components.CalculatorKeypad
import com.sauryah.graion.ui.components.DisplayArea

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import com.sauryah.graion.domain.model.AngleMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    preferences: UserPreferences,
    onAction: (CalculatorAction) -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CalculatorTheme.colors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Graion",
                            color = colors.textPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        // Interactive DEG / RAD Mode Switch
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surfaceVariant.copy(alpha = 0.8f))
                                .clickable { onAction(CalculatorAction.ToggleAngleMode) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Angle mode ${if (preferences.angleMode == AngleMode.DEGREES) "Degrees" else "Radians"}. Tap to toggle."
                                }
                        ) {
                            Text(
                                text = if (preferences.angleMode == AngleMode.DEGREES) "DEG" else "RAD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        if (state.memory != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colors.accentPrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "M",
                                    color = colors.accentPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.semantics {
                                        contentDescription = "Memory stored: ${state.memory}"
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToTools,
                        modifier = Modifier.semantics { contentDescription = "Open Engineering Tools" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Tools",
                            tint = colors.textSecondary
                        )
                    }
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.semantics { contentDescription = "Open Calculation History" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = colors.textSecondary
                        )
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.semantics { contentDescription = "Open Settings" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = colors.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Display Area (Expression, Result, Live Preview)
            DisplayArea(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                onDeleteClick = { onAction(CalculatorAction.Delete) },
                onClearAllClick = { onAction(CalculatorAction.Clear) }
            )

            // Calculator Keypad (7 Rows)
            CalculatorKeypad(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f),
                hapticsEnabled = preferences.hapticsEnabled,
                soundEnabled = preferences.soundEnabled,
                onAction = onAction
            )
        }
    }
}
