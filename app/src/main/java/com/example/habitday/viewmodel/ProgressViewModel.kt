package com.example.habitday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habitday.data.remote.TodoDto
import com.example.habitday.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ApiState<out T> {
    object Idle : ApiState<Nothing>()
    object Loading : ApiState<Nothing>()
    data class Success<T>(val data: T) : ApiState<T>()
    data class Error(val message: String) : ApiState<Nothing>()
}

class ProgressViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _todosState = MutableStateFlow<ApiState<List<TodoDto>>>(ApiState.Idle)
    val todosState: StateFlow<ApiState<List<TodoDto>>> = _todosState.asStateFlow()

    private val _syncState = MutableStateFlow<ApiState<String>>(ApiState.Idle)
    val syncState: StateFlow<ApiState<String>> = _syncState.asStateFlow()

    fun fetchExternalData() {
        viewModelScope.launch {
            _todosState.value = ApiState.Loading
            try {
                val response = repository.getTodos()
                if (response.isSuccessful) {
                    _todosState.value = ApiState.Success(response.body()?.todos ?: emptyList())
                } else {
                    _todosState.value = ApiState.Error("Erro ao carregar dados externos")
                }
            } catch (e: Exception) {
                _todosState.value = ApiState.Error("Sem conexão com a internet")
            }
        }
    }

    fun syncHabit(habitName: String) {
        viewModelScope.launch {
            _syncState.value = ApiState.Loading
            try {
                val response = repository.addTodo("Sincronizando: $habitName")
                if (response.isSuccessful) {
                    _syncState.value = ApiState.Success("Hábito sincronizado com sucesso")
                } else {
                    _syncState.value = ApiState.Error("Erro na sincronização")
                }
            } catch (e: Exception) {
                _syncState.value = ApiState.Error("Falha na rede")
            }
        }
    }
    
    fun resetSyncState() {
        _syncState.value = ApiState.Idle
    }
}

class ProgressViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
