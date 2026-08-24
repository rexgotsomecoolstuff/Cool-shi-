package com.example.ui.screens.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyHabitLog
import com.example.data.model.DailyNutritionSummary
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutPlan
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.MacroProgressBar
import com.example.ui.components.StatMetricTile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun HomeScreen(
    profile: UserProfile,
    habitLog: DailyHabitLog?,
    nutritionSummary: DailyNutritionSummary?,
    todayWorkoutPlan: WorkoutPlan?,
    onStartWorkout: (WorkoutPlan) -> Unit,
    onAddWater: () -> Unit,
    onToggleMobility: () -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToCoach: () -> Unit
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    val habit = habitLog ?: DailyHabitLog("")
    val waterTarget = profile.dailyWaterTargetMl.coerceAtLeast(2000)
    val waterPercent = ((habit.waterIntakeMl.toFloat() / waterTarget) * 100).toInt().coerceIn(0, 100)
    val sleepTarget = profile.dailySleepTargetHours.coerceAtLeast(7f)
    val sleepPercent = ((habit.sleepHours / sleepTarget) * 100).toInt().coerceIn(0, 100)

    val caloriesLogged = nutritionSummary?.totalCalories ?: 0
    val calorieTarget = profile.dailyCalorieTarget.coerceAtLeast(1500)
    val caloriePercent = ((caloriesLogged.toFloat() / calorieTarget) * 100).toInt().coerceIn(0, 100)

    // Calculate Overall System Completion Score
    val workoutDone = if (todayWorkoutPlan?.isCompletedToday == true || habit.workoutCompleted) 1f else 0f
    val waterScore = (waterPercent / 100f).coerceIn(0f, 1f)
    val sleepScore = (sleepPercent / 100f).coerceIn(0f, 1f)
    val nutritionScore = (caloriePercent / 100f).coerceIn(0f, 1f)
    val mobilityDone = if (habit.mobilityCompleted) 1f else 0f

    val totalSystemScore = ((workoutDone * 0.35f + nutritionScore * 0.25f + waterScore * 0.15f + sleepScore * 0.15f + mobilityDone * 0.10f) * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$greeting, ${profile.name}",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "“Your body. Your goal. Your system.”",
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Streak Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AmberContainer)
                    .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = AmberAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.streakDays}d Streak",
                        color = AmberAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // System Score Card
        CyberCard(
            borderColor = CyanAccent.copy(alpha = 0.4f),
            modifier = Modifier.testTag("dashboard_score_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TODAY'S SYSTEM SCORE",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$totalSystemScore%",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Current Goal: ${profile.primaryGoal.replace("_", " ")}${if (profile.secondaryGoal != null) " + " + profile.secondaryGoal.replace("_", " ") else ""}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                CircularProgressMeter(
                    progress = totalSystemScore / 100f,
                    size = 76.dp,
                    strokeWidth = 7.dp,
                    color = if (totalSystemScore > 75) EmeraldAccent else CyanAccent
                ) {
                    Icon(
                        imageVector = if (totalSystemScore > 75) Icons.Default.Check else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (totalSystemScore > 75) EmeraldAccent else CyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Today's Workout Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TODAY'S WORKOUT",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "View Schedule",
                color = CyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onNavigateToWorkouts() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (todayWorkoutPlan != null) {
            CyberCard(
                borderColor = if (todayWorkoutPlan.isCompletedToday) EmeraldAccent else AmberAccent.copy(alpha = 0.5f),
                modifier = Modifier.testTag("today_workout_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = todayWorkoutPlan.title,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (todayWorkoutPlan.isCompletedToday) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CyberBadge(text = "COMPLETED", color = EmeraldAccent)
                                }
                            }
                            Text(
                                text = todayWorkoutPlan.subtitle,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        CyberBadge(
                            text = "${todayWorkoutPlan.estimatedDurationMin}m",
                            color = AmberAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Focus: ${todayWorkoutPlan.targetMuscleGroups.replace(",", " • ")}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onStartWorkout(todayWorkoutPlan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("start_workout_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (todayWorkoutPlan.isCompletedToday) ObsidianSurfaceElevated else AmberAccent
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (todayWorkoutPlan.isCompletedToday) Icons.Default.Check else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (todayWorkoutPlan.isCompletedToday) EmeraldAccent else Color(0xFF080B11),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (todayWorkoutPlan.isCompletedToday) "RE-OPEN SESSION" else "START WORKOUT",
                            color = if (todayWorkoutPlan.isCompletedToday) TextPrimary else Color(0xFF080B11),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            CyberCard(onClick = onNavigateToWorkouts) {
                Text(
                    text = "Rest or Active Recovery Day. Tap to view full routine.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Habits Checklist
        Text(
            text = "DAILY HABIT SYSTEM",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Hydration Card
            CyberCard(
                modifier = Modifier
                    .weight(1f)
                    .testTag("habit_water_card"),
                onClick = onAddWater
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add water", tint = CyanAccent, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${habit.waterIntakeMl} ml", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Water / ${waterTarget}ml", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (habit.waterIntakeMl.toFloat() / waterTarget).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = CyanAccent,
                        trackColor = ObsidianBorder
                    )
                }
            }

            // Mobility Card
            CyberCard(
                modifier = Modifier
                    .weight(1f)
                    .testTag("habit_mobility_card"),
                borderColor = if (habit.mobilityCompleted) EmeraldAccent else ObsidianBorder,
                onClick = onToggleMobility
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.SelfImprovement, contentDescription = null, tint = if (habit.mobilityCompleted) EmeraldAccent else PurpleAccent, modifier = Modifier.size(20.dp))
                        if (habit.mobilityCompleted) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (habit.mobilityCompleted) "Completed" else "Pending",
                        color = if (habit.mobilityCompleted) EmeraldAccent else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(text = "Mobility / 10 min", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { if (habit.mobilityCompleted) 1f else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = EmeraldAccent,
                        trackColor = ObsidianBorder
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nutrition Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NUTRITION & MACROS",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Food Log",
                color = CyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onNavigateToNutrition() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        CyberCard(
            onClick = onNavigateToNutrition,
            modifier = Modifier.testTag("home_nutrition_card")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${nutritionSummary?.totalCalories ?: 0} / ${profile.dailyCalorieTarget} kcal",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(text = "Target Energy", color = TextMuted, fontSize = 11.sp)
                    }
                    CyberBadge(text = "${caloriePercent}%", color = CyanAccent)
                }
                Spacer(modifier = Modifier.height(14.dp))

                MacroProgressBar(
                    label = "Protein",
                    current = nutritionSummary?.totalProtein ?: 0f,
                    target = profile.dailyProteinTargetG.toFloat(),
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                MacroProgressBar(
                    label = "Carbohydrates",
                    current = nutritionSummary?.totalCarbs ?: 0f,
                    target = profile.dailyCarbTargetG.toFloat(),
                    color = AmberAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                MacroProgressBar(
                    label = "Fats",
                    current = nutritionSummary?.totalFat ?: 0f,
                    target = profile.dailyFatTargetG.toFloat(),
                    color = EmeraldAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ORAXIS AI Coach Quick Banner
        CyberCard(
            borderColor = PurpleAccent.copy(alpha = 0.5f),
            onClick = onNavigateToCoach,
            modifier = Modifier.testTag("home_ai_coach_card")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PurpleContainer)
                        .border(1.dp, PurpleAccent.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleAccent)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "ORAXIS AI Coach", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Ask for exercise substitutions, high protein meals, or recovery reviews.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
