package com.example.lihascalculator

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.lihascalculator.domain.model.CalculatorAction
import com.example.lihascalculator.ui.calculator.CalculatorScreen
import com.example.lihascalculator.ui.calculator.CalculatorViewModel
import com.example.lihascalculator.ui.history.HistoryScreen
import com.example.lihascalculator.ui.settings.SettingsScreen

@Composable
fun MainNavigation(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(CalculatorRoute)
    val state by viewModel.uiState.collectAsState()
    val preferences by viewModel.userPreferences.collectAsState()
    val historyList by viewModel.history.collectAsState()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier.fillMaxSize(),
        entryProvider = entryProvider {
            entry<CalculatorRoute> {
                CalculatorScreen(
                    state = state,
                    preferences = preferences,
                    onAction = viewModel::onAction,
                    onNavigateToHistory = { backStack.add(HistoryRoute) },
                    onNavigateToSettings = { backStack.add(SettingsRoute) }
                )
            }

            entry<HistoryRoute> {
                HistoryScreen(
                    historyList = historyList,
                    onBackClick = { backStack.removeLastOrNull() },
                    onItemClick = { record ->
                        viewModel.onAction(CalculatorAction.SetExpression(record.expression))
                        backStack.removeLastOrNull()
                    },
                    onDeleteItem = { id -> viewModel.deleteHistoryItem(id) },
                    onClearAll = { viewModel.clearHistory() }
                )
            }

            entry<SettingsRoute> {
                SettingsScreen(
                    preferences = preferences,
                    onThemeChange = { mode -> viewModel.setThemeMode(mode) },
                    onHapticsChange = { enabled -> viewModel.setHapticsEnabled(enabled) },
                    onSoundChange = { enabled -> viewModel.setSoundEnabled(enabled) },
                    onClearHistory = { viewModel.clearHistory() },
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
