package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Achievement
import com.example.data.model.BodyMeasurement
import com.example.data.model.PerformancePR
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyReport
import com.example.data.repository.AiCoachRepository
import com.example.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProgressViewModel(
    private val fitnessRepository: FitnessRepository,
    private val aiCoachRepository: AiCoachRepository
) : ViewModel() {

    val allMeasurements: StateFlow<List<BodyMeasurement>> = fitnessRepository.allMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPRs: StateFlow<List<PerformancePR>> = fitnessRepository.allPRs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAchievements: StateFlow<List<Achievement>> = fitnessRepository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyReports: StateFlow<List<WeeklyReport>> = aiCoachRepository.weeklyReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isGeneratingReport = MutableStateFlow(false)
    val isGeneratingReport = _isGeneratingReport.asStateFlow()

    fun logMeasurement(
        weightKg: Float,
        waistCm: Float?,
        chestCm: Float?,
        armCm: Float?,
        legCm: Float?,
        bodyFat: Float?,
        notes: String
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val m = BodyMeasurement(
                dateString = dateStr,
                weightKg = weightKg,
                waistCm = waistCm,
                chestCm = chestCm,
                armCm = armCm,
                legCm = legCm,
                bodyFatEstimate = bodyFat,
                notes = notes
            )
            fitnessRepository.saveBodyMeasurement(m)
        }
    }

    fun updatePR(exerciseName: String, value: Float, unit: String, category: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            val pr = PerformancePR(
                exerciseName = exerciseName,
                recordValue = value,
                unit = unit,
                category = category,
                achievedDateString = dateStr
            )
            fitnessRepository.savePR(pr)
        }
    }

    fun generateWeeklyReport(profile: UserProfile, workoutsCompleted: Int, totalVolume: Float) {
        viewModelScope.launch {
            _isGeneratingReport.value = true
            aiCoachRepository.generateWeeklyReport(profile, workoutsCompleted, totalVolume)
            _isGeneratingReport.value = false
        }
    }
}
