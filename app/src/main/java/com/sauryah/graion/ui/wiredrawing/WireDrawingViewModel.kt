package com.sauryah.graion.ui.wiredrawing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sauryah.graion.domain.engine.wiredrawing.WireDrawingCalculatorEngine
import com.sauryah.graion.domain.model.wiredrawing.PassResult
import com.sauryah.graion.domain.model.wiredrawing.SavedSchedule
import com.sauryah.graion.domain.repository.WireDrawScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WireDrawingViewModel(
    private val scheduleRepository: WireDrawScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WireDrawingState())
    val uiState: StateFlow<WireDrawingState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<List<Double>>()
    private val redoStack = ArrayDeque<List<Double>>()

    init {
        val initialDies = WireDrawingCalculatorEngine.DEFAULT_EXAMPLE_DIES
        recalculateWithDies(initialDies, pushToUndo = false)

        viewModelScope.launch {
            scheduleRepository.getSchedules().collect { schedules ->
                _uiState.update { it.copy(savedSchedules = schedules) }
            }
        }
    }

    fun onNavSelected(nav: WireDrawingBottomNav) {
        _uiState.update { it.copy(activeNav = nav) }
    }

    fun onSelectChartType(type: AnalysisChartType) {
        _uiState.update { it.copy(analysisChartType = type) }
    }

    fun onInputTextChanged(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    fun onCalculateClick() {
        val raw = _uiState.value.inputText
        val (parsedDies, errors) = WireDrawingCalculatorEngine.parseInputText(raw)

        if (errors.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    validationErrors = errors,
                    statusMessage = "Validation error in die inputs."
                )
            }
            return
        }

        if (parsedDies.size < 2) {
            _uiState.update {
                it.copy(
                    dies = parsedDies,
                    passes = emptyList(),
                    validationErrors = listOf("Please enter at least 2 die diameters to calculate drawing passes."),
                    statusMessage = "Minimum 2 dies required."
                )
            }
            return
        }

        recalculateWithDies(parsedDies, pushToUndo = true)
        _uiState.update {
            it.copy(
                isEditDiesInputOpen = false,
                statusMessage = "Calculated ${parsedDies.size - 1} passes."
            )
        }
    }

    fun onPasteExample() {
        val example = WireDrawingCalculatorEngine.DEFAULT_EXAMPLE_DIES
        _uiState.update { it.copy(inputText = example.joinToString(", ")) }
        recalculateWithDies(example, pushToUndo = true)
    }

    fun onClearInput() {
        if (_uiState.value.dies.isNotEmpty()) {
            pushUndo(_uiState.value.dies)
        }
        _uiState.update {
            it.copy(
                inputText = "",
                dies = emptyList(),
                passes = emptyList(),
                validationErrors = emptyList(),
                selectedPassIndex = 0,
                selectedPassForDetail = null,
                editingPass = null,
                statusMessage = "Cleared die inputs."
            )
        }
    }

    fun openPassDetail(pass: PassResult) {
        val idx = _uiState.value.passes.indexOfFirst { it.passNumber == pass.passNumber }.coerceAtLeast(0)
        _uiState.update {
            it.copy(
                selectedPassForDetail = pass,
                selectedPassIndex = idx
            )
        }
    }

    fun closePassDetail() {
        _uiState.update { it.copy(selectedPassForDetail = null) }
    }

    fun openEditPass(pass: PassResult) {
        _uiState.update {
            it.copy(
                editingPass = pass,
                selectedPassForDetail = null
            )
        }
    }

    fun closeEditPass() {
        _uiState.update { it.copy(editingPass = null) }
    }

    fun applyEditPass(passNumber: Int, newToDie: Double) {
        // passNumber is 1-indexed. The "To" die is at index = passNumber in the dies array
        val current = _uiState.value.dies.toMutableList()
        if (passNumber in 1 until current.size && newToDie > 0.0) {
            val rounded = WireDrawingCalculatorEngine.roundTo3(newToDie)
            current[passNumber] = rounded
            recalculateWithDies(current, pushToUndo = true)
            _uiState.update {
                it.copy(
                    editingPass = null,
                    statusMessage = "Pass #$passNumber updated to ${String.format(java.util.Locale.US, "%.3f", rounded)} mm."
                )
            }
        }
    }

    fun openEditDiesInput() {
        _uiState.update { it.copy(isEditDiesInputOpen = true) }
    }

    fun closeEditDiesInput() {
        _uiState.update { it.copy(isEditDiesInputOpen = false) }
    }

    fun onSelectPass(index: Int) {
        val maxIndex = (_uiState.value.passes.size - 1).coerceAtLeast(0)
        val safeIndex = index.coerceIn(0, maxIndex)
        _uiState.update {
            it.copy(
                selectedPassIndex = safeIndex,
                selectedPassForDetail = it.passes.getOrNull(safeIndex)
            )
        }
    }

    fun onGenerateSeries(
        dStart: Double,
        dEnd: Double,
        targetElongation: Double,
        finalMin: Double?,
        finalMax: Double?
    ) {
        val series = WireDrawingCalculatorEngine.generateDieSeries(
            dStart = dStart,
            dEnd = dEnd,
            targetElongation = targetElongation,
            finalPassMin = finalMin,
            finalPassMax = finalMax
        )
        recalculateWithDies(series, pushToUndo = true)
        _uiState.update {
            it.copy(
                isSeriesGeneratorOpen = false,
                activeNav = WireDrawingBottomNav.CALCULATE,
                statusMessage = "Generated series with ${series.size - 1} passes."
            )
        }
    }

    fun onSuggesterTargetChange(target: Double) {
        val suggestions = WireDrawingCalculatorEngine.suggestIntermediateDies(
            _uiState.value.dies,
            target
        )
        _uiState.update {
            it.copy(
                suggesterTargetElongation = target,
                suggestedPasses = suggestions
            )
        }
    }

    fun onApplySuggestions() {
        val suggestions = _uiState.value.suggestedPasses
        if (suggestions.isEmpty()) return

        val newDies = WireDrawingCalculatorEngine.applySuggestedDies(
            _uiState.value.dies,
            suggestions
        )
        recalculateWithDies(newDies, pushToUndo = true)
        _uiState.update {
            it.copy(
                suggestedPasses = emptyList(),
                isSuggesterOpen = false,
                activeNav = WireDrawingBottomNav.CALCULATE,
                statusMessage = "Applied intermediate dies to schedule."
            )
        }
    }

    fun onTargetLimitsChange(min: Double, max: Double) {
        val checkResults = WireDrawingCalculatorEngine.checkTargets(_uiState.value.passes, min, max)
        _uiState.update {
            it.copy(
                targetMinElongation = min,
                targetMaxElongation = max,
                targetCheckResults = checkResults
            )
        }
    }

    fun onSaveSchedule(name: String) {
        if (name.isBlank() || _uiState.value.dies.isEmpty()) return
        viewModelScope.launch {
            scheduleRepository.saveSchedule(name.trim(), _uiState.value.dies)
            _uiState.update {
                it.copy(
                    isSaveDialogOpen = false,
                    statusMessage = "Schedule '$name' saved."
                )
            }
        }
    }

    fun onLoadSchedule(schedule: SavedSchedule) {
        recalculateWithDies(schedule.dies, pushToUndo = true)
        _uiState.update {
            it.copy(
                activeNav = WireDrawingBottomNav.CALCULATE,
                statusMessage = "Loaded schedule '${schedule.name}'."
            )
        }
    }

    fun onDeleteSchedule(id: Long) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(id)
            if (_uiState.value.comparedSchedule?.id == id) {
                _uiState.update {
                    it.copy(
                        comparedSchedule = null,
                        comparedPasses = emptyList(),
                        comparedStats = null
                    )
                }
            }
            _uiState.update { it.copy(statusMessage = "Schedule deleted.") }
        }
    }

    fun onCompareWithSchedule(schedule: SavedSchedule?) {
        if (schedule == null) {
            _uiState.update {
                it.copy(
                    comparedSchedule = null,
                    comparedPasses = emptyList(),
                    comparedStats = null,
                    isCompareDialogOpen = false
                )
            }
            return
        }

        val compPasses = WireDrawingCalculatorEngine.calculatePasses(schedule.dies)
        val compStats = WireDrawingCalculatorEngine.calculateStatistics(schedule.dies, compPasses)

        _uiState.update {
            it.copy(
                comparedSchedule = schedule,
                comparedPasses = compPasses,
                comparedStats = compStats,
                isCompareDialogOpen = false,
                statusMessage = "Comparing with '${schedule.name}'."
            )
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeLast()
            redoStack.addLast(_uiState.value.dies)
            recalculateWithDies(previous, pushToUndo = false)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeLast()
            undoStack.addLast(_uiState.value.dies)
            recalculateWithDies(next, pushToUndo = false)
        }
    }

    fun setSeriesGeneratorOpen(open: Boolean) {
        _uiState.update { it.copy(isSeriesGeneratorOpen = open) }
    }

    fun setTargetCheckerOpen(open: Boolean) {
        _uiState.update { it.copy(isTargetCheckerOpen = open) }
    }

    fun setSuggesterOpen(open: Boolean) {
        _uiState.update { it.copy(isSuggesterOpen = open) }
    }

    fun setSaveDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isSaveDialogOpen = open) }
    }

    fun setCompareDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isCompareDialogOpen = open) }
    }

    fun setConsistencyDetailOpen(open: Boolean) {
        _uiState.update { it.copy(isConsistencyDetailOpen = open) }
    }

    fun setCadDetailOpen(open: Boolean) {
        _uiState.update { it.copy(isCadDetailOpen = open) }
    }

    fun dismissStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    private fun recalculateWithDies(dies: List<Double>, pushToUndo: Boolean) {
        if (pushToUndo && _uiState.value.dies.isNotEmpty() && _uiState.value.dies != dies) {
            pushUndo(_uiState.value.dies)
        }

        val passes = WireDrawingCalculatorEngine.calculatePasses(dies)
        val stats = WireDrawingCalculatorEngine.calculateStatistics(dies, passes)
        val consistency = WireDrawingCalculatorEngine.calculateConsistency(passes)
        val targetChecks = WireDrawingCalculatorEngine.checkTargets(
            passes,
            _uiState.value.targetMinElongation,
            _uiState.value.targetMaxElongation
        )
        val suggestions = WireDrawingCalculatorEngine.suggestIntermediateDies(
            dies,
            _uiState.value.suggesterTargetElongation
        )

        val formattedText = dies.joinToString(", ") { String.format(java.util.Locale.US, "%.3f", it) }

        _uiState.update {
            it.copy(
                inputText = formattedText,
                dies = dies,
                passes = passes,
                stats = stats,
                consistency = consistency,
                targetCheckResults = targetChecks,
                suggestedPasses = suggestions,
                validationErrors = emptyList(),
                selectedPassIndex = it.selectedPassIndex.coerceIn(0, (passes.size - 1).coerceAtLeast(0)),
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    private fun pushUndo(state: List<Double>) {
        undoStack.addLast(state)
        if (undoStack.size > 50) {
            undoStack.removeFirst()
        }
        redoStack.clear()
        _uiState.update { it.copy(canUndo = true, canRedo = false) }
    }
}
