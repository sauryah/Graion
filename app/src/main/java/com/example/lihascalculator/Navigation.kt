package com.example.lihascalculator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.lihascalculator.domain.model.CalculatorAction
import com.example.lihascalculator.ui.calculator.CalculatorScreen
import com.example.lihascalculator.ui.calculator.CalculatorViewModel
import com.example.lihascalculator.ui.history.HistoryScreen
import com.example.lihascalculator.ui.settings.SettingsScreen
import com.example.lihascalculator.ui.tools.ToolsLandingScreen
import com.example.lihascalculator.ui.wiredrawing.WireDrawingScreen
import com.example.lihascalculator.ui.wiredrawing.WireDrawingViewModel
import com.example.lihascalculator.ui.wiredrawing.WireDrawingViewModelFactory

@Composable
fun MainNavigation(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(CalculatorRoute)
    val state by viewModel.uiState.collectAsState()
    val preferences by viewModel.userPreferences.collectAsState()
    val historyList by viewModel.history.collectAsState()

    val wireDrawingViewModel: WireDrawingViewModel = viewModel(
        factory = WireDrawingViewModelFactory(context)
    )
    val wireDrawingState by wireDrawingViewModel.uiState.collectAsState()

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
                    onNavigateToTools = { backStack.add(ToolsRoute) },
                    onNavigateToHistory = { backStack.add(HistoryRoute) },
                    onNavigateToSettings = { backStack.add(SettingsRoute) }
                )
            }

            entry<ToolsRoute> {
                ToolsLandingScreen(
                    onNavigateToWireDrawing = { backStack.add(WireDrawingRoute) },
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }

            entry<WireDrawingRoute> {
                WireDrawingScreen(
                    state = wireDrawingState,
                    viewModel = wireDrawingViewModel,
                    onBackClick = { backStack.removeLastOrNull() }
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
