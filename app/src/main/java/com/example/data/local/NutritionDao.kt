package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MealLog
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Query("SELECT * FROM meal_logs WHERE dateString = :dateString ORDER BY timestamp ASC")
    fun getMealsForDate(dateString: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLog): Long

    @Delete
    suspend fun deleteMeal(meal: MealLog)

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMealById(id: Long)

    @Query("SELECT SUM(caloriesKcal) FROM meal_logs WHERE dateString = :dateString")
    suspend fun getTotalCaloriesForDate(dateString: String): Int?

    @Query("SELECT SUM(proteinG) FROM meal_logs WHERE dateString = :dateString")
    suspend fun getTotalProteinForDate(dateString: String): Float?
}
