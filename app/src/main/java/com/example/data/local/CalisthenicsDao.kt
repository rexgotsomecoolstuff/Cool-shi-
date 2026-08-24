package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CalisthenicsSkill
import com.example.data.model.CalisthenicsTestLog
import kotlinx.coroutines.flow.Flow

@Dao
interface CalisthenicsDao {
    @Query("SELECT * FROM calisthenics_skills ORDER BY currentLevel ASC")
    fun getAllSkills(): Flow<List<CalisthenicsSkill>>

    @Query("SELECT * FROM calisthenics_skills WHERE id = :id LIMIT 1")
    suspend fun getSkillById(id: String): CalisthenicsSkill?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<CalisthenicsSkill>)

    @Update
    suspend fun updateSkill(skill: CalisthenicsSkill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestLog(log: CalisthenicsTestLog)

    @Query("SELECT * FROM calisthenics_test_logs ORDER BY timestamp DESC")
    fun getAllTestLogs(): Flow<List<CalisthenicsTestLog>>

    @Query("SELECT COUNT(*) FROM calisthenics_skills")
    suspend fun getSkillsCount(): Int
}
