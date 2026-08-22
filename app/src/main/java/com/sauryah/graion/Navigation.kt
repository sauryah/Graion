package com.sauryah.graion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sauryah.graion.domain.model.CalculatorAction
import com.sauryah.graion.ui.calculator.CalculatorScreen
import com.sauryah.graion.ui.calculator.CalculatorViewModel
import com.sauryah.graion.ui.history.HistoryScreen
import com.sauryah.graion.ui.settings.SettingsScreen
import com.sauryah.graion.ui.tools.ToolsLandingScreen
import com.sauryah.graion.ui.unitconverter.UnitConverterScreen
import com.sauryah.graion.ui.wiredrawing.WireDrawingScreen
import com.sauryah.graion.ui.wiredrawing.WireDrawingViewModel
import com.sauryah.graion.ui.wiredrawing.WireDrawingViewModelFactory

@Composable
fun MainNavigation(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(CalculatorRoute)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val historyList by viewModel.history.collectAsStateWithLifecycle()

    val wireDrawingViewModel: WireDrawingViewModel = viewModel(
        factory = WireDrawingViewModelFactory(context.applicationContext)
    )
    val wireDrawingState by wireDrawingViewModel.uiState.collectAsStateWithLifecycle()

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
                    onNavigateToUnitConverter = { backStack.add(UnitConverterRoute) },
                    onNavigateToPythonWorkbench = { backStack.add(PythonWorkbenchRoute) },
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }

            entry<PythonWorkbenchRoute> {
                com.sauryah.graion.ui.python.PythonWorkbenchScreen(
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }

            entry<UnitConverterRoute> {
                UnitConverterScreen(
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
