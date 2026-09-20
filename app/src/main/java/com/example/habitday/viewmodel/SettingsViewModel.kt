package com.example.habitday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habitday.data.local.UserPreferencesEntity
import com.example.habitday.data.repository.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: HabitRepository) : ViewModel() {

    val preferences: StateFlow<UserPreferencesEntity?> = repository.getPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateTheme(mode: String) {
        viewModelScope.launch {
            val current = preferences.value ?: UserPreferencesEntity()
            repository.updatePreferences(current.copy(themeMode = mode))
        }
    }

    fun updateHighlightColor(color: String) {
        viewModelScope.launch {
            val current = preferences.value ?: UserPreferencesEntity()
            repository.updatePreferences(current.copy(highlightColor = color))
        }
    }

    fun updateBackgroundColor(color: String) {
        viewModelScope.launch {
            val current = preferences.value ?: UserPreferencesEntity()
            repository.updatePreferences(current.copy(backgroundColor = color))
        }
    }

    fun updateAvatarStyle(style: String) {
        viewModelScope.launch {
            val current = preferences.value ?: UserPreferencesEntity()
            repository.updatePreferences(current.copy(avatarStyle = style))
        }
    }
    
    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val current = preferences.value ?: UserPreferencesEntity()
            repository.updatePreferences(current.copy(notificationsEnabled = enabled))
        }
    }

    fun updateProfileImage(uri: String?) {
        viewModelScope.launch {
            val current = preferences.value ?: UserPreferencesEntity()
            repository.updatePreferences(current.copy(profileImageUri = uri))
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val current = preferences.value ?: UserPreferencesEntity()
            repository.updatePreferences(current.copy(isOnboardingCompleted = true))
        }
    }
}

class SettingsViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
