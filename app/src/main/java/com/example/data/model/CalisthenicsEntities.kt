package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calisthenics_skills")
data class CalisthenicsSkill(
    @PrimaryKey val id: String,
    val skillTreeName: String, // e.g. "Push-up Progression", "Handstand", "Planche", "Muscle-up"
    val currentLevel: Int = 1,
    val maxLevel: Int = 5,
    val currentExerciseName: String,
    val targetRepsOrHold: String, // "3x15 clean reps" or "20s hold"
    val difficulty: String, // BEGINNER, NOVICE, INTERMEDIATE, ADVANCED, ELITE
    val prerequisites: String, // CSV or descriptive text
    val progressionPath: String, // Steps separated by '>'
    val safetyNotes: String,
    val techniqueCues: String,
    val isMastered: Boolean = false,
    val bestRecord: String = "0",
    val lastTestedDate: Long = 0L
)

@Entity(tableName = "calisthenics_test_logs")
data class CalisthenicsTestLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val skillId: String,
    val skillName: String,
    val levelTested: Int,
    val scoreAchieved: String, // "12 reps" or "18s"
    val passed: Boolean,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
