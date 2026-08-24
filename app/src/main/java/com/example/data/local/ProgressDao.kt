package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Achievement
import com.example.data.model.BodyMeasurement
import com.example.data.model.ChatMessage
import com.example.data.model.PerformancePR
import com.example.data.model.WeeklyReport
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    // Body Measurements
    @Query("SELECT * FROM body_measurements ORDER BY dateString DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurement)

    @Query("SELECT * FROM body_measurements ORDER BY dateString DESC LIMIT 1")
    suspend fun getLatestMeasurement(): BodyMeasurement?

    // Performance PRs
    @Query("SELECT * FROM performance_prs ORDER BY exerciseName ASC")
    fun getAllPRs(): Flow<List<PerformancePR>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePR(pr: PerformancePR)

    // Weekly Reports
    @Query("SELECT * FROM weekly_reports ORDER BY weekIdentifier DESC")
    fun getAllWeeklyReports(): Flow<List<WeeklyReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyReport(report: WeeklyReport)

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedDate = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getAchievementsCount(): Int

    // AI Chat History
    @Query("SELECT * FROM ai_chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)
}
