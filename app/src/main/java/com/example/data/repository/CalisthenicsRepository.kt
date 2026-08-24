package com.example.data.repository

import com.example.data.local.CalisthenicsDao
import com.example.data.local.HabitDao
import com.example.data.local.ProgressDao
import com.example.data.model.CalisthenicsSkill
import com.example.data.model.CalisthenicsTestLog
import com.example.data.sample.SeedData
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalisthenicsRepository(
    private val calisthenicsDao: CalisthenicsDao,
    private val progressDao: ProgressDao,
    private val habitDao: HabitDao
) {
    val allSkills: Flow<List<CalisthenicsSkill>> = calisthenicsDao.getAllSkills()
    val allTestLogs: Flow<List<CalisthenicsTestLog>> = calisthenicsDao.getAllTestLogs()

    suspend fun initializeSkillsIfNeeded() {
        if (calisthenicsDao.getSkillsCount() == 0) {
            calisthenicsDao.insertSkills(SeedData.getInitialCalisthenicsSkills())
        }
    }

    suspend fun logSkillTest(
        skill: CalisthenicsSkill,
        levelTested: Int,
        scoreAchieved: String,
        passed: Boolean,
        notes: String
    ) {
        val testLog = CalisthenicsTestLog(
            skillId = skill.id,
            skillName = skill.skillTreeName,
            levelTested = levelTested,
            scoreAchieved = scoreAchieved,
            passed = passed,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        calisthenicsDao.insertTestLog(testLog)

        if (passed) {
            val newLevel = (skill.currentLevel + 1).coerceAtMost(skill.maxLevel)
            val isMax = newLevel >= skill.maxLevel
            val updatedSkill = skill.copy(
                currentLevel = newLevel,
                isMastered = isMax,
                bestRecord = scoreAchieved,
                lastTestedDate = System.currentTimeMillis()
            )
            calisthenicsDao.updateSkill(updatedSkill)

            // Achievement triggers
            if (skill.id == "skill_pullups" && passed) {
                progressDao.unlockAchievement("ach_first_pullup", System.currentTimeMillis())
            }
            if (skill.id == "skill_handstand" && passed) {
                progressDao.unlockAchievement("ach_handstand_hold", System.currentTimeMillis())
            }
            if (skill.id == "skill_lsit" && passed) {
                progressDao.unlockAchievement("ach_lsit_hold", System.currentTimeMillis())
            }
        }

        // Mark calisthenics practiced today in daily habit log
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val habit = habitDao.getHabitLogForDateOnce(todayStr)
        if (habit != null) {
            habitDao.insertOrUpdateHabitLog(habit.copy(calisthenicsPracticeCompleted = true))
        }
    }
}
