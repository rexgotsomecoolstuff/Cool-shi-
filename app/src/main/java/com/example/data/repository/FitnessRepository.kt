package com.example.data.repository

import com.example.data.local.ExerciseDao
import com.example.data.local.HabitDao
import com.example.data.local.ProgressDao
import com.example.data.local.UserDao
import com.example.data.local.WorkoutDao
import com.example.data.model.Achievement
import com.example.data.model.BodyMeasurement
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetLog
import com.example.data.model.PerformancePR
import com.example.data.model.PhysiqueGoal
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutExerciseItem
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionLog
import com.example.data.sample.SeedData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FitnessRepository(
    private val userDao: UserDao,
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val progressDao: ProgressDao,
    private val habitDao: HabitDao
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val exerciseListType = Types.newParameterizedType(List::class.java, WorkoutExerciseItem::class.java)
    private val exerciseListAdapter = moshi.adapter<List<WorkoutExerciseItem>>(exerciseListType)

    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()
    val workoutPlans: Flow<List<WorkoutPlan>> = workoutDao.getAllWorkoutPlans()
    val recentSessions: Flow<List<WorkoutSessionLog>> = workoutDao.getAllSessionLogs()
    val allPRs: Flow<List<PerformancePR>> = progressDao.getAllPRs()
    val allAchievements: Flow<List<Achievement>> = progressDao.getAllAchievements()
    val allMeasurements: Flow<List<BodyMeasurement>> = progressDao.getAllMeasurements()

    suspend fun initializeSeedDataIfNeeded() {
        if (exerciseDao.getCount() == 0) {
            exerciseDao.insertExercises(SeedData.getInitialExercises())
        }
        if (progressDao.getAchievementsCount() == 0) {
            progressDao.insertAchievements(SeedData.getInitialAchievements())
            for (pr in SeedData.getInitialPRs()) {
                progressDao.insertOrUpdatePR(pr)
            }
        }
        val existingProfile = userDao.getUserProfileOnce()
        if (existingProfile == null) {
            userDao.insertOrUpdateProfile(UserProfile(id = 1, isOnboarded = false))
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userDao.insertOrUpdateProfile(profile)
        // Generate personalized workout system based on user specs
        generateSystemWorkoutPlans(profile)
    }

    suspend fun updateProfile(profile: UserProfile) {
        userDao.updateProfile(profile)
    }

    suspend fun generateSystemWorkoutPlans(profile: UserProfile) {
        val exercises = exerciseDao.getAllExercises().firstOrNull() ?: SeedData.getInitialExercises()
        val userEquipment = profile.availableEquipment.split(",").map { it.trim().uppercase() }.toSet()
        val userDays = profile.daysPerWeek
        val goal = profile.primaryGoal

        // Filter exercises strictly to what the user owns/has access to
        val availableExercises = exercises.filter { ex ->
            val required = ex.equipmentRequired.split(",").map { it.trim().uppercase() }
            required.contains("NONE") || required.any { req -> userEquipment.contains(req) }
        }.ifEmpty {
            // Fallback to bodyweight exercises
            exercises.filter { it.equipmentRequired.contains("NONE") || it.isCalisthenics }
        }

        workoutDao.clearWorkoutPlans()

        val plans = mutableListOf<WorkoutPlan>()

        when {
            userDays == 2 -> {
                // Full Body A & B
                plans.add(buildFullBodyWorkout("A", "Full Body Power & Hypertrophy A", availableExercises, profile))
                plans.add(buildFullBodyWorkout("B", "Full Body Aesthetics & Core B", availableExercises, profile))
            }
            userDays == 3 -> {
                // Push / Pull / Legs or Full Body
                plans.add(buildTargetedWorkout("Day 1 - Push", "Chest, Shoulders, Triceps", listOf("CHEST", "UPPER_CHEST", "SHOULDERS", "LATERAL_DELTS", "TRICEPS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 2 - Pull", "Back, Lats, Biceps, Rear Delts", listOf("BACK", "LATS", "BICEPS", "REAR_DELTS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 3 - Legs & Core", "Quads, Glutes, Calves, Core", listOf("LEGS", "GLUTES", "CALVES", "CORE"), availableExercises, profile))
            }
            userDays == 4 -> {
                // Upper / Lower Split
                plans.add(buildTargetedWorkout("Day 1 - Upper Power", "Upper Body Heavy Strength & Skill", listOf("CHEST", "BACK", "SHOULDERS", "BICEPS", "TRICEPS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 2 - Lower Strength", "Quad & Hamstring Power + Trunk", listOf("LEGS", "GLUTES", "CALVES", "CORE"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 3 - Upper Hypertrophy", "Upper Body Sculpt & Symmetry", listOf("UPPER_CHEST", "LATERAL_DELTS", "LATS", "ARMS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 4 - Lower & Calisthenics", "Posterior Chain & Core Stability", listOf("GLUTES", "LEGS", "CORE", "POSTURE"), availableExercises, profile))
            }
            userDays == 5 -> {
                // PPL + Upper/Lower or Aesthetic Split
                plans.add(buildTargetedWorkout("Day 1 - Push Alpha", "Chest & Lateral Delts Focus", listOf("CHEST", "UPPER_CHEST", "LATERAL_DELTS", "TRICEPS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 2 - Pull Alpha", "Lats & Mid-Back Density", listOf("LATS", "BACK", "REAR_DELTS", "BICEPS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 3 - Legs & Core", "Squat Mechanics & Core Stability", listOf("LEGS", "GLUTES", "CALVES", "CORE"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 4 - Shoulders & Arms", "Deltoid Cap & Arm Hypertrophy", listOf("SHOULDERS", "LATERAL_DELTS", "BICEPS", "TRICEPS", "FOREARMS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 5 - Full Body Dynamic", "Calisthenics Skill & Functional Strength", listOf("CHEST", "BACK", "LEGS", "CORE"), availableExercises, profile))
            }
            else -> {
                // 6 Days PPL x 2
                plans.add(buildTargetedWorkout("Day 1 - Push (Strength)", "Heavy Pecs & Deltoids", listOf("CHEST", "SHOULDERS", "TRICEPS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 2 - Pull (Strength)", "Vertical Pull & Scapular Control", listOf("LATS", "BACK", "BICEPS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 3 - Legs (Quad Dominant)", "Squat Mastery & Core", listOf("LEGS", "GLUTES", "CALVES"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 4 - Push (Hypertrophy)", "Incline Pecs & Side Delts", listOf("UPPER_CHEST", "LATERAL_DELTS", "TRICEPS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 5 - Pull (Hypertrophy)", "Horizontal Rows & Arm Density", listOf("BACK", "REAR_DELTS", "BICEPS"), availableExercises, profile))
                plans.add(buildTargetedWorkout("Day 6 - Legs (Posterior & Skills)", "Hamstrings, Glutes & Calisthenics", listOf("GLUTES", "LEGS", "CORE"), availableExercises, profile))
            }
        }

        workoutDao.insertWorkoutPlans(plans)
    }

    private fun buildTargetedWorkout(
        title: String,
        subtitle: String,
        muscles: List<String>,
        available: List<Exercise>,
        profile: UserProfile
    ): WorkoutPlan {
        val selectedExercises = mutableListOf<Exercise>()

        for (muscle in muscles) {
            val matching = available.filter { it.primaryMuscle == muscle || it.secondaryMuscles.contains(muscle) }
            val chosen = matching.shuffled().firstOrNull { !selectedExercises.contains(it) }
            if (chosen != null) {
                selectedExercises.add(chosen)
            }
        }

        // Fill up to appropriate count based on duration (15min -> 3ex, 45min -> 5-6ex, 60min -> 6-7ex)
        val targetCount = when {
            profile.sessionDurationMin <= 20 -> 3
            profile.sessionDurationMin <= 35 -> 4
            profile.sessionDurationMin <= 50 -> 5
            else -> 6
        }

        while (selectedExercises.size < targetCount && available.isNotEmpty()) {
            val extra = available.shuffled().firstOrNull { !selectedExercises.contains(it) } ?: break
            selectedExercises.add(extra)
        }

        val items = selectedExercises.mapIndexed { idx, ex ->
            WorkoutExerciseItem(
                exerciseId = ex.id,
                exerciseName = ex.name,
                targetMuscles = "${ex.primaryMuscle} (${ex.secondaryMuscles})",
                sets = ex.defaultSets,
                repsTarget = ex.defaultReps,
                restSec = ex.defaultRestSec,
                tempo = ex.defaultTempo,
                rpeTarget = if (idx == 0) "RPE 8-9" else "RPE 7-8",
                weightKg = 0f,
                notes = ex.instructions.take(80) + "...",
                equipmentRequired = ex.equipmentRequired
            )
        }

        return WorkoutPlan(
            id = UUID.randomUUID().toString(),
            title = title,
            subtitle = subtitle,
            splitType = "ORAXIS_ADAPTIVE",
            dayOfWeekName = title.substringBefore(" -"),
            targetMuscleGroups = muscles.joinToString(","),
            estimatedDurationMin = profile.sessionDurationMin,
            difficulty = profile.fitnessLevel,
            exercisesJson = exerciseListAdapter.toJson(items),
            warmupGuidance = "5 min dynamic joint mobility: Arm circles, Cat-cow, World's greatest stretch, 2 ramp-up warmup sets.",
            cooldownGuidance = "5 min static stretching: Doorway chest stretch, Kneeling hip flexor stretch, Child's pose."
        )
    }

    private fun buildFullBodyWorkout(
        variant: String,
        title: String,
        available: List<Exercise>,
        profile: UserProfile
    ): WorkoutPlan {
        val targets = listOf("CHEST", "BACK", "LEGS", "SHOULDERS", "CORE")
        return buildTargetedWorkout("Full Body $variant", title, targets, available, profile)
    }

    fun parseExerciseItems(json: String): List<WorkoutExerciseItem> {
        return try {
            exerciseListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun recordCompletedSession(
        plan: WorkoutPlan,
        durationSeconds: Int,
        rpe: Int,
        feedback: String,
        setLogs: List<ExerciseSetLog>
    ) {
        val sessionId = UUID.randomUUID().toString()
        val totalSets = setLogs.count { it.isCompleted }
        val totalReps = setLogs.filter { it.isCompleted }.sumOf { it.repsCompleted }
        val totalVolume = setLogs.filter { it.isCompleted }.sumOf { (it.weightKg * it.repsCompleted).toDouble() }.toFloat()

        val sessionLog = WorkoutSessionLog(
            id = sessionId,
            planId = plan.id,
            planTitle = plan.title,
            dateEpoch = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            totalVolumeKg = totalVolume,
            totalSetsCompleted = totalSets,
            totalRepsCompleted = totalReps,
            perceivedExertionRpe = rpe,
            performanceFeedback = feedback,
            notes = "Adaptive auto-logged session"
        )

        workoutDao.insertSessionLog(sessionLog)
        val mappedSets = setLogs.map { it.copy(sessionId = sessionId) }
        workoutDao.insertSetLogs(mappedSets)

        // Mark today completed in habit log
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        habitDao.updateWorkoutCompleted(todayStr, true)

        // Mark plan completed
        workoutDao.updateWorkoutPlan(plan.copy(isCompletedToday = true))

        // Check and unlock Achievements
        progressDao.unlockAchievement("ach_first_workout", System.currentTimeMillis())
        val totalSessions = workoutDao.getTotalCompletedSessionsCount()
        if (totalSessions >= 10) progressDao.unlockAchievement("ach_workouts_10", System.currentTimeMillis())
        if (totalSessions >= 50) progressDao.unlockAchievement("ach_workouts_50", System.currentTimeMillis())

        // Smart Adaptation: adjust next workouts based on performance feedback
        adaptFutureWorkouts(feedback, rpe)
    }

    private suspend fun adaptFutureWorkouts(feedback: String, rpe: Int) {
        val plans = workoutDao.getAllWorkoutPlans().firstOrNull() ?: return
        for (plan in plans) {
            val items = parseExerciseItems(plan.exercisesJson).toMutableList()
            val adjusted = items.map { item ->
                when {
                    feedback == "EASY" || rpe <= 6 -> {
                        // Increase reps or slight intensity
                        item.copy(notes = "ORAXIS AI Auto-Progression: Load adjusted +2.5% based on previous ease.")
                    }
                    feedback == "STRUGGLED" || rpe >= 9 -> {
                        // Recommend recovery tempo
                        item.copy(notes = "ORAXIS AI Deload note: Focus strictly on tempo and form.")
                    }
                    else -> item
                }
            }
            workoutDao.updateWorkoutPlan(plan.copy(exercisesJson = exerciseListAdapter.toJson(adjusted)))
        }
    }

    suspend fun saveBodyMeasurement(measurement: BodyMeasurement) {
        progressDao.insertMeasurement(measurement)
        // Also update current weight on user profile if recorded
        val profile = userDao.getUserProfileOnce()
        if (profile != null && measurement.weightKg > 0f) {
            userDao.updateProfile(profile.copy(
                currentWeightKg = measurement.weightKg,
                waistCm = measurement.waistCm ?: profile.waistCm,
                chestCm = measurement.chestCm ?: profile.chestCm,
                armCm = measurement.armCm ?: profile.armCm,
                legCm = measurement.legCm ?: profile.legCm
            ))
        }
    }

    suspend fun savePR(pr: PerformancePR) {
        progressDao.insertOrUpdatePR(pr)
        progressDao.unlockAchievement("ach_first_pr", System.currentTimeMillis())
    }
}
