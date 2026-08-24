package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_habit_logs")
data class DailyHabitLog(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val workoutCompleted: Boolean = false,
    val waterIntakeMl: Int = 0,
    val sleepHours: Float = 0f,
    val stepsCount: Int = 0,
    val mobilityCompleted: Boolean = false,
    val calisthenicsPracticeCompleted: Boolean = false,
    val moodRating: Int = 4, // 1-5 (1: Poor, 5: Peak)
    val energyRating: Int = 4, // 1-5
    val muscleSorenessRating: Int = 2, // 1-5 (1: Fresh, 5: Very sore)
    val trainingFatigueRating: Int = 2, // 1-5
    val stressRating: Int = 2, // 1-5
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class RecoveryAnalysis(
    val recoveryScorePercent: Int, // 0 - 100%
    val statusLevel: String, // "PEAK_RECOVERY", "OPTIMAL", "MODERATE_FATIGUE", "HIGH_FATIGUE_DELOAD"
    val recommendation: String,
    val sleepScorePercent: Int,
    val hydrationScorePercent: Int
)
