package com.example.habitday.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val description: String = "",

    val frequency: String = "Todos os dias",

    val reminderTime: String? = null,

    val category: String = "Geral",

    val icon: String = "check",

    val color: String = "GREEN",

    val isHydration: Boolean = false,

    val hydrationGoal: Int = 8,

    val active: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)