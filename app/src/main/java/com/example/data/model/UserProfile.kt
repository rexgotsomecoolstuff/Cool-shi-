package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Athlete",
    val age: Int = 25,
    val sex: String = "Male",
    val heightCm: Float = 178f,
    val currentWeightKg: Float = 75f,
    val targetWeightKg: Float? = 78f,
    val bodyFatEstimate: Float? = 15f,
    val waistCm: Float? = 82f,
    val chestCm: Float? = 102f,
    val armCm: Float? = 37f,
    val legCm: Float? = 58f,
    val fitnessLevel: String = FitnessLevel.INTERMEDIATE.name,
    val primaryGoal: String = PhysiqueGoal.AESTHETIC.name,
    val secondaryGoal: String? = PhysiqueGoal.CALISTHENICS.name,
    val priorityMuscles: String = "UPPER_CHEST,LATERAL_DELTS,LATS,ARMS", // CSV
    val timeframe: String = Timeframe.WEEKS_12.name,
    val trainingEnvironment: String = TrainingEnvironment.FULL_GYM.name,
    val availableEquipment: String = "BARBELL,DUMBBELLS,PULL_UP_BAR,DIP_BARS,GYM_MACHINES,BENCH", // CSV
    val trainingPreferences: String = "HYPERTROPHY,CALISTHENICS,STRENGTH_TRAINING", // CSV
    val trainingDislikes: String = "", // CSV
    val daysPerWeek: Int = 4,
    val sessionDurationMin: Int = 45,
    val dietaryRestrictions: String = "",
    val allergies: String = "",
    val dailyCalorieTarget: Int = 2450,
    val dailyProteinTargetG: Int = 160,
    val dailyCarbTargetG: Int = 260,
    val dailyFatTargetG: Int = 70,
    val dailyWaterTargetMl: Int = 3200,
    val dailySleepTargetHours: Float = 8f,
    val useImperialUnits: Boolean = false,
    val streakDays: Int = 1,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val isOnboarded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
