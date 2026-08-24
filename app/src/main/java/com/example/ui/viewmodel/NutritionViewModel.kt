package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DailyNutritionSummary
import com.example.data.model.MealLog
import com.example.data.model.MealType
import com.example.data.model.UserProfile
import com.example.data.repository.AiCoachRepository
import com.example.data.repository.HabitRepository
import com.example.data.repository.NutritionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NutritionViewModel(
    private val nutritionRepository: NutritionRepository,
    private val habitRepository: HabitRepository,
    private val aiCoachRepository: AiCoachRepository
) : ViewModel() {

    val todayDateStr = habitRepository.getTodayDateString()

    private val _selectedDate = MutableStateFlow(todayDateStr)
    val selectedDate = _selectedDate.asStateFlow()

    val mealsToday: StateFlow<List<MealLog>> = _selectedDate.flatMapLatest { date ->
        nutritionRepository.getMealsForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _aiSuggestion = MutableStateFlow<String?>(null)
    val aiSuggestion = _aiSuggestion.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    fun logMeal(
        mealType: MealType,
        foodName: String,
        portion: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float
    ) {
        viewModelScope.launch {
            val meal = MealLog(
                dateString = _selectedDate.value,
                mealType = mealType.name,
                foodName = foodName,
                portionDescription = portion,
                caloriesKcal = calories,
                proteinG = protein,
                carbsG = carbs,
                fatG = fat
            )
            nutritionRepository.logMeal(meal)
        }
    }

    fun deleteMeal(id: Long) {
        viewModelScope.launch {
            nutritionRepository.deleteMeal(id)
        }
    }

    fun askNutritionAi(query: String, profile: UserProfile) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val reply = aiCoachRepository.sendMessage(query, profile)
            _aiSuggestion.value = reply
            _isAiLoading.value = false
        }
    }

    fun clearAiSuggestion() {
        _aiSuggestion.value = null
    }
}
