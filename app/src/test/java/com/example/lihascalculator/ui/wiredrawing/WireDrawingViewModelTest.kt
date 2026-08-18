package com.example.lihascalculator.ui.wiredrawing

import com.example.lihascalculator.domain.model.wiredrawing.SavedSchedule
import com.example.lihascalculator.domain.repository.WireDrawScheduleRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WireDrawingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeWireDrawScheduleRepository
    private lateinit var viewModel: WireDrawingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWireDrawScheduleRepository()
        viewModel = WireDrawingViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateHasDefaultExampleDies() {
        val state = viewModel.uiState.value
        assertTrue(state.dies.size > 2)
        assertTrue(state.passes.isNotEmpty())
        assertTrue(state.isValidSchedule)
        assertNotNull(state.stats)
        assertFalse(state.canUndo)
        assertFalse(state.canRedo)
    }

    @Test
    fun testCalculationWithValidInput() {
        viewModel.onInputTextChanged("2.500, 2.000, 1.600")
        viewModel.onCalculateClick()

        val state = viewModel.uiState.value
        assertEquals(3, state.dies.size)
        assertEquals(2, state.passes.size)
        assertEquals(2.500, state.passes[0].fromDie, 0.001)
        assertEquals(2.000, state.passes[0].toDie, 0.001)
        assertEquals(1.600, state.passes[1].toDie, 0.001)
        assertTrue(state.validationErrors.isEmpty())
        assertTrue(state.canUndo)
    }

    @Test
    fun testCalculationWithInvalidNumericInput() {
        viewModel.onInputTextChanged("2.500, abc, -1.0")
        viewModel.onCalculateClick()

        val state = viewModel.uiState.value
        assertTrue(state.validationErrors.isNotEmpty())
    }

    @Test
    fun testCalculationWithLessThanTwoDies() {
        viewModel.onInputTextChanged("2.500")
        viewModel.onCalculateClick()

        val state = viewModel.uiState.value
        assertTrue(state.validationErrors.isNotEmpty())
        assertFalse(state.isValidSchedule)
    }

    @Test
    fun testUndoRedoStack() {
        viewModel.onInputTextChanged("2.500, 2.000, 1.500")
        viewModel.onCalculateClick()
        val firstState = viewModel.uiState.value.dies

        viewModel.onInputTextChanged("3.000, 2.500, 2.000, 1.500")
        viewModel.onCalculateClick()
        val secondState = viewModel.uiState.value.dies

        assertTrue(viewModel.uiState.value.canUndo)

        // Undo
        viewModel.undo()
        assertEquals(firstState, viewModel.uiState.value.dies)
        assertTrue(viewModel.uiState.value.canRedo)

        // Redo
        viewModel.redo()
        assertEquals(secondState, viewModel.uiState.value.dies)
    }

    @Test
    fun testClearInput() {
        viewModel.onClearInput()
        val state = viewModel.uiState.value
        assertTrue(state.dies.isEmpty())
        assertTrue(state.passes.isEmpty())
        assertFalse(state.isValidSchedule)
        assertTrue(state.canUndo) // Can undo clear
    }

    @Test
    fun testGenerateSeries() {
        viewModel.onGenerateSeries(
            dStart = 3.000,
            dEnd = 1.000,
            targetElongation = 20.0,
            finalMin = null,
            finalMax = null
        )

        val state = viewModel.uiState.value
        assertTrue(state.dies.size > 2)
        assertEquals(3.000, state.dies.first(), 0.001)
        assertEquals(1.000, state.dies.last(), 0.001)
        assertEquals(WireDrawingBottomNav.CALCULATE, state.activeNav)
    }

    @Test
    fun testSaveAndLoadSchedule() = runTest {
        viewModel.onInputTextChanged("2.400, 2.000, 1.700")
        viewModel.onCalculateClick()

        viewModel.onSaveSchedule("Test Schedule")
        advanceUntilIdle()

        assertEquals(1, fakeRepository.schedulesList.size)
        assertEquals("Test Schedule", fakeRepository.schedulesList[0].name)
        assertEquals(listOf(2.400, 2.000, 1.700), fakeRepository.schedulesList[0].dies)

        // Modify current and load saved
        viewModel.onInputTextChanged("1.000, 0.800")
        viewModel.onCalculateClick()

        viewModel.onLoadSchedule(fakeRepository.schedulesList[0])
        assertEquals(listOf(2.400, 2.000, 1.700), viewModel.uiState.value.dies)
    }

    @Test
    fun testDeleteSchedule() = runTest {
        viewModel.onSaveSchedule("Schedule to delete")
        advanceUntilIdle()

        val savedId = fakeRepository.schedulesList.first().id
        viewModel.onDeleteSchedule(savedId)
        advanceUntilIdle()

        assertTrue(fakeRepository.schedulesList.isEmpty())
    }

    @Test
    fun testCompareWithSchedule() {
        val scheduleToCompare = SavedSchedule(
            id = 10,
            name = "Baseline Copper",
            dies = listOf(2.500, 2.000, 1.600, 1.300)
        )

        viewModel.onCompareWithSchedule(scheduleToCompare)
        val state = viewModel.uiState.value

        assertNotNull(state.comparedSchedule)
        assertEquals(3, state.comparedPasses.size)
        assertNotNull(state.comparedStats)

        // Clear compare
        viewModel.onCompareWithSchedule(null)
        assertNull(viewModel.uiState.value.comparedSchedule)
    }

    @Test
    fun testApplyEditPass() {
        viewModel.onInputTextChanged("2.500, 2.000, 1.500")
        viewModel.onCalculateClick()

        // Edit pass #1 to die from 2.000 to 2.100
        viewModel.applyEditPass(1, 2.100)

        val state = viewModel.uiState.value
        assertEquals(2.100, state.dies[1], 0.001)
        assertEquals(2.100, state.passes[0].toDie, 0.001)
        assertEquals(2.100, state.passes[1].fromDie, 0.001)
    }

    @Test
    fun testTargetCheckerLimits() {
        viewModel.onInputTextChanged("2.500, 2.000, 1.500")
        viewModel.onCalculateClick()

        viewModel.onTargetLimitsChange(10.0, 30.0)
        val state = viewModel.uiState.value
        assertEquals(10.0, state.targetMinElongation, 0.001)
        assertEquals(30.0, state.targetMaxElongation, 0.001)
        assertEquals(2, state.targetCheckResults.size)
    }
}

private class FakeWireDrawScheduleRepository : WireDrawScheduleRepository {
    val schedulesList = mutableListOf<SavedSchedule>()
    private val flow = MutableStateFlow<List<SavedSchedule>>(emptyList())

    override fun getSchedules(): Flow<List<SavedSchedule>> = flow.asStateFlow()

    override suspend fun saveSchedule(name: String, dies: List<Double>): Long {
        val schedule = SavedSchedule(
            id = schedulesList.size.toLong() + 1,
            name = name,
            dies = dies
        )
        schedulesList.add(schedule)
        flow.value = schedulesList.toList()
        return schedule.id
    }

    override suspend fun deleteSchedule(id: Long) {
        schedulesList.removeAll { it.id == id }
        flow.value = schedulesList.toList()
    }

    override suspend fun clearAllSchedules() {
        schedulesList.clear()
        flow.value = emptyList()
    }
}
