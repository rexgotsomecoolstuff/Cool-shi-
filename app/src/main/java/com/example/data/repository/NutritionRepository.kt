package com.example.data.repository

import com.example.data.local.HabitDao
import com.example.data.local.NutritionDao
import com.example.data.local.UserDao
import com.example.data.model.DailyNutritionSummary
import com.example.data.model.MealLog
import com.example.data.model.PhysiqueGoal
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NutritionRepository(
    private val nutritionDao: NutritionDao,
    private val userDao: UserDao,
    private val habitDao: HabitDao
) {
    fun getMealsForDate(dateString: String): Flow<List<MealLog>> =
        nutritionDao.getMealsForDate(dateString)

    fun getDailySummary(dateString: String, profile: UserProfile): Flow<DailyNutritionSummary> {
        return nutritionDao.getMealsForDate(dateString).map { meals ->
            val totalCal = meals.sumOf { it.caloriesKcal }
            val totalP = meals.sumOf { it.proteinG.toDouble() }.toFloat()
            val totalC = meals.sumOf { it.carbsG.toDouble() }.toFloat()
            val totalF = meals.sumOf { it.fatG.toDouble() }.toFloat()

            DailyNutritionSummary(
                dateString = dateString,
                totalCalories = totalCal,
                totalProtein = totalP,
                totalCarbs = totalC,
                totalFat = totalF,
                calorieTarget = profile.dailyCalorieTarget,
                proteinTarget = profile.dailyProteinTargetG,
                carbsTarget = profile.dailyCarbTargetG,
                fatTarget = profile.dailyFatTargetG,
                meals = meals
            )
        }
    }

    suspend fun logMeal(meal: MealLog): Long {
        return nutritionDao.insertMeal(meal)
    }

    suspend fun deleteMeal(id: Long) {
        nutritionDao.deleteMealById(id)
    }

    /**
     * Calculates realistic, healthy macro recommendations based on Mifflin-St Jeor TDEE formula.
     * Includes safety guardrails: For minors (<18), prevents caloric restriction.
     */
    fun calculateTargets(
        age: Int,
        sex: String,
        weightKg: Float,
        heightCm: Float,
        goal: String,
        activityLevel: Float = 1.4f
    ): Triple<Int, Int, Pair<Int, Int>> { // Returns (Calories, Protein, Pair(Carbs, Fat))
        // BMR (Mifflin-St Jeor)
        val bmr = if (sex.equals("Female", ignoreCase = true)) {
            (10 * weightKg) + (6.25f * heightCm) - (5 * age) - 161
        } else {
            (10 * weightKg) + (6.25f * heightCm) - (5 * age) + 5
        }

        val tdee = bmr * activityLevel

        val isMinor = age < 18

        val adjustedCalories = when {
            isMinor -> (tdee + 150).toInt() // Minors require energy for growth and development; never restrict
            goal.contains(PhysiqueGoal.BULKY_MASS.name) -> (tdee + 350).toInt()
            goal.contains(PhysiqueGoal.MUSCULAR.name) -> (tdee + 250).toInt()
            goal.contains(PhysiqueGoal.LEAN_ATHLETIC.name) -> (tdee - 200).coerceAtLeast(1500f).toInt()
            goal.contains(PhysiqueGoal.AESTHETIC.name) -> (tdee - 150).coerceAtLeast(1600f).toInt()
            else -> tdee.toInt()
        }

        // Protein: 1.8g - 2.2g per kg bodyweight
        val proteinGrams = (weightKg * 2.0f).toInt().coerceIn(100, 220)

        // Fats: ~25-30% of total calories (9 kcal/g)
        val fatCalories = adjustedCalories * 0.25f
        val fatGrams = (fatCalories / 9f).toInt().coerceIn(45, 100)

        // Carbs: Remaining calories (4 kcal/g)
        val remainingCal = adjustedCalories - (proteinGrams * 4) - (fatGrams * 9)
        val carbGrams = (remainingCal / 4f).toInt().coerceAtLeast(120)

        return Triple(adjustedCalories, proteinGrams, Pair(carbGrams, fatGrams))
    }
}
