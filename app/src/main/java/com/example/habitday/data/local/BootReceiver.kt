package com.example.habitday.data.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habitday.HabitApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as HabitApplication
            val repository = app.repository
            val notificationHelper = app.notificationHelper
            
            CoroutineScope(Dispatchers.IO).launch {
                val habits = repository.getActiveHabits().first()
                habits.forEach { habit ->
                    habit.reminderTime?.let { time ->
                        notificationHelper.scheduleNotification(
                            habitId = habit.id,
                            habitName = habit.name,
                            time = time,
                            isHydration = habit.isHydration
                        )
                    }
                }
            }
        }
    }
}
