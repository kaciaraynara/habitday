package com.example.habitday.data.repository

import com.example.habitday.data.local.HabitDao
import com.example.habitday.data.local.HabitEntity
import com.example.habitday.data.local.HabitRecordDao
import com.example.habitday.data.local.HabitRecordEntity
import com.example.habitday.data.local.PreferencesDao
import com.example.habitday.data.local.UserDao
import com.example.habitday.data.local.UserEntity
import com.example.habitday.data.local.UserPreferencesEntity
import com.example.habitday.data.remote.AddTodoRequest
import com.example.habitday.data.remote.HabitApi
import com.example.habitday.data.remote.TodoDto
import com.example.habitday.data.remote.TodoListResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitRecordDao: HabitRecordDao,
    private val preferencesDao: PreferencesDao,
    private val userDao: UserDao,
    private val habitApi: HabitApi
) {
    // Room - Users
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun registerUser(user: UserEntity) = userDao.registerUser(user)

    // Room - Habits
    fun getActiveHabits(): Flow<List<HabitEntity>> = habitDao.getActiveHabits()
    suspend fun getHabitById(id: Long): HabitEntity? = habitDao.getHabitById(id)
    suspend fun insertHabit(habit: HabitEntity) = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = habitDao.deleteHabit(habit)

    // Room - Records
    fun getRecordsByDate(date: String): Flow<List<HabitRecordEntity>> = habitRecordDao.getRecordsByDate(date)
    fun getRecordsForHabit(habitId: Long): Flow<List<HabitRecordEntity>> = habitRecordDao.getRecordsForHabit(habitId)
    suspend fun insertRecord(record: HabitRecordEntity) = habitRecordDao.insertRecord(record)
    suspend fun updateRecord(record: HabitRecordEntity) = habitRecordDao.updateRecord(record)
    suspend fun deleteRecord(habitId: Long, date: String) = habitRecordDao.deleteRecord(habitId, date)
    suspend fun deleteAllData() {
        habitRecordDao.deleteAllRecords()
        habitDao.deleteAllHabits()
    }

    suspend fun calculateStreak(habitId: Long): Int {
        val records = habitRecordDao.getAllRecordsForHabit(habitId)
        if (records.isEmpty()) return 0
        
        var streak = 0
        var currentDate = java.time.LocalDate.now()
        
        // If not completed today, check if it was completed yesterday to continue streak
        val lastRecordDate = java.time.LocalDate.parse(records[0].date)
        if (lastRecordDate != currentDate && lastRecordDate != currentDate.minusDays(1)) {
            return 0
        }

        for (record in records) {
            val recordDate = java.time.LocalDate.parse(record.date)
            if (recordDate == currentDate) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else if (recordDate.isBefore(currentDate)) {
                break // Gap found
            }
        }
        return streak
    }

    // Room - Preferences
    fun getPreferences(): Flow<UserPreferencesEntity?> = preferencesDao.getPreferences()
    suspend fun updatePreferences(preferences: UserPreferencesEntity) = preferencesDao.updatePreferences(preferences)

    // Retrofit
    suspend fun getTodos(): Response<TodoListResponse> = habitApi.getTodos()
    suspend fun addTodo(todo: String): Response<TodoDto> {
        val request = AddTodoRequest(todo = todo, completed = false, userId = 1)
        return habitApi.addTodo(request)
    }
}
