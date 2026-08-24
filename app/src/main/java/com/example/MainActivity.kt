package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.OraxisDatabase
import com.example.data.model.DailyNutritionSummary
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutPlan
import com.example.data.repository.AiCoachRepository
import com.example.data.repository.CalisthenicsRepository
import com.example.data.repository.FitnessRepository
import com.example.data.repository.HabitRepository
import com.example.data.repository.NutritionRepository
import com.example.ui.screens.calisthenics.CalisthenicsScreen
import com.example.ui.screens.coach.CoachScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.nutrition.NutritionScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.screens.workout.ActiveWorkoutScreen
import com.example.ui.screens.workout.WorkoutScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.OraxisPhysicTheme
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AiCoachViewModel
import com.example.ui.viewmodel.CalisthenicsViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NutritionViewModel
import com.example.ui.viewmodel.ProgressViewModel
import com.example.ui.viewmodel.WorkoutViewModel

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    WORKOUTS("Workouts", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "nav_workouts"),
    CALISTHENICS("Skills", Icons.Filled.MilitaryTech, Icons.Outlined.MilitaryTech, "nav_calisthenics"),
    NUTRITION("Nutrition", Icons.Filled.Restaurant, Icons.Outlined.Restaurant, "nav_nutrition"),
    PROGRESS("Progress", Icons.Filled.ShowChart, Icons.Outlined.ShowChart, "nav_progress"),
    COACH("AI Coach", Icons.Filled.AutoAwesome, Icons.Filled.AutoAwesome, "nav_coach"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = OraxisDatabase.getDatabase(applicationContext)
        val fitnessRepo = FitnessRepository(db.userDao(), db.workoutDao(), db.exerciseDao(), db.progressDao(), db.habitDao())
        val calisthenicsRepo = CalisthenicsRepository(db.calisthenicsDao(), db.progressDao(), db.habitDao())
        val habitRepo = HabitRepository(db.habitDao())
        val nutritionRepo = NutritionRepository(db.nutritionDao(), db.userDao(), db.habitDao())
        val aiCoachRepo = AiCoachRepository(db.progressDao())

        setContent {
            OraxisPhysicTheme {
                val mainViewModel = remember { MainViewModel(fitnessRepo, habitRepo, calisthenicsRepo, nutritionRepo, aiCoachRepo) }
                val workoutViewModel = remember { WorkoutViewModel(fitnessRepo) }
                val calisthenicsViewModel = remember { CalisthenicsViewModel(calisthenicsRepo) }
                val nutritionViewModel = remember { NutritionViewModel(nutritionRepo, habitRepo, aiCoachRepo) }
                val progressViewModel = remember { ProgressViewModel(fitnessRepo, aiCoachRepo) }
                val aiCoachViewModel = remember { AiCoachViewModel(aiCoachRepo) }

                OraxisApp(
                    mainViewModel = mainViewModel,
                    workoutViewModel = workoutViewModel,
                    calisthenicsViewModel = calisthenicsViewModel,
                    nutritionViewModel = nutritionViewModel,
                    progressViewModel = progressViewModel,
                    aiCoachViewModel = aiCoachViewModel
                )
            }
        }
    }
}

@Composable
fun OraxisApp(
    mainViewModel: MainViewModel,
    workoutViewModel: WorkoutViewModel,
    calisthenicsViewModel: CalisthenicsViewModel,
    nutritionViewModel: NutritionViewModel,
    progressViewModel: ProgressViewModel,
    aiCoachViewModel: AiCoachViewModel
) {
    val profileState by mainViewModel.profile.collectAsState()
    val todayHabitState by mainViewModel.todayHabit.collectAsState()
    val isBuildingSystem by mainViewModel.isBuildingSystem.collectAsState()

    val workoutPlans by workoutViewModel.workoutPlans.collectAsState()
    val allExercises by workoutViewModel.allExercises.collectAsState()
    val filteredExercises by workoutViewModel.filteredExercises.collectAsState()
    val sessionLogs by workoutViewModel.sessionLogs.collectAsState()
    val activeWorkoutState by workoutViewModel.activeWorkout.collectAsState()

    val calisthenicsSkills by calisthenicsViewModel.allSkills.collectAsState()
    val mealsToday by nutritionViewModel.mealsToday.collectAsState()
    val nutritionAiSuggestion by nutritionViewModel.aiSuggestion.collectAsState()
    val isNutritionAiLoading by nutritionViewModel.isAiLoading.collectAsState()

    val measurements by progressViewModel.allMeasurements.collectAsState()
    val prs by progressViewModel.allPRs.collectAsState()
    val achievements by progressViewModel.allAchievements.collectAsState()
    val weeklyReports by progressViewModel.weeklyReports.collectAsState()
    val isGeneratingReport by progressViewModel.isGeneratingReport.collectAsState()

    val chatMessages by aiCoachViewModel.chatMessages.collectAsState()
    val isAiResponding by aiCoachViewModel.isResponding.collectAsState()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

    if (profileState == null) {
        Box(modifier = Modifier.fillMaxSize().background(ObsidianBg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CyanAccent)
        }
        return
    }

    val profile = profileState!!

    if (!profile.isOnboarded) {
        OnboardingScreen(
            initialProfile = profile,
            isBuildingSystem = isBuildingSystem,
            onComplete = { completedProfile ->
                mainViewModel.completeOnboarding(completedProfile) {
                    currentTab = NavigationTab.HOME
                }
            }
        )
        return
    }

    // Active Workout Screen Takeover
    if (activeWorkoutState.plan != null && !activeWorkoutState.isFinished) {
        ActiveWorkoutScreen(
            state = activeWorkoutState,
            onLogSet = { item, setNum, w, r, rpe ->
                workoutViewModel.logSet(item, setNum, w, r, rpe)
            },
            onSkipRest = { workoutViewModel.skipRestTimer() },
            onFinishWorkout = { rpe, feedback ->
                workoutViewModel.finishWorkout(rpe, feedback) {
                    currentTab = NavigationTab.HOME
                }
            },
            onCancel = { workoutViewModel.cancelActiveWorkout() }
        )
        return
    }

    val todayPlan = workoutPlans.firstOrNull { !it.isCompletedToday } ?: workoutPlans.firstOrNull()

    val totalCal = mealsToday.sumOf { it.caloriesKcal }
    val totalP = mealsToday.sumOf { it.proteinG.toDouble() }.toFloat()
    val totalC = mealsToday.sumOf { it.carbsG.toDouble() }.toFloat()
    val totalF = mealsToday.sumOf { it.fatG.toDouble() }.toFloat()

    val nutritionSummary = DailyNutritionSummary(
        dateString = mainViewModel.todayDateStr,
        totalCalories = totalCal,
        totalProtein = totalP,
        totalCarbs = totalC,
        totalFat = totalF,
        calorieTarget = profile.dailyCalorieTarget,
        proteinTarget = profile.dailyProteinTargetG,
        carbsTarget = profile.dailyCarbTargetG,
        fatTarget = profile.dailyFatTargetG,
        meals = mealsToday
    )

    Scaffold(
        containerColor = ObsidianBg,
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                listOf(
                    NavigationTab.HOME,
                    NavigationTab.WORKOUTS,
                    NavigationTab.CALISTHENICS,
                    NavigationTab.NUTRITION,
                    NavigationTab.PROGRESS,
                    NavigationTab.COACH,
                    NavigationTab.PROFILE
                ).forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        modifier = Modifier.testTag(tab.testTag),
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyanAccent,
                            unselectedIconColor = TextSecondary,
                            selectedTextColor = CyanAccent,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CyanAccent.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                NavigationTab.HOME -> HomeScreen(
                    profile = profile,
                    habitLog = todayHabitState,
                    nutritionSummary = nutritionSummary,
                    todayWorkoutPlan = todayPlan,
                    onStartWorkout = { plan -> workoutViewModel.startWorkout(plan) },
                    onAddWater = { mainViewModel.addWater(250) },
                    onToggleMobility = { mainViewModel.toggleMobility() },
                    onNavigateToWorkouts = { currentTab = NavigationTab.WORKOUTS },
                    onNavigateToNutrition = { currentTab = NavigationTab.NUTRITION },
                    onNavigateToCoach = { currentTab = NavigationTab.COACH }
                )
                NavigationTab.WORKOUTS -> {
                    val searchQuery by workoutViewModel.searchQuery.collectAsState()
                    val selectedMuscle by workoutViewModel.selectedMuscleFilter.collectAsState()
                    val selectedEquip by workoutViewModel.selectedEquipmentFilter.collectAsState()

                    WorkoutScreen(
                        workoutPlans = workoutPlans,
                        exercises = filteredExercises,
                        sessionLogs = sessionLogs,
                        searchQuery = searchQuery,
                        onSearchChange = { workoutViewModel.setSearchQuery(it) },
                        selectedMuscle = selectedMuscle,
                        onMuscleSelect = { workoutViewModel.setMuscleFilter(it) },
                        selectedEquip = selectedEquip,
                        onEquipSelect = { workoutViewModel.setEquipmentFilter(it) },
                        onParsePlanItems = { workoutViewModel.parsePlanItems(it) },
                        onStartWorkout = { workoutViewModel.startWorkout(it) }
                    )
                }
                NavigationTab.CALISTHENICS -> CalisthenicsScreen(
                    skills = calisthenicsSkills,
                    onTestSkill = { skill, score, passed, notes ->
                        calisthenicsViewModel.submitSkillTest(skill, score, passed, notes)
                    }
                )
                NavigationTab.NUTRITION -> NutritionScreen(
                    profile = profile,
                    meals = mealsToday,
                    aiSuggestion = nutritionAiSuggestion,
                    isAiLoading = isNutritionAiLoading,
                    onLogMeal = { type, name, portion, cal, p, c, f ->
                        nutritionViewModel.logMeal(type, name, portion, cal, p, c, f)
                    },
                    onDeleteMeal = { nutritionViewModel.deleteMeal(it) },
                    onAskAi = { nutritionViewModel.askNutritionAi(it, profile) },
                    onClearAiSuggestion = { nutritionViewModel.clearAiSuggestion() }
                )
                NavigationTab.PROGRESS -> ProgressScreen(
                    profile = profile,
                    measurements = measurements,
                    prs = prs,
                    achievements = achievements,
                    weeklyReports = weeklyReports,
                    isGeneratingReport = isGeneratingReport,
                    onLogMeasurement = { w, waist, chest, arm, leg, bf, notes ->
                        progressViewModel.logMeasurement(w, waist, chest, arm, leg, bf, notes)
                    },
                    onUpdatePR = { name, valKg, unit, cat ->
                        progressViewModel.updatePR(name, valKg, unit, cat)
                    },
                    onGenerateWeeklyReport = {
                        progressViewModel.generateWeeklyReport(profile, sessionLogs.size, sessionLogs.sumOf { it.totalVolumeKg.toDouble() }.toFloat())
                    }
                )
                NavigationTab.COACH -> CoachScreen(
                    profile = profile,
                    messages = chatMessages,
                    isResponding = isAiResponding,
                    onSendMessage = { aiCoachViewModel.sendMessage(it, profile) }
                )
                NavigationTab.PROFILE -> ProfileScreen(
                    profile = profile,
                    onUpdateProfile = { updated -> mainViewModel.updateProfile(updated) },
                    onRebuildSystem = {
                        mainViewModel.completeOnboarding(profile) {
                            currentTab = NavigationTab.HOME
                        }
                    }
                )
            }
        }
    }
}
