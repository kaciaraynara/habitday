package com.example.habitday.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitRecordDao {
    @Query("SELECT * FROM habit_records WHERE date = :date")
    fun getRecordsByDate(date: String): Flow<List<HabitRecordEntity>>

    @Query("SELECT * FROM habit_records WHERE habitId = :habitId")
    fun getRecordsForHabit(habitId: Long): Flow<List<HabitRecordEntity>>

    @Query("SELECT * FROM habit_records WHERE date BETWEEN :startDate AND :endDate")
    fun getRecordsInRange(startDate: String, endDate: String): Flow<List<HabitRecordEntity>>

    @Query("SELECT * FROM habit_records WHERE habitId = :habitId ORDER BY date DESC")
    suspend fun getAllRecordsForHabit(habitId: Long): List<HabitRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HabitRecordEntity)

    @Update
    suspend fun updateRecord(record: HabitRecordEntity)

    @Query("DELETE FROM habit_records WHERE habitId = :habitId AND date = :date")
    suspend fun deleteRecord(habitId: Long, date: String)

    @Query("DELETE FROM habit_records")
    suspend fun deleteAllRecords()
}
