package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val splitType: String, // PUSH_PULL_LEGS, UPPER_LOWER, FULL_BODY, CALISTHENICS_SKILL, BRO_SPLIT
    val dayOfWeekName: String, // Day 1, Monday, etc.
    val targetMuscleGroups: String, // CSV
    val estimatedDurationMin: Int = 45,
    val difficulty: String = "INTERMEDIATE",
    val exercisesJson: String, // List of WorkoutExerciseDef serialized
    val warmupGuidance: String,
    val cooldownGuidance: String,
    val isCustom: Boolean = false,
    val isCompletedToday: Boolean = false
)

data class WorkoutExerciseItem(
    val exerciseId: String,
    val exerciseName: String,
    val targetMuscles: String,
    val sets: Int,
    val repsTarget: String,
    val restSec: Int,
    val tempo: String,
    val rpeTarget: String,
    val weightKg: Float = 0f,
    val notes: String = "",
    val equipmentRequired: String = "NONE"
)

@Entity(tableName = "workout_session_logs")
data class WorkoutSessionLog(
    @PrimaryKey val id: String,
    val planId: String,
    val planTitle: String,
    val dateEpoch: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val totalVolumeKg: Float = 0f,
    val totalSetsCompleted: Int = 0,
    val totalRepsCompleted: Int = 0,
    val perceivedExertionRpe: Int = 8,
    val performanceFeedback: String = "SOLID", // EASY, PERFECT, STRUGGLED
    val notes: String = ""
)

@Entity(tableName = "exercise_set_logs")
data class ExerciseSetLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val exerciseId: String,
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Float,
    val repsCompleted: Int,
    val rpe: Float,
    val isCompleted: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
