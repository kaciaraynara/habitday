package com.example.habitday

import com.example.habitday.data.local.HabitEntity
import com.example.habitday.data.repository.HabitRepository
import com.example.habitday.viewmodel.HabitViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: HabitRepository
    private lateinit var viewModel: HabitViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(HabitRepository::class.java)
        `when`(repository.getActiveHabits()).thenReturn(flowOf(emptyList()))
        viewModel = HabitViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun insertHabit_callsRepository() = runTest {
        viewModel.insertHabit("Beber Agua", "2L por dia", "Diario", "08:00", "Icon", "#4CAF50")
        testDispatcher.scheduler.advanceUntilIdle()
        verify(repository).insertHabit(any())
    }
}
