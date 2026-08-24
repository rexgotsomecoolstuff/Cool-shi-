package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,
    val name: String,
    val primaryMuscle: String,
    val secondaryMuscles: String, // CSV
    val equipmentRequired: String, // CSV (e.g. BARBELL, DUMBBELLS, NONE)
    val difficulty: String, // BEGINNER, INTERMEDIATE, ADVANCED
    val movementPattern: String, // PUSH, PULL, SQUAT, HINGE, LUNGE, CARRY, ISOLATION, CORE
    val isCalisthenics: Boolean = false,
    val instructions: String,
    val commonMistakes: String,
    val progression: String = "",
    val regression: String = "",
    val defaultSets: Int = 3,
    val defaultReps: String = "8-12",
    val defaultRestSec: Int = 90,
    val defaultTempo: String = "3-0-1-0",
    val safetyNotes: String = ""
)
