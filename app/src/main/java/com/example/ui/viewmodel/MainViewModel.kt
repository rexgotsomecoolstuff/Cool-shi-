package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.DailyHabitLog
import com.example.data.model.RecoveryAnalysis
import com.example.data.model.UserProfile
import com.example.data.repository.AiCoachRepository
import com.example.data.repository.CalisthenicsRepository
import com.example.data.repository.FitnessRepository
import com.example.data.repository.HabitRepository
import com.example.data.repository.NutritionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeDashboardState(
    val profile: UserProfile = UserProfile(),
    val todayHabit: DailyHabitLog = DailyHabitLog(""),
    val recoveryAnalysis: RecoveryAnalysis = RecoveryAnalysis(85, "OPTIMAL", "Systems primed", 80, 80),
    val overallDailyScorePercent: Int = 0,
    val completedExercisesToday: Int = 0,
    val totalExercisesToday: Int = 0,
    val isBuildingSystem: Boolean = false
)

class MainViewModel(
    private val fitnessRepository: FitnessRepository,
    private val habitRepository: HabitRepository,
    private val calisthenicsRepository: CalisthenicsRepository,
    private val nutritionRepository: NutritionRepository,
    private val aiCoachRepository: AiCoachRepository
) : ViewModel() {

    val profile: StateFlow<UserProfile?> = fitnessRepository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayDateStr = habitRepository.getTodayDateString()

    val todayHabit: StateFlow<DailyHabitLog?> = habitRepository.getHabitForDate(todayDateStr)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isBuildingSystem = MutableStateFlow(false)
    val isBuildingSystem = _isBuildingSystem.asStateFlow()

    init {
        viewModelScope.launch {
            fitnessRepository.initializeSeedDataIfNeeded()
            calisthenicsRepository.initializeSkillsIfNeeded()
            habitRepository.getOrCreateTodayHabit()
        }
    }

    fun completeOnboarding(updatedProfile: UserProfile, onFinished: () -> Unit) {
        viewModelScope.launch {
            _isBuildingSystem.value = true
            // Calculate targets
            val (cal, prot, carbsFat) = nutritionRepository.calculateTargets(
                age = updatedProfile.age,
                sex = updatedProfile.sex,
                weightKg = updatedProfile.currentWeightKg,
                heightCm = updatedProfile.heightCm,
                goal = updatedProfile.primaryGoal
            )
            val readyProfile = updatedProfile.copy(
                isOnboarded = true,
                dailyCalorieTarget = cal,
                dailyProteinTargetG = prot,
                dailyCarbTargetG = carbsFat.first,
                dailyFatTargetG = carbsFat.second
            )

            fitnessRepository.saveUserProfile(readyProfile)
            kotlinx.coroutines.delay(1800) // Aesthetic System Generation Pause
            _isBuildingSystem.value = false
            onFinished()
        }
    }

    fun addWater(ml: Int = 250) {
        viewModelScope.launch {
            habitRepository.addWater(ml)
        }
    }

    fun toggleMobility() {
        viewModelScope.launch {
            habitRepository.toggleMobility()
        }
    }

    fun updateHabit(log: DailyHabitLog) {
        viewModelScope.launch {
            habitRepository.updateHabitLog(log)
        }
    }

    fun updateProfile(newProfile: UserProfile) {
        viewModelScope.launch {
            fitnessRepository.saveUserProfile(newProfile)
        }
    }
}
