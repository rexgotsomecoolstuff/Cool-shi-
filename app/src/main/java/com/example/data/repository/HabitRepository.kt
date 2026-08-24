package com.example.data.repository

import com.example.data.local.HabitDao
import com.example.data.model.DailyHabitLog
import com.example.data.model.RecoveryAnalysis
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitRepository(private val habitDao: HabitDao) {

    fun getTodayDateString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun getHabitForDate(dateString: String): Flow<DailyHabitLog?> =
        habitDao.getHabitLogForDate(dateString)

    suspend fun getOrCreateTodayHabit(): DailyHabitLog {
        val today = getTodayDateString()
        val existing = habitDao.getHabitLogForDateOnce(today)
        if (existing != null) return existing

        val newLog = DailyHabitLog(
            dateString = today,
            waterIntakeMl = 500,
            sleepHours = 7.5f,
            stepsCount = 4200,
            moodRating = 4,
            energyRating = 4,
            muscleSorenessRating = 2,
            trainingFatigueRating = 2
        )
        habitDao.insertOrUpdateHabitLog(newLog)
        return newLog
    }

    suspend fun updateHabitLog(log: DailyHabitLog) {
        habitDao.insertOrUpdateHabitLog(log)
    }

    suspend fun addWater(ml: Int) {
        val habit = getOrCreateTodayHabit()
        val updated = habit.copy(waterIntakeMl = (habit.waterIntakeMl + ml).coerceAtMost(6000))
        habitDao.insertOrUpdateHabitLog(updated)
    }

    suspend fun toggleMobility() {
        val habit = getOrCreateTodayHabit()
        habitDao.insertOrUpdateHabitLog(habit.copy(mobilityCompleted = !habit.mobilityCompleted))
    }

    fun calculateRecovery(habit: DailyHabitLog, targetWater: Int, targetSleep: Float): RecoveryAnalysis {
        val sleepRatio = (habit.sleepHours / targetSleep.coerceAtLeast(6f)).coerceIn(0f, 1.2f)
        val waterRatio = (habit.waterIntakeMl.toFloat() / targetWater.coerceAtLeast(2000)).coerceIn(0f, 1.2f)
        val energyScore = habit.energyRating / 5.0f
        val sorenessFactor = 1.0f - ((habit.muscleSorenessRating - 1) / 5.0f * 0.4f)
        val fatigueFactor = 1.0f - ((habit.trainingFatigueRating - 1) / 5.0f * 0.4f)

        val totalScore = ((sleepRatio * 0.35f + waterRatio * 0.20f + energyScore * 0.20f + sorenessFactor * 0.15f + fatigueFactor * 0.10f) * 100).toInt().coerceIn(10, 100)

        val (status, rec) = when {
            totalScore >= 85 -> Pair(
                "PEAK RECOVERY",
                "Central nervous system and muscles are fully primed. Excellent day for heavy progressive overload or high-skill calisthenics."
            )
            totalScore >= 70 -> Pair(
                "OPTIMAL SYSTEM",
                "Solid systemic recovery. Execute your scheduled session with standard volume and intensity."
            )
            totalScore >= 50 -> Pair(
                "MODERATE FATIGUE",
                "Recovery is moderate. Prioritize warm-up sets, maintain proper hydration, and avoid training to complete failure today."
            )
            else -> Pair(
                "HIGH FATIGUE (ACTIVE REST / DELOAD)",
                "Systemic strain detected. Consider reducing overall training volume by 30-50% or focusing on light mobility and active rest."
            )
        }

        return RecoveryAnalysis(
            recoveryScorePercent = totalScore,
            statusLevel = status,
            recommendation = rec,
            sleepScorePercent = (sleepRatio * 100).toInt().coerceIn(0, 100),
            hydrationScorePercent = (waterRatio * 100).toInt().coerceIn(0, 100)
        )
    }
}
