package com.example.habitday.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "SISTEMA", // CLARO, ESCURO, SISTEMA
    val highlightColor: String = "#1B5E20",
    val backgroundColor: String = "#FFFFFF",
    val avatarStyle: String = "CLASSICO", // CLASSICO, CALMO, ENERGETICO, FOCADO, MINIMALISTA
    val notificationsEnabled: Boolean = true,
    val showCompletedHabits: Boolean = true,
    val isOnboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userName: String? = null,
    val profileImageUri: String? = null
)
