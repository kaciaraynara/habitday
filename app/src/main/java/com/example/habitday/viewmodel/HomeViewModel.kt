package com.example.habitday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habitday.data.local.HabitEntity
import com.example.habitday.data.local.HabitRecordEntity
import com.example.habitday.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DailyHabitItem(
    val habit: HabitEntity,
    val isCompleted: Boolean,
    val hydrationAmount: Int = 0,
    val streak: Int = 0
)

data class HomeUiState(
    val habits: List<DailyHabitItem> = emptyList(),
    val completionPercentage: Int = 0,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val dateText: String = ""
)

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getActiveHabits(),
        _currentDate.flatMapLatest { date -> repository.getRecordsByDate(date.toString()) },
        _currentDate
    ) { habits, records, date ->
        val recordMap = records.associateBy { it.habitId }
        val dailyItems = habits.map { habit ->
            val record = recordMap[habit.id]
            val completed = if (habit.isHydration) {
                (record?.hydrationAmount ?: 0) >= habit.hydrationGoal
            } else {
                record?.completed ?: false
            }
            DailyHabitItem(habit, completed, record?.hydrationAmount ?: 0)
        }
        val completedCount = dailyItems.count { it.isCompleted }
        val totalCount = dailyItems.size
        val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
        
        HomeUiState(
            habits = dailyItems,
            completionPercentage = percentage,
            completedCount = completedCount,
            totalCount = totalCount,
            dateText = formatDisplayDate(date)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun toggleHabitCompletion(habitId: Long, completed: Boolean) {
        viewModelScope.launch {
            val date = _currentDate.value.toString()
            if (completed) {
                repository.insertRecord(
                    HabitRecordEntity(habitId = habitId, date = date, completed = true)
                )
            } else {
                repository.deleteRecord(habitId, date)
            }
        }
    }

    fun updateHydration(habitId: Long, delta: Int) {
        viewModelScope.launch {
            val date = _currentDate.value.toString()
            val records = repository.getRecordsByDate(date).first()
            val currentRecord = records.find { it.habitId == habitId }
            
            val newAmount = ((currentRecord?.hydrationAmount ?: 0) + delta).coerceAtLeast(0)
            val habit = repository.getHabitById(habitId)
            val isCompleted = habit?.let { newAmount >= it.hydrationGoal } ?: false

            repository.insertRecord(
                currentRecord?.copy(hydrationAmount = newAmount, completed = isCompleted)
                    ?: HabitRecordEntity(habitId = habitId, date = date, hydrationAmount = newAmount, completed = isCompleted)
            )
        }
    }

    private fun formatDisplayDate(date: LocalDate): String {
        // Simple manual formatting to avoid Locale issues in mock environment if any
        val dayOfWeek = when (date.dayOfWeek.value) {
            1 -> "segunda-feira"
            2 -> "terça-feira"
            3 -> "quarta-feira"
            4 -> "quinta-feira"
            5 -> "sexta-feira"
            6 -> "sábado"
            7 -> "domingo"
            else -> ""
        }
        val month = when (date.monthValue) {
            1 -> "janeiro"
            2 -> "fevereiro"
            3 -> "março"
            4 -> "abril"
            5 -> "maio"
            6 -> "junho"
            7 -> "julho"
            8 -> "agosto"
            9 -> "setembro"
            10 -> "outubro"
            11 -> "novembro"
            12 -> "dezembro"
            else -> ""
        }
        return "$dayOfWeek, ${date.dayOfMonth} de $month"
    }
}

class HomeViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
