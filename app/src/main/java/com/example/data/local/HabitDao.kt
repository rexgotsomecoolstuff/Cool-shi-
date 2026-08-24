package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyHabitLog
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM daily_habit_logs WHERE dateString = :dateString LIMIT 1")
    fun getHabitLogForDate(dateString: String): Flow<DailyHabitLog?>

    @Query("SELECT * FROM daily_habit_logs WHERE dateString = :dateString LIMIT 1")
    suspend fun getHabitLogForDateOnce(dateString: String): DailyHabitLog?

    @Query("SELECT * FROM daily_habit_logs ORDER BY dateString DESC LIMIT :limit")
    fun getRecentHabitLogs(limit: Int): Flow<List<DailyHabitLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHabitLog(log: DailyHabitLog)

    @Query("UPDATE daily_habit_logs SET workoutCompleted = :completed WHERE dateString = :dateString")
    suspend fun updateWorkoutCompleted(dateString: String, completed: Boolean)

    @Query("UPDATE daily_habit_logs SET waterIntakeMl = :waterMl WHERE dateString = :dateString")
    suspend fun updateWaterIntake(dateString: String, waterMl: Int)

    @Query("UPDATE daily_habit_logs SET sleepHours = :hours WHERE dateString = :dateString")
    suspend fun updateSleepHours(dateString: String, hours: Float)

    @Query("UPDATE daily_habit_logs SET mobilityCompleted = :completed WHERE dateString = :dateString")
    suspend fun updateMobility(dateString: String, completed: Boolean)
}
