package com.example.habitday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habitday.data.local.HabitEntity
import com.example.habitday.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val allHabits: StateFlow<List<HabitEntity>> = combine(
        repository.getActiveHabits(),
        _searchQuery
    ) { habits, query ->
        if (query.isBlank()) habits
        else habits.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    suspend fun insertHabit(
        name: String, 
        description: String, 
        frequency: String, 
        reminderTime: String?, 
        category: String, 
        icon: String, 
        color: String,
        isHydration: Boolean = false,
        hydrationGoal: Int = 8
    ): Long {
        val habit = HabitEntity(
            name = name,
            description = description,
            frequency = frequency,
            reminderTime = reminderTime,
            category = category,
            icon = icon,
            color = color,
            isHydration = isHydration,
            hydrationGoal = hydrationGoal
        )
        return repository.insertHabit(habit)
    }

    fun updateHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun duplicateHabit(habit: HabitEntity) {
        viewModelScope.launch {
            val duplicate = habit.copy(id = 0, name = "${habit.name} (Cópia)")
            repository.insertHabit(duplicate)
        }
    }

    suspend fun getHabitById(id: Long): HabitEntity? {
        return repository.getHabitById(id)
    }
}

class HabitViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
