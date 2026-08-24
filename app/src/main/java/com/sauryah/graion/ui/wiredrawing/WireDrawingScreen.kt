package com.sauryah.graion.ui.wiredrawing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.graion.theme.LocalCalculatorColors
import com.sauryah.graion.ui.wiredrawing.components.ConsistencyDetailDialog
import com.sauryah.graion.ui.wiredrawing.components.DieSeriesGeneratorDialog
import com.sauryah.graion.ui.wiredrawing.components.DieSuggesterDialog
import com.sauryah.graion.ui.wiredrawing.components.EditDieDialog
import com.sauryah.graion.ui.wiredrawing.components.EditDiesInputDialog
import com.sauryah.graion.ui.wiredrawing.components.MachineKinematicsDialog
import com.sauryah.graion.ui.wiredrawing.components.PassDetailBottomSheet
import com.sauryah.graion.ui.wiredrawing.components.SaveScheduleDialog
import com.sauryah.graion.ui.wiredrawing.components.ScheduleCompareDialog
import com.sauryah.graion.ui.wiredrawing.components.TargetCheckerDialog
import com.sauryah.graion.ui.wiredrawing.components.WireWeightDialog
import com.sauryah.graion.ui.wiredrawing.components.analysisTabContent
import com.sauryah.graion.ui.wiredrawing.components.calculateTabContent
import com.sauryah.graion.ui.wiredrawing.components.optimizeTabContent
import com.sauryah.graion.ui.wiredrawing.components.savedTabContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WireDrawingScreen(
    state: WireDrawingState,
    viewModel: WireDrawingViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.statusMessage) {
        state.statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissStatusMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wire Drawing",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Tools",
                            tint = colors.textPrimary
                        )
                    }
                },
                actions = {
                    // Undo Action
                    IconButton(
                        onClick = viewModel::undo,
                        enabled = state.canUndo
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (state.canUndo) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Redo Action
                    IconButton(
                        onClick = viewModel::redo,
                        enabled = state.canRedo
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (state.canRedo) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Save Schedule Quick Action
                    IconButton(
                        onClick = { viewModel.setSaveDialogOpen(true) },
                        enabled = state.isValidSchedule
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save Schedule",
                            tint = if (state.isValidSchedule) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.textPrimary
                )
            )
        },
        bottomBar = {
            // 4-Tab Bottom Navigation Bar (CALCULATE, ANALYSIS, OPTIMIZE, SAVED)
            NavigationBar(
                containerColor = colors.surface,
                contentColor = colors.textPrimary,
                tonalElevation = 4.dp
            ) {
                val navItems = listOf(
                    Triple(WireDrawingBottomNav.CALCULATE, "Calculate", Icons.Default.Calculate),
                    Triple(WireDrawingBottomNav.ANALYSIS, "Analysis", Icons.Default.Analytics),
                    Triple(WireDrawingBottomNav.OPTIMIZE, "Optimize", Icons.Default.Tune),
                    Triple(WireDrawingBottomNav.SAVED, "Saved", Icons.Default.Bookmark)
                )

                navItems.forEach { (nav, label, icon) ->
                    val isSelected = state.activeNav == nav
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.onNavSelected(nav) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.accentPrimary,
                            selectedTextColor = colors.accentPrimary,
                            indicatorColor = colors.accentPrimary.copy(alpha = 0.15f),
                            unselectedIconColor = colors.textSecondary,
                            unselectedTextColor = colors.textSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (state.activeNav) {
                WireDrawingBottomNav.CALCULATE -> {
                    calculateTabContent(
                        state = state,
                        colors = colors,
                        onOpenEditDies = viewModel::openEditDiesInput,
                        onCalculate = viewModel::onCalculateClick,
                        onPassClick = viewModel::openPassDetail,
                        onEditPassClick = viewModel::openEditPass
                    )
                }

                WireDrawingBottomNav.ANALYSIS -> {
                    analysisTabContent(
                        state = state,
                        colors = colors,
                        onSelectChartType = viewModel::onSelectChartType,
                        onSelectPass = viewModel::onSelectPass,
                        onOpenConsistencyDetail = { viewModel.setConsistencyDetailOpen(true) }
                    )
                }

                WireDrawingBottomNav.OPTIMIZE -> {
                    optimizeTabContent(
                        state = state,
                        colors = colors,
                        onOpenGenerateSeries = { viewModel.setSeriesGeneratorOpen(true) },
                        onOpenTargetCheck = { viewModel.setTargetCheckerOpen(true) },
                        onOpenSuggestDies = { viewModel.setSuggesterOpen(true) },
                        onOpenWireWeight = { viewModel.setWireWeightDialogOpen(true) },
                        onOpenKinematics = { viewModel.setKinematicsDialogOpen(true) }
                    )
                }

                WireDrawingBottomNav.SAVED -> {
                    savedTabContent(
                        state = state,
                        colors = colors,
                        context = context,
                        onOpenSaveDialog = { viewModel.setSaveDialogOpen(true) },
                        onOpenCompareDialog = { viewModel.setCompareDialogOpen(true) },
                        onLoadSchedule = viewModel::onLoadSchedule,
                        onDeleteSchedule = viewModel::onDeleteSchedule,
                        onExportCsv = viewModel::exportCsv,
                        onExportPdf = viewModel::exportPdf,
                        onExportCad = viewModel::exportCadSpec
                    )
                }
            }
        }
    }

    // Modal Dialogs & Sheets (Progressive Disclosure)

    // 1. Pass Detail Modal Bottom Sheet
    PassDetailBottomSheet(
        pass = state.selectedPassForDetail,
        colors = colors,
        onDismiss = viewModel::closePassDetail,
        onEditDieClick = viewModel::openEditPass,
        onViewCadClick = {
            viewModel.closePassDetail()
            viewModel.setCadDetailOpen(true)
        }
    )

    // 2. Edit Single Pass Die Dialog
    EditDieDialog(
        pass = state.editingPass,
        colors = colors,
        onDismiss = viewModel::closeEditPass,
        onApply = viewModel::applyEditPass
    )

    // 3. Edit All Dies Input Modal
    EditDiesInputDialog(
        isOpen = state.isEditDiesInputOpen,
        state = state,
        colors = colors,
        onDismiss = viewModel::closeEditDiesInput,
        onInputChanged = viewModel::onInputTextChanged,
        onCalculate = viewModel::onCalculateClick,
        onPasteExample = viewModel::onPasteExample,
        onClear = viewModel::onClearInput
    )

    // 4. Generate Series Dialog
    DieSeriesGeneratorDialog(
        isOpen = state.isSeriesGeneratorOpen,
        colors = colors,
        onDismiss = { viewModel.setSeriesGeneratorOpen(false) },
        onGenerate = viewModel::onGenerateSeries
    )

    // 5. Target Checker Dialog
    TargetCheckerDialog(
        isOpen = state.isTargetCheckerOpen,
        targetChecks = state.targetCheckResults,
        minTarget = state.targetMinElongation,
        maxTarget = state.targetMaxElongation,
        colors = colors,
        onDismiss = { viewModel.setTargetCheckerOpen(false) },
        onLimitsChange = viewModel::onTargetLimitsChange
    )

    // 6. Die Suggester Dialog
    DieSuggesterDialog(
        isOpen = state.isSuggesterOpen,
        suggestions = state.suggestedPasses,
        targetElongation = state.suggesterTargetElongation,
        colors = colors,
        onDismiss = { viewModel.setSuggesterOpen(false) },
        onTargetChange = viewModel::onSuggesterTargetChange,
        onApplySuggestions = viewModel::onApplySuggestions
    )

    // 7. Save Schedule Dialog
    SaveScheduleDialog(
        isOpen = state.isSaveDialogOpen,
        currentDies = state.dies,
        colors = colors,
        onDismiss = { viewModel.setSaveDialogOpen(false) },
        onSave = viewModel::onSaveSchedule
    )

    // 8. Schedule Compare Dialog
    ScheduleCompareDialog(
        isOpen = state.isCompareDialogOpen,
        currentStats = state.stats,
        currentPasses = state.passes,
        savedSchedules = state.savedSchedules,
        comparedSchedule = state.comparedSchedule,
        comparedStats = state.comparedStats,
        comparedPasses = state.comparedPasses,
        colors = colors,
        onDismiss = { viewModel.setCompareDialogOpen(false) },
        onSelectScheduleToCompare = viewModel::onCompareWithSchedule
    )

    // 9. Consistency Detail Dialog
    ConsistencyDetailDialog(
        isOpen = state.isConsistencyDetailOpen,
        consistency = state.consistency,
        colors = colors,
        onDismiss = { viewModel.setConsistencyDetailOpen(false) }
    )

    // 10. Wire Weight & Length Calculator Dialog
    WireWeightDialog(
        isOpen = state.isWireWeightDialogOpen,
        initialDiameterMm = state.dies.firstOrNull() ?: 2.0,
        colors = colors,
        onDismiss = { viewModel.setWireWeightDialogOpen(false) }
    )

    // 11. Machine Kinematics & Line Speed Dialog
    MachineKinematicsDialog(
        isOpen = state.isKinematicsDialogOpen,
        passes = state.passes,
        colors = colors,
        onDismiss = { viewModel.setKinematicsDialogOpen(false) }
    )
}
