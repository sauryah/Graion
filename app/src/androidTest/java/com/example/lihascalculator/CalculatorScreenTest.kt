package com.example.lihascalculator

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.lihascalculator.domain.model.CalculatorAction
import com.example.lihascalculator.domain.model.CalculatorOperator
import com.example.lihascalculator.domain.model.UserPreferences
import com.example.lihascalculator.theme.LihasCalculatorTheme
import com.example.lihascalculator.ui.calculator.CalculatorScreen
import com.example.lihascalculator.ui.calculator.CalculatorState
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
            LihasCalculatorTheme {
                CalculatorScreen(
                    state = state,
                    preferences = preferences,
                    onAction = {},
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
            LihasCalculatorTheme {
                CalculatorScreen(
                    state = state,
                    preferences = preferences,
                    onAction = { recordedActions.add(it) },
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
