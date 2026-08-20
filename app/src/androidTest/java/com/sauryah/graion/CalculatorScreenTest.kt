package com.sauryah.graion

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.sauryah.graion.domain.model.CalculatorAction
import com.sauryah.graion.domain.model.CalculatorOperator
import com.sauryah.graion.domain.model.UserPreferences
import com.sauryah.graion.theme.GraionTheme
import com.sauryah.graion.ui.calculator.CalculatorScreen
import com.sauryah.graion.ui.calculator.CalculatorState
import org.junit.Rule
import org.junit.Test

class CalculatorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCalculatorButtonsDisplayed() {
        val state = CalculatorState(expression = "0")
        val preferences = UserPreferences()

        composeTestRule.setContent {
            GraionTheme {
                CalculatorScreen(
                    state = state,
                    preferences = preferences,
                    onAction = {},
                    onNavigateToTools = {},
                    onNavigateToHistory = {},
                    onNavigateToSettings = {}
                )
            }
        }

        // Check essential keypad elements
        composeTestRule.onNodeWithText("AC").assertIsDisplayed()
        composeTestRule.onNodeWithText("=").assertIsDisplayed()
        composeTestRule.onNodeWithText("7").assertIsDisplayed()
        composeTestRule.onNodeWithText("8").assertIsDisplayed()
        composeTestRule.onNodeWithText("9").assertIsDisplayed()
        composeTestRule.onNodeWithText("×").assertIsDisplayed()
        composeTestRule.onNodeWithText("÷").assertIsDisplayed()
        composeTestRule.onNodeWithText("+").assertIsDisplayed()
        composeTestRule.onNodeWithText("−").assertIsDisplayed()
    }

    @Test
    fun testButtonInteractions() {
        val recordedActions = mutableListOf<CalculatorAction>()
        val state = CalculatorState(expression = "2+3", result = "5", isCalculated = true)
        val preferences = UserPreferences()

        composeTestRule.setContent {
            GraionTheme {
                CalculatorScreen(
                    state = state,
                    preferences = preferences,
                    onAction = { recordedActions.add(it) },
                    onNavigateToTools = {},
                    onNavigateToHistory = {},
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("=").performClick()

        assert(recordedActions.contains(CalculatorAction.Number(5)))
        assert(recordedActions.contains(CalculatorAction.Operator(CalculatorOperator.ADD)))
        assert(recordedActions.contains(CalculatorAction.Calculate))
    }
}
