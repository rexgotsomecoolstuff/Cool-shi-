package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Achievement
import com.example.data.model.BodyMeasurement
import com.example.data.model.CalisthenicsSkill
import com.example.data.model.CalisthenicsTestLog
import com.example.data.model.ChatMessage
import com.example.data.model.DailyHabitLog
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetLog
import com.example.data.model.MealLog
import com.example.data.model.PerformancePR
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyReport
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionLog

@Database(
    entities = [
        UserProfile::class,
        Exercise::class,
        WorkoutPlan::class,
        WorkoutSessionLog::class,
        ExerciseSetLog::class,
        CalisthenicsSkill::class,
        CalisthenicsTestLog::class,
        MealLog::class,
        DailyHabitLog::class,
        BodyMeasurement::class,
        PerformancePR::class,
        WeeklyReport::class,
        Achievement::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OraxisDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun habitDao(): HabitDao
    abstract fun calisthenicsDao(): CalisthenicsDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: OraxisDatabase? = null

        fun getDatabase(context: Context): OraxisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OraxisDatabase::class.java,
                    "oraxis_physic_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
