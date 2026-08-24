package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DailyHabitLog
import com.example.data.model.PhysiqueGoal
import com.example.data.repository.HabitRepository
import com.example.data.repository.NutritionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ORAXIS PHYSIC", appName)
    }

    @Test
    fun `test nutrition calculator calculates safe macros`() {
        val dummyNutritionDao = object : com.example.data.local.NutritionDao {
            override fun getMealsForDate(date: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.MealLog>())
            override suspend fun insertMeal(meal: com.example.data.model.MealLog) = 1L
            override suspend fun deleteMealById(id: Long) {}
            override suspend fun deleteMealsForDate(date: String) {}
        }
        val dummyUserDao = object : com.example.data.local.UserDao {
            override fun getUserProfile() = kotlinx.coroutines.flow.flowOf(null)
            override suspend fun getUserProfileOnce() = null
            override suspend fun insertOrUpdateProfile(profile: com.example.data.model.UserProfile) {}
            override suspend fun updateProfile(profile: com.example.data.model.UserProfile) {}
        }
        val dummyHabitDao = object : com.example.data.local.HabitDao {
            override fun getHabitLogForDate(date: String) = kotlinx.coroutines.flow.flowOf(null)
            override suspend fun getHabitLogForDateOnce(date: String) = null
            override suspend fun insertOrUpdateHabitLog(log: DailyHabitLog) {}
            override suspend fun updateWorkoutCompleted(date: String, completed: Boolean) {}
            override suspend fun getRecentHabits(limit: Int) = kotlinx.coroutines.flow.flowOf(emptyList<DailyHabitLog>())
        }

        val repo = NutritionRepository(dummyNutritionDao, dummyUserDao, dummyHabitDao)
        val (calories, protein, carbsFat) = repo.calculateTargets(
            age = 22,
            sex = "Male",
            weightKg = 75f,
            heightCm = 178f,
            goal = PhysiqueGoal.AESTHETIC.name
        )

        assertTrue("Calories should be sufficient and healthy", calories > 1800)
        assertTrue("Protein should support muscle growth (~150g)", protein in 140..170)
        assertTrue("Carbs should be positive", carbsFat.first > 100)
        assertTrue("Fats should be in healthy range", carbsFat.second in 45..90)
    }

    @Test
    fun `test recovery score calculation`() {
        val dummyHabitDao = object : com.example.data.local.HabitDao {
            override fun getHabitLogForDate(date: String) = kotlinx.coroutines.flow.flowOf(null)
            override suspend fun getHabitLogForDateOnce(date: String) = null
            override suspend fun insertOrUpdateHabitLog(log: DailyHabitLog) {}
            override suspend fun updateWorkoutCompleted(date: String, completed: Boolean) {}
            override suspend fun getRecentHabits(limit: Int) = kotlinx.coroutines.flow.flowOf(emptyList<DailyHabitLog>())
        }

        val habitRepo = HabitRepository(dummyHabitDao)
        val sampleHabit = DailyHabitLog(
            dateString = "2026-08-24",
            waterIntakeMl = 3200,
            sleepHours = 8.2f,
            stepsCount = 8500,
            moodRating = 5,
            energyRating = 5,
            muscleSorenessRating = 1,
            trainingFatigueRating = 1
        )

        val analysis = habitRepo.calculateRecovery(sampleHabit, targetWater = 3000, targetSleep = 8.0f)
        assertTrue("Score should be peak recovery", analysis.recoveryScorePercent >= 80)
        assertEquals("PEAK RECOVERY", analysis.statusLevel)
    }
}
