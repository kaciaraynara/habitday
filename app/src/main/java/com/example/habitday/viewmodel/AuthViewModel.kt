package com.example.habitday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habitday.data.local.UserEntity
import com.example.habitday.data.local.UserPreferencesEntity
import com.example.habitday.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userName: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.getUserByEmail(email)
            if (user != null && user.password == password) {
                updateSession(user.name)
                _authState.value = AuthState.Success(user.name)
            } else {
                _authState.value = AuthState.Error("Email ou senha inválidos")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                _authState.value = AuthState.Error("Email já cadastrado")
            } else {
                repository.registerUser(UserEntity(name = name, email = email, password = password))
                updateSession(name)
                _authState.value = AuthState.Success(name)
            }
        }
    }

    private suspend fun updateSession(name: String) {
        repository.updatePreferences(
            UserPreferencesEntity(isLoggedIn = true, userName = name)
        )
    }

    fun logout() {
        viewModelScope.launch {
            repository.updatePreferences(
                UserPreferencesEntity(isLoggedIn = false, userName = null)
            )
            _authState.value = AuthState.Idle
        }
    }
}

class AuthViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
