package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExerciseSetLog
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_plans")
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plans WHERE id = :id LIMIT 1")
    suspend fun getWorkoutPlanById(id: String): WorkoutPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlans(plans: List<WorkoutPlan>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlan)

    @Update
    suspend fun updateWorkoutPlan(plan: WorkoutPlan)

    @Query("DELETE FROM workout_plans")
    suspend fun clearWorkoutPlans()

    @Query("UPDATE workout_plans SET isCompletedToday = 0")
    suspend fun resetDailyCompletion()

    // Session Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionLog(log: WorkoutSessionLog)

    @Query("SELECT * FROM workout_session_logs ORDER BY dateEpoch DESC")
    fun getAllSessionLogs(): Flow<List<WorkoutSessionLog>>

    @Query("SELECT * FROM workout_session_logs ORDER BY dateEpoch DESC LIMIT :limit")
    fun getRecentSessionLogs(limit: Int): Flow<List<WorkoutSessionLog>>

    @Query("SELECT COUNT(*) FROM workout_session_logs")
    suspend fun getTotalCompletedSessionsCount(): Int

    // Set Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetLogs(sets: List<ExerciseSetLog>)

    @Query("SELECT * FROM exercise_set_logs WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getSetLogsForSession(sessionId: String): Flow<List<ExerciseSetLog>>
}
