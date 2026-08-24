package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val weightKg: Float,
    val waistCm: Float? = null,
    val chestCm: Float? = null,
    val armCm: Float? = null,
    val legCm: Float? = null,
    val bodyFatEstimate: Float? = null,
    val photoUri: String? = null,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "performance_prs")
data class PerformancePR(
    @PrimaryKey val exerciseName: String, // e.g. "Barbell Bench Press", "Max Push-ups", "Pull-ups", "Deadlift"
    val recordValue: Float, // weight in kg or rep count or hold seconds
    val unit: String, // "kg", "reps", "sec"
    val category: String, // "STRENGTH", "CALISTHENICS", "ENDURANCE"
    val achievedDateString: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weekly_reports")
data class WeeklyReport(
    @PrimaryKey val weekIdentifier: String, // e.g. "2026-W34"
    val startDateString: String,
    val endDateString: String,
    val workoutsCompleted: Int,
    val totalVolumeKg: Float,
    val calisthenicsProgressions: Int,
    val avgCalorieIntake: Int,
    val avgProteinGrams: Int,
    val avgSleepHours: Float,
    val weightChangeKg: Float,
    val recoveryAdherencePercent: Int,
    val mainAchievement: String,
    val biggestLimiter: String,
    val nextWeekPriorities: String,
    val generatedDateEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null,
    val category: String = "SYSTEM" // WORKOUT, STREAK, CALISTHENICS, NUTRITION
)
