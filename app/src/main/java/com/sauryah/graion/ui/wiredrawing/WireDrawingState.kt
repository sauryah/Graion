package com.sauryah.graion.ui.wiredrawing

import androidx.compose.runtime.Immutable
import com.sauryah.graion.domain.engine.wiredrawing.WireDrawingCalculatorEngine
import com.sauryah.graion.domain.model.wiredrawing.ConsistencyResult
import com.sauryah.graion.domain.model.wiredrawing.PassResult
import com.sauryah.graion.domain.model.wiredrawing.SavedSchedule
import com.sauryah.graion.domain.model.wiredrawing.SuggestedIntermediatePass
import com.sauryah.graion.domain.model.wiredrawing.TargetCheckResult
import com.sauryah.graion.domain.model.wiredrawing.WireDrawingStats

enum class WireDrawingBottomNav(val title: String) {
    CALCULATE("Calculate"),
    ANALYSIS("Analysis"),
    OPTIMIZE("Optimize"),
    SAVED("Saved")
}

enum class AnalysisChartType(val title: String) {
    ELONGATION("Elongation %"),
    AREA_REDUCTION("Reduction %"),
    DIE_DIAMETER("Die Sizes")
}

@Immutable
data class WireDrawingState(
    val inputText: String = WireDrawingCalculatorEngine.DEFAULT_EXAMPLE_DIES.joinToString(", "),
    val dies: List<Double> = WireDrawingCalculatorEngine.DEFAULT_EXAMPLE_DIES,
    val passes: List<PassResult> = emptyList(),
    val stats: WireDrawingStats = WireDrawingCalculatorEngine.calculateStatistics(emptyList(), emptyList()),
    val consistency: ConsistencyResult = WireDrawingCalculatorEngine.calculateConsistency(emptyList()),
    val selectedPassIndex: Int = 0,
    val selectedPassForDetail: PassResult? = null,
    val editingPass: PassResult? = null,
    val validationErrors: List<String> = emptyList(),
    val targetMinElongation: Double = 15.0,
    val targetMaxElongation: Double = 22.0,
    val targetCheckResults: List<TargetCheckResult> = emptyList(),
    val suggesterTargetElongation: Double = 20.0,
    val suggestedPasses: List<SuggestedIntermediatePass> = emptyList(),
    val savedSchedules: List<SavedSchedule> = emptyList(),
    val comparedSchedule: SavedSchedule? = null,
    val comparedPasses: List<PassResult> = emptyList(),
    val comparedStats: WireDrawingStats? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val activeNav: WireDrawingBottomNav = WireDrawingBottomNav.CALCULATE,
    val analysisChartType: AnalysisChartType = AnalysisChartType.ELONGATION,
    val isEditDiesInputOpen: Boolean = false,
    val isSeriesGeneratorOpen: Boolean = false,
    val isTargetCheckerOpen: Boolean = false,
    val isSuggesterOpen: Boolean = false,
    val isSaveDialogOpen: Boolean = false,
    val isCompareDialogOpen: Boolean = false,
    val isConsistencyDetailOpen: Boolean = false,
    val isCadDetailOpen: Boolean = false,
    val statusMessage: String? = null
) {
    val isValidSchedule: Boolean
        get() = dies.size >= 2 && passes.isNotEmpty()

    val selectedPass: PassResult?
        get() = passes.getOrNull(selectedPassIndex)

    val dieSequencePreview: String
        get() = if (dies.size <= 5) {
            dies.joinToString(" → ") { String.format(java.util.Locale.US, "%.3f", it) }
        } else {
            "${dies.take(3).joinToString(" → ") { String.format(java.util.Locale.US, "%.3f", it) }} → ... → ${String.format(java.util.Locale.US, "%.3f", dies.last())}"
        }
}
