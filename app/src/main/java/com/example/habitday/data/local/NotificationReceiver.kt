package com.example.habitday.data.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habitday.HabitApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as HabitApplication
        val repository = app.repository
        
        when (intent.action) {
            "ACTION_REGISTER_WATER" -> {
                val habitId = intent.getLongExtra("HABIT_ID", -1L)
                if (habitId != -1L) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val date = LocalDate.now().toString()
                        val records = repository.getRecordsByDate(date).first()
                        val currentRecord = records.find { it.habitId == habitId }
                        
                        val newAmount = (currentRecord?.hydrationAmount ?: 0) + 1
                        val habit = repository.getHabitById(habitId)
                        val isCompleted = habit?.let { newAmount >= it.hydrationGoal } ?: false

                        repository.insertRecord(
                            currentRecord?.copy(hydrationAmount = newAmount, completed = isCompleted)
                                ?: HabitRecordEntity(habitId = habitId, date = date, hydrationAmount = newAmount, completed = isCompleted)
                        )
                    }
                }
            }
            "ACTION_COMPLETE_HABIT" -> {
                val habitId = intent.getLongExtra("HABIT_ID", -1L)
                if (habitId != -1L) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val date = LocalDate.now().toString()
                        repository.insertRecord(
                            HabitRecordEntity(habitId = habitId, date = date, completed = true)
                        )
                    }
                }
            }
            else -> {
                val habitName = intent.getStringExtra("HABIT_NAME") ?: "Hábito"
                val habitId = intent.getLongExtra("HABIT_ID", -1L)
                val isHydration = intent.getBooleanExtra("IS_HYDRATION", false)
                val helper = NotificationHelper(context)
                helper.showNotification(habitName, habitId, isHydration)
            }
        }
    }
}
