package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetLog
import com.example.data.model.WorkoutExerciseItem
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionLog
import com.example.data.repository.FitnessRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveWorkoutState(
    val plan: WorkoutPlan? = null,
    val items: List<WorkoutExerciseItem> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val completedSetLogs: List<ExerciseSetLog> = emptyList(),
    val isTimerRunning: Boolean = false,
    val elapsedSeconds: Int = 0,
    val restTimerRemainingSeconds: Int = 0,
    val isRestTimerActive: Boolean = false,
    val isFinished: Boolean = false
)

class WorkoutViewModel(
    private val fitnessRepository: FitnessRepository
) : ViewModel() {

    val workoutPlans: StateFlow<List<WorkoutPlan>> = fitnessRepository.workoutPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExercises: StateFlow<List<Exercise>> = fitnessRepository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessionLogs: StateFlow<List<WorkoutSessionLog>> = fitnessRepository.recentSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedMuscleFilter = MutableStateFlow("ALL")
    val selectedMuscleFilter = _selectedMuscleFilter.asStateFlow()

    private val _selectedEquipmentFilter = MutableStateFlow("ALL")
    val selectedEquipmentFilter = _selectedEquipmentFilter.asStateFlow()

    val filteredExercises: StateFlow<List<Exercise>> = combine(
        allExercises,
        _searchQuery,
        _selectedMuscleFilter,
        _selectedEquipmentFilter
    ) { exercises, query, muscle, equip ->
        exercises.filter { ex ->
            val matchQuery = query.isBlank() || ex.name.contains(query, ignoreCase = true) || ex.primaryMuscle.contains(query, ignoreCase = true)
            val matchMuscle = muscle == "ALL" || ex.primaryMuscle.equals(muscle, ignoreCase = true) || ex.secondaryMuscles.contains(muscle, ignoreCase = true)
            val matchEquip = equip == "ALL" || (equip == "CALISTHENICS" && ex.isCalisthenics) || ex.equipmentRequired.contains(equip, ignoreCase = true)
            matchQuery && matchMuscle && matchEquip
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Workout Execution
    private val _activeWorkout = MutableStateFlow(ActiveWorkoutState())
    val activeWorkout = _activeWorkout.asStateFlow()

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMuscleFilter(muscle: String) {
        _selectedMuscleFilter.value = muscle
    }

    fun setEquipmentFilter(equip: String) {
        _selectedEquipmentFilter.value = equip
    }

    fun parsePlanItems(plan: WorkoutPlan): List<WorkoutExerciseItem> {
        return fitnessRepository.parseExerciseItems(plan.exercisesJson)
    }

    fun startWorkout(plan: WorkoutPlan) {
        val items = fitnessRepository.parseExerciseItems(plan.exercisesJson)
        _activeWorkout.value = ActiveWorkoutState(
            plan = plan,
            items = items,
            currentExerciseIndex = 0,
            completedSetLogs = emptyList(),
            isTimerRunning = true,
            elapsedSeconds = 0,
            isFinished = false
        )

        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeWorkout.value = _activeWorkout.value.copy(
                    elapsedSeconds = _activeWorkout.value.elapsedSeconds + 1
                )
            }
        }
    }

    fun logSet(
        exerciseItem: WorkoutExerciseItem,
        setNumber: Int,
        weight: Float,
        reps: Int,
        rpe: Float
    ) {
        val currentSets = _activeWorkout.value.completedSetLogs.toMutableList()
        val newSet = ExerciseSetLog(
            sessionId = "",
            exerciseId = exerciseItem.exerciseId,
            exerciseName = exerciseItem.exerciseName,
            setNumber = setNumber,
            weightKg = weight,
            repsCompleted = reps,
            rpe = rpe,
            isCompleted = true
        )
        currentSets.add(newSet)
        _activeWorkout.value = _activeWorkout.value.copy(completedSetLogs = currentSets)

        // Start Rest Timer
        startRestTimer(exerciseItem.restSec)
    }

    private fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value.copy(
            restTimerRemainingSeconds = seconds,
            isRestTimerActive = true
        )
        restTimerJob = viewModelScope.launch {
            var left = seconds
            while (left > 0) {
                delay(1000)
                left -= 1
                _activeWorkout.value = _activeWorkout.value.copy(restTimerRemainingSeconds = left)
            }
            _activeWorkout.value = _activeWorkout.value.copy(isRestTimerActive = false)
        }
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value.copy(isRestTimerActive = false, restTimerRemainingSeconds = 0)
    }

    fun finishWorkout(rpe: Int, feedback: String, onFinished: () -> Unit) {
        val current = _activeWorkout.value
        val plan = current.plan ?: return

        workoutTimerJob?.cancel()
        restTimerJob?.cancel()

        viewModelScope.launch {
            fitnessRepository.recordCompletedSession(
                plan = plan,
                durationSeconds = current.elapsedSeconds,
                rpe = rpe,
                feedback = feedback,
                setLogs = current.completedSetLogs
            )
            _activeWorkout.value = _activeWorkout.value.copy(isFinished = true, isTimerRunning = false)
            onFinished()
        }
    }

    fun cancelActiveWorkout() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _activeWorkout.value = ActiveWorkoutState()
    }
}
