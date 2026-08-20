package com.sauryah.lihas.calculator.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sauryah.lihas.calculator.domain.engine.CalculatorEngine
import com.sauryah.lihas.calculator.domain.engine.NumberFormatter
import com.sauryah.lihas.calculator.domain.model.CalculationRecord
import com.sauryah.lihas.calculator.domain.model.CalculatorAction
import com.sauryah.lihas.calculator.domain.model.EvaluationResult
import com.sauryah.lihas.calculator.domain.model.ThemeMode
import com.sauryah.lihas.calculator.domain.model.UserPreferences
import com.sauryah.lihas.calculator.domain.repository.HistoryRepository
import com.sauryah.lihas.calculator.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CalculatorViewModel(
    private val engine: CalculatorEngine = CalculatorEngine(),
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    val userPreferences: StateFlow<UserPreferences> = settingsRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val history: StateFlow<List<CalculationRecord>> = historyRepository.getHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> handleNumber(action.number)
            is CalculatorAction.Decimal -> handleDecimal()
            is CalculatorAction.Operator -> handleOperator(action.operator.symbol)
            is CalculatorAction.Clear -> handleClear()
            is CalculatorAction.Delete -> handleDelete()
            is CalculatorAction.Calculate -> handleCalculate()
            is CalculatorAction.Parentheses -> handleParentheses()
            is CalculatorAction.Percentage -> handlePercentage()
            is CalculatorAction.ToggleSign -> handleToggleSign()
            is CalculatorAction.SetExpression -> handleSetExpression(action.expression)
            is CalculatorAction.UseResult -> handleUseResult(action.result)
        }
    }

    private fun handleNumber(number: Int) {
        _uiState.update { current ->
            val newExpr = if (current.isError || current.isCalculated || current.expression == "0") {
                number.toString()
            } else {
                current.expression + number.toString()
            }

            val preview = evaluateLivePreview(newExpr)
            current.copy(
                expression = newExpr,
                displayExpression = NumberFormatter.formatDisplayExpression(newExpr),
                previewResult = preview,
                isError = false,
                errorMessage = null,
                isCalculated = false
            )
        }
    }

    private fun handleDecimal() {
        _uiState.update { current ->
            if (current.isError || current.isCalculated) {
                return@update current.copy(
                    expression = "0.",
                    displayExpression = "0.",
                    previewResult = null,
                    isError = false,
                    errorMessage = null,
                    isCalculated = false
                )
            }

            val expr = current.expression
            val lastNumberSegment = expr.takeLastWhile { it.isDigit() || it == '.' }

            val newExpr = if (lastNumberSegment.contains('.')) {
                expr // Already contains decimal in current number segment
            } else if (lastNumberSegment.isEmpty()) {
                if (expr.isEmpty() || isEndingWithOperator(expr) || expr.endsWith("(")) {
                    expr + "0."
                } else {
                    expr + "."
                }
            } else {
                expr + "."
            }

            val preview = evaluateLivePreview(newExpr)
            current.copy(
                expression = newExpr,
                displayExpression = NumberFormatter.formatDisplayExpression(newExpr),
                previewResult = preview,
                isError = false,
                errorMessage = null,
                isCalculated = false
            )
        }
    }

    private fun handleOperator(opSymbol: String) {
        _uiState.update { current ->
            engine.clearRepeatedOperation()

            val baseExpr = if (current.isCalculated && current.result.isNotEmpty()) {
                current.result
            } else if (current.isError) {
                "0"
            } else {
                current.expression
            }

            val newExpr = if (isEndingWithOperator(baseExpr)) {
                baseExpr.dropLast(1) + opSymbol
            } else {
                baseExpr + opSymbol
            }

            val preview = evaluateLivePreview(newExpr)
            current.copy(
                expression = newExpr,
                displayExpression = NumberFormatter.formatDisplayExpression(newExpr),
                previewResult = preview,
                isError = false,
                errorMessage = null,
                isCalculated = false
            )
        }
    }

    private fun handleClear() {
        engine.clearRepeatedOperation()
        _uiState.value = CalculatorState.initial()
    }

    private fun handleDelete() {
        _uiState.update { current ->
            if (current.isError || current.isCalculated) {
                return@update CalculatorState.initial()
            }

            val expr = current.expression
            val newExpr = if (expr.length > 1) {
                expr.dropLast(1)
            } else {
                "0"
            }

            val preview = evaluateLivePreview(newExpr)
            current.copy(
                expression = newExpr,
                displayExpression = NumberFormatter.formatDisplayExpression(newExpr),
                previewResult = preview,
                isError = false,
                errorMessage = null,
                isCalculated = false
            )
        }
    }

    private fun handleCalculate() {
        val state = _uiState.value

        if (state.isCalculated && state.result.isNotEmpty()) {
            // Repeated equals operation
            val currentVal = try {
                BigDecimal(state.result)
            } catch (e: Exception) {
                null
            }

            if (currentVal != null) {
                val repeatedResult = engine.repeatLastOperation(currentVal)
                if (repeatedResult is EvaluationResult.Success) {
                    saveCalculationToHistory(state.expression, repeatedResult.formatted)
                    _uiState.update {
                        it.copy(
                            expression = repeatedResult.formatted,
                            displayExpression = NumberFormatter.formatDisplayExpression(repeatedResult.formatted),
                            result = repeatedResult.formatted,
                            previewResult = null,
                            isCalculated = true,
                            isError = false,
                            errorMessage = null
                        )
                    }
                }
            }
            return
        }

        when (val result = engine.evaluate(state.expression)) {
            is EvaluationResult.Success -> {
                saveCalculationToHistory(state.expression, result.formatted)
                _uiState.update {
                    it.copy(
                        result = result.formatted,
                        previewResult = null,
                        isCalculated = true,
                        isError = false,
                        errorMessage = null
                    )
                }
            }

            is EvaluationResult.Error -> {
                _uiState.update {
                    it.copy(
                        isError = true,
                        errorMessage = result.userMessage,
                        previewResult = null
                    )
                }
            }
        }
    }

    private fun handleParentheses() {
        _uiState.update { current ->
            val expr = if (current.isCalculated || current.isError) "0" else current.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val lastChar = expr.lastOrNull()

            val newExpr = when {
                expr == "0" -> "("
                lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == '%') -> {
                    if (openCount > closeCount) {
                        "$expr)"
                    } else {
                        "$expr*("
                    }
                }
                lastChar != null && (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/' || lastChar == '(') -> {
                    "$expr("
                }
                else -> "$expr("
            }

            val preview = evaluateLivePreview(newExpr)
            current.copy(
                expression = newExpr,
                displayExpression = NumberFormatter.formatDisplayExpression(newExpr),
                previewResult = preview,
                isError = false,
                errorMessage = null,
                isCalculated = false
            )
        }
    }

    private fun handlePercentage() {
        _uiState.update { current ->
            val expr = if (current.isCalculated && current.result.isNotEmpty()) current.result else current.expression
            val lastChar = expr.lastOrNull()

            if (lastChar != null && (lastChar.isDigit() || lastChar == ')')) {
                val newExpr = "$expr%"
                val preview = evaluateLivePreview(newExpr)
                current.copy(
                    expression = newExpr,
                    displayExpression = NumberFormatter.formatDisplayExpression(newExpr),
                    previewResult = preview,
                    isError = false,
                    errorMessage = null,
                    isCalculated = false
                )
            } else {
                current
            }
        }
    }

    private fun handleToggleSign() {
        _uiState.update { current ->
            val expr = if (current.isCalculated && current.result.isNotEmpty()) current.result else current.expression
            if (expr == "0" || expr.isEmpty()) return@update current

            // Toggle sign of last number token
            val newExpr = if (expr.startsWith("-") && !expr.drop(1).any { it == '+' || it == '-' || it == '*' || it == '/' }) {
                expr.drop(1)
            } else if (!expr.any { it == '+' || it == '-' || it == '*' || it == '/' }) {
                "-$expr"
            } else {
                // If it ends with parenthesized negative like +(-5), toggle to +5
                if (expr.endsWith(")") && expr.contains("(-")) {
                    val lastOpenParenIndex = expr.lastIndexOf("(-")
                    if (lastOpenParenIndex != -1) {
                        val before = expr.substring(0, lastOpenParenIndex)
                        val numPart = expr.substring(lastOpenParenIndex + 2, expr.length - 1)
                        "$before$numPart"
                    } else {
                        expr
                    }
                } else {
                    // Extract trailing number
                    val lastNumberSegment = expr.takeLastWhile { it.isDigit() || it == '.' }
                    if (lastNumberSegment.isNotEmpty()) {
                        val prefix = expr.dropLast(lastNumberSegment.length)
                        "$prefix(-$lastNumberSegment)"
                    } else {
                        expr
                    }
                }
            }

            val preview = evaluateLivePreview(newExpr)
            current.copy(
                expression = newExpr,
                displayExpression = NumberFormatter.formatDisplayExpression(newExpr),
                previewResult = preview,
                isError = false,
                errorMessage = null,
                isCalculated = false
            )
        }
    }

    private fun handleSetExpression(expr: String) {
        val preview = evaluateLivePreview(expr)
        _uiState.value = CalculatorState(
            expression = expr,
            displayExpression = NumberFormatter.formatDisplayExpression(expr),
            previewResult = preview,
            isCalculated = false,
            isError = false
        )
    }

    private fun handleUseResult(res: String) {
        _uiState.value = CalculatorState(
            expression = res,
            displayExpression = NumberFormatter.formatDisplayExpression(res),
            result = res,
            isCalculated = false,
            isError = false
        )
    }

    private fun evaluateLivePreview(expr: String): String? {
        return when (val preview = engine.evaluatePreview(expr)) {
            is EvaluationResult.Success -> {
                if (preview.formatted != expr) preview.formatted else null
            }
            else -> null
        }
    }

    private fun isEndingWithOperator(expr: String): Boolean {
        return expr.endsWith("+") || expr.endsWith("-") || expr.endsWith("*") || expr.endsWith("/")
    }

    private fun saveCalculationToHistory(expression: String, result: String) {
        viewModelScope.launch {
            try {
                historyRepository.saveCalculation(expression, result)
            } catch (e: Exception) {
                // Ignore history save error
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHapticsEnabled(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSoundEnabled(enabled)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteCalculation(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }
}
