package com.example.habitday

import android.app.Application
import com.example.habitday.data.local.HabitDatabase
import com.example.habitday.data.local.NotificationHelper
import com.example.habitday.data.local.SensorHelper
import com.example.habitday.data.remote.RetrofitClient
import com.example.habitday.data.repository.HabitRepository

class HabitApplication : Application() {
    val database by lazy { HabitDatabase.getDatabase(this) }
    val notificationHelper by lazy { NotificationHelper(this) }
    val sensorHelper by lazy { SensorHelper(this) }
    val repository by lazy { 
        HabitRepository(
            database.habitDao(), 
            database.habitRecordDao(), 
            database.preferencesDao(),
            database.userDao(),
            RetrofitClient.habitApi
        ) 
    }
}
