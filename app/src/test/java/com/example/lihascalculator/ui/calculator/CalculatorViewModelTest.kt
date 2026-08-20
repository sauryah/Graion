package com.sauryah.lihas.calculator.ui.calculator

import com.sauryah.lihas.calculator.domain.engine.CalculatorEngine
import com.sauryah.lihas.calculator.domain.model.CalculationRecord
import com.sauryah.lihas.calculator.domain.model.CalculatorAction
import com.sauryah.lihas.calculator.domain.model.CalculatorOperator
import com.sauryah.lihas.calculator.domain.model.ThemeMode
import com.sauryah.lihas.calculator.domain.model.UserPreferences
import com.sauryah.lihas.calculator.domain.repository.HistoryRepository
import com.sauryah.lihas.calculator.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeHistoryRepository: FakeHistoryRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeHistoryRepository = FakeHistoryRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = CalculatorViewModel(
            engine = CalculatorEngine(),
            historyRepository = fakeHistoryRepository,
            settingsRepository = fakeSettingsRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        val state = viewModel.uiState.value
        assertEquals("0", state.expression)
        assertEquals("", state.result)
        assertNull(state.previewResult)
        assertFalse(state.isError)
        assertFalse(state.isCalculated)
    }

    @Test
    fun testNumberInput() {
        viewModel.onAction(CalculatorAction.Number(5))
        assertEquals("5", viewModel.uiState.value.expression)

        viewModel.onAction(CalculatorAction.Number(2))
        assertEquals("52", viewModel.uiState.value.expression)
    }

    @Test
    fun testDecimalInput() {
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Decimal)
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Number(4))
        assertEquals("3.14", viewModel.uiState.value.expression)

        // Multiple decimal taps in same number segment are ignored
        viewModel.onAction(CalculatorAction.Decimal)
        assertEquals("3.14", viewModel.uiState.value.expression)
    }

    @Test
    fun testBasicCalculation() = runTest {
        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Operator(CalculatorOperator.ADD))
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Operator(CalculatorOperator.MULTIPLY))
        viewModel.onAction(CalculatorAction.Number(4))

        // Live preview before equals
        assertEquals("14", viewModel.uiState.value.previewResult)

        // Press equals
        viewModel.onAction(CalculatorAction.Calculate)
        advanceUntilIdle()

        assertEquals("14", viewModel.uiState.value.result)
        assertTrue(viewModel.uiState.value.isCalculated)
        assertFalse(viewModel.uiState.value.isError)

        // Verify calculation was saved in history repository
        assertEquals(1, fakeHistoryRepository.historyList.size)
        assertEquals("2+3*4", fakeHistoryRepository.historyList[0].expression)
        assertEquals("14", fakeHistoryRepository.historyList[0].result)
    }

    @Test
    fun testRepeatedEquals() = runTest {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Operator(CalculatorOperator.ADD))
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Calculate)
        advanceUntilIdle()

        assertEquals("8", viewModel.uiState.value.result)

        // Repeated equals
        viewModel.onAction(CalculatorAction.Calculate)
        advanceUntilIdle()
        assertEquals("11", viewModel.uiState.value.result)

        viewModel.onAction(CalculatorAction.Calculate)
        advanceUntilIdle()
        assertEquals("14", viewModel.uiState.value.result)
    }

    @Test
    fun testDivisionByZero() = runTest {
        viewModel.onAction(CalculatorAction.Number(9))
        viewModel.onAction(CalculatorAction.Operator(CalculatorOperator.DIVIDE))
        viewModel.onAction(CalculatorAction.Number(0))
        viewModel.onAction(CalculatorAction.Calculate)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isError)
        assertEquals("Cannot divide by zero", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun testClearAction() {
        viewModel.onAction(CalculatorAction.Number(9))
        viewModel.onAction(CalculatorAction.Operator(CalculatorOperator.ADD))
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Clear)

        assertEquals("0", viewModel.uiState.value.expression)
        assertEquals("", viewModel.uiState.value.result)
        assertNull(viewModel.uiState.value.previewResult)
    }

    @Test
    fun testDeleteAction() {
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Number(3))
        assertEquals("123", viewModel.uiState.value.expression)

        viewModel.onAction(CalculatorAction.Delete)
        assertEquals("12", viewModel.uiState.value.expression)

        viewModel.onAction(CalculatorAction.Delete)
        assertEquals("1", viewModel.uiState.value.expression)

        viewModel.onAction(CalculatorAction.Delete)
        assertEquals("0", viewModel.uiState.value.expression)
    }

    @Test
    fun testSmartParentheses() {
        // Starts with 0 -> (
        viewModel.onAction(CalculatorAction.Parentheses)
        assertEquals("(", viewModel.uiState.value.expression)

        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Operator(CalculatorOperator.ADD))
        viewModel.onAction(CalculatorAction.Number(3))

        // Open count > close count -> )
        viewModel.onAction(CalculatorAction.Parentheses)
        assertEquals("(2+3)", viewModel.uiState.value.expression)

        // After closed paren -> implicit *(
        viewModel.onAction(CalculatorAction.Parentheses)
        assertEquals("(2+3)*(", viewModel.uiState.value.expression)
    }

    @Test
    fun testToggleSign() {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.ToggleSign)
        assertEquals("-5", viewModel.uiState.value.expression)

        viewModel.onAction(CalculatorAction.ToggleSign)
        assertEquals("5", viewModel.uiState.value.expression)
    }

    @Test
    fun testRestoreFromHistory() {
        viewModel.onAction(CalculatorAction.SetExpression("(10 + 20) * 3"))
        assertEquals("(10 + 20) * 3", viewModel.uiState.value.expression)
        assertEquals("90", viewModel.uiState.value.previewResult)
    }

    @Test
    fun testSettingsUpdates() = runTest {
        viewModel.setThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()
        assertEquals(ThemeMode.LIGHT, fakeSettingsRepository.userPreferences.value.themeMode)

        viewModel.setHapticsEnabled(false)
        advanceUntilIdle()
        assertFalse(fakeSettingsRepository.userPreferences.value.hapticsEnabled)

        viewModel.setSoundEnabled(true)
        advanceUntilIdle()
        assertTrue(fakeSettingsRepository.userPreferences.value.soundEnabled)
    }
}

private class FakeHistoryRepository : HistoryRepository {
    val historyList = mutableListOf<CalculationRecord>()
    private val flow = MutableStateFlow<List<CalculationRecord>>(emptyList())

    override fun getHistory(): Flow<List<CalculationRecord>> = flow.asStateFlow()

    override suspend fun saveCalculation(expression: String, result: String): Long {
        val record = CalculationRecord(
            id = historyList.size.toLong() + 1,
            expression = expression,
            result = result
        )
        historyList.add(0, record)
        flow.value = historyList.toList()
        return record.id
    }

    override suspend fun deleteCalculation(id: Long) {
        historyList.removeAll { it.id == id }
        flow.value = historyList.toList()
    }

    override suspend fun clearHistory() {
        historyList.clear()
        flow.value = emptyList()
    }
}

private class FakeSettingsRepository : SettingsRepository {
    val userPreferences = MutableStateFlow(UserPreferences())
    override val userPreferencesFlow: Flow<UserPreferences> = userPreferences.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        userPreferences.value = userPreferences.value.copy(themeMode = mode)
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        userPreferences.value = userPreferences.value.copy(hapticsEnabled = enabled)
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        userPreferences.value = userPreferences.value.copy(soundEnabled = enabled)
    }
}
