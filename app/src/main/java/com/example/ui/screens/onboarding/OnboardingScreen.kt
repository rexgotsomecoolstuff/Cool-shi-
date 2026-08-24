package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EquipmentType
import com.example.data.model.FitnessLevel
import com.example.data.model.MuscleGroup
import com.example.data.model.PhysiqueGoal
import com.example.data.model.Timeframe
import com.example.data.model.TrainingEnvironment
import com.example.data.model.TrainingPreference
import com.example.data.model.UserProfile
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberChip
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    initialProfile: UserProfile,
    isBuildingSystem: Boolean,
    onComplete: (UserProfile) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 8

    // Form state
    var name by remember { mutableStateOf(initialProfile.name) }
    var ageText by remember { mutableStateOf(initialProfile.age.toString()) }
    var sex by remember { mutableStateOf(initialProfile.sex) }
    var heightText by remember { mutableStateOf(initialProfile.heightCm.toInt().toString()) }
    var currentWeightText by remember { mutableStateOf(initialProfile.currentWeightKg.toInt().toString()) }
    var targetWeightText by remember { mutableStateOf(initialProfile.targetWeightKg?.toInt()?.toString() ?: "") }
    var fitnessLevel by remember { mutableStateOf(FitnessLevel.INTERMEDIATE) }

    // Body Measurements
    var waistText by remember { mutableStateOf(initialProfile.waistCm?.toInt()?.toString() ?: "") }
    var chestText by remember { mutableStateOf(initialProfile.chestCm?.toInt()?.toString() ?: "") }
    var armText by remember { mutableStateOf(initialProfile.armCm?.toInt()?.toString() ?: "") }
    var legText by remember { mutableStateOf(initialProfile.legCm?.toInt()?.toString() ?: "") }
    var bodyFatText by remember { mutableStateOf(initialProfile.bodyFatEstimate?.toInt()?.toString() ?: "") }

    // Goals & Priorities
    var primaryGoal by remember { mutableStateOf(PhysiqueGoal.AESTHETIC) }
    var secondaryGoal by remember { mutableStateOf<PhysiqueGoal?>(PhysiqueGoal.CALISTHENICS) }
    var selectedMuscles by remember { mutableStateOf(setOf("UPPER_CHEST", "LATERAL_DELTS", "LATS", "ARMS")) }

    // Timeframe
    var selectedTimeframe by remember { mutableStateOf(Timeframe.WEEKS_12) }

    // Environment & Equipment
    var trainingEnv by remember { mutableStateOf(TrainingEnvironment.FULL_GYM) }
    var selectedEquipment by remember {
        mutableStateOf(
            setOf(
                EquipmentType.BARBELL.name,
                EquipmentType.DUMBBELLS.name,
                EquipmentType.PULL_UP_BAR.name,
                EquipmentType.DIP_BARS.name,
                EquipmentType.GYM_MACHINES.name,
                EquipmentType.BENCH.name
            )
        )
    }

    // Training Preferences & Schedule
    var selectedPreferences by remember {
        mutableStateOf(setOf(TrainingPreference.HYPERTROPHY.name, TrainingPreference.CALISTHENICS.name, TrainingPreference.STRENGTH_TRAINING.name))
    }
    var daysPerWeek by remember { mutableIntStateOf(4) }
    var sessionDurationMin by remember { mutableIntStateOf(45) }
    var dietaryRestrictions by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }

    if (isBuildingSystem) {
        AiSystemBuildingView()
        return
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        IconButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.testTag("onboarding_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextSecondary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ORAXIS PHYSIC",
                            color = CyanAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Step ${currentStep + 1} of $totalSteps",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Step Progress Line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 0 until totalSteps) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (i <= currentStep) CyanAccent else ObsidianBorder
                                )
                        )
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            currentStep++
                        } else {
                            val finalProfile = initialProfile.copy(
                                name = name.ifBlank { "Athlete" },
                                age = ageText.toIntOrNull() ?: 25,
                                sex = sex,
                                heightCm = heightText.toFloatOrNull() ?: 178f,
                                currentWeightKg = currentWeightText.toFloatOrNull() ?: 75f,
                                targetWeightKg = targetWeightText.toFloatOrNull(),
                                fitnessLevel = fitnessLevel.name,
                                waistCm = waistText.toFloatOrNull(),
                                chestCm = chestText.toFloatOrNull(),
                                armCm = armText.toFloatOrNull(),
                                legCm = legText.toFloatOrNull(),
                                bodyFatEstimate = bodyFatText.toFloatOrNull(),
                                primaryGoal = primaryGoal.name,
                                secondaryGoal = secondaryGoal?.name,
                                priorityMuscles = selectedMuscles.joinToString(","),
                                timeframe = selectedTimeframe.name,
                                trainingEnvironment = trainingEnv.name,
                                availableEquipment = selectedEquipment.joinToString(","),
                                trainingPreferences = selectedPreferences.joinToString(","),
                                daysPerWeek = daysPerWeek,
                                sessionDurationMin = sessionDurationMin,
                                dietaryRestrictions = dietaryRestrictions,
                                allergies = allergies
                            )
                            onComplete(finalProfile)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("onboarding_continue_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (currentStep == totalSteps - 1) "BUILD MY ORAXIS SYSTEM" else "CONTINUE",
                        color = Color(0xFF080B11),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (currentStep == totalSteps - 1) Icons.Default.AutoAwesome else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF080B11),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "step_content"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 12.dp)
                ) {
                    when (step) {
                        0 -> StepBasicInfo(
                            name = name, onNameChange = { name = it },
                            age = ageText, onAgeChange = { ageText = it },
                            sex = sex, onSexChange = { sex = it },
                            height = heightText, onHeightChange = { heightText = it },
                            weight = currentWeightText, onWeightChange = { currentWeightText = it },
                            targetWeight = targetWeightText, onTargetWeightChange = { targetWeightText = it },
                            fitnessLevel = fitnessLevel, onFitnessLevelChange = { fitnessLevel = it }
                        )
                        1 -> StepBodyMeasurements(
                            waist = waistText, onWaistChange = { waistText = it },
                            chest = chestText, onChestChange = { chestText = it },
                            arm = armText, onArmChange = { armText = it },
                            leg = legText, onLegChange = { legText = it },
                            bodyFat = bodyFatText, onBodyFatChange = { bodyFatText = it }
                        )
                        2 -> StepPhysiqueGoal(
                            primary = primaryGoal, onPrimaryChange = { primaryGoal = it },
                            secondary = secondaryGoal, onSecondaryChange = { secondaryGoal = it }
                        )
                        3 -> StepPriorityMuscles(
                            selected = selectedMuscles,
                            onToggle = { muscle ->
                                selectedMuscles = if (selectedMuscles.contains(muscle)) {
                                    selectedMuscles - muscle
                                } else {
                                    selectedMuscles + muscle
                                }
                            }
                        )
                        4 -> StepTimeframe(
                            selected = selectedTimeframe,
                            onSelect = { selectedTimeframe = it }
                        )
                        5 -> StepEnvironmentAndEquipment(
                            env = trainingEnv, onEnvChange = { trainingEnv = it },
                            selectedEquipment = selectedEquipment,
                            onToggleEquip = { eq ->
                                selectedEquipment = if (selectedEquipment.contains(eq)) {
                                    selectedEquipment - eq
                                } else {
                                    selectedEquipment + eq
                                }
                            }
                        )
                        6 -> StepTrainingPreferences(
                            selected = selectedPreferences,
                            onToggle = { pref ->
                                selectedPreferences = if (selectedPreferences.contains(pref)) {
                                    selectedPreferences - pref
                                } else {
                                    selectedPreferences + pref
                                }
                            }
                        )
                        7 -> StepScheduleAndNutrition(
                            days = daysPerWeek, onDaysChange = { daysPerWeek = it },
                            duration = sessionDurationMin, onDurationChange = { sessionDurationMin = it },
                            diet = dietaryRestrictions, onDietChange = { dietaryRestrictions = it },
                            allergies = allergies, onAllergiesChange = { allergies = it }
                        )
                    }
                }
            }
        }
    }
}

// ---------------- SUB STEPS ----------------

@Composable
private fun StepBasicInfo(
    name: String, onNameChange: (String) -> Unit,
    age: String, onAgeChange: (String) -> Unit,
    sex: String, onSexChange: (String) -> Unit,
    height: String, onHeightChange: (String) -> Unit,
    weight: String, onWeightChange: (String) -> Unit,
    targetWeight: String, onTargetWeightChange: (String) -> Unit,
    fitnessLevel: FitnessLevel, onFitnessLevelChange: (FitnessLevel) -> Unit
) {
    Text(
        text = "Your Identity & Baseline",
        color = TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "ORAXIS builds your bespoke system from raw biometric parameters.",
        color = TextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    CyberTextField(value = name, onValueChange = onNameChange, label = "Full Name / Alias", testTag = "input_name")
    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CyberTextField(
            value = age, onValueChange = onAgeChange, label = "Age",
            modifier = Modifier.weight(1f), testTag = "input_age"
        )
        // Sex Selector
        Row(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                .background(ObsidianSurfaceElevated),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Male", "Female").forEach { s ->
                val isSel = sex.equals(s, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(if (isSel) CyanAccent.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onSexChange(s) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = s,
                        color = if (isSel) CyanAccent else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CyberTextField(
            value = height, onValueChange = onHeightChange, label = "Height (cm)",
            modifier = Modifier.weight(1f), testTag = "input_height"
        )
        CyberTextField(
            value = weight, onValueChange = onWeightChange, label = "Current Wt (kg)",
            modifier = Modifier.weight(1f), testTag = "input_weight"
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
    CyberTextField(
        value = targetWeight, onValueChange = onTargetWeightChange,
        label = "Target Weight (kg) - Optional", testTag = "input_target_weight"
    )

    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "Current Fitness Level",
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))

    FitnessLevel.values().forEach { level ->
        val isSelected = fitnessLevel == level
        CyberCard(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .testTag("fitness_level_${level.name.lowercase()}"),
            borderColor = if (isSelected) CyanAccent else ObsidianBorder,
            onClick = { onFitnessLevelChange(level) }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = level.title, color = if (isSelected) CyanAccent else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = level.description, color = TextSecondary, fontSize = 12.sp)
                }
                if (isSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyanAccent)
                }
            }
        }
    }
}

@Composable
private fun StepBodyMeasurements(
    waist: String, onWaistChange: (String) -> Unit,
    chest: String, onChestChange: (String) -> Unit,
    arm: String, onArmChange: (String) -> Unit,
    leg: String, onLegChange: (String) -> Unit,
    bodyFat: String, onBodyFatChange: (String) -> Unit
) {
    Text(text = "Body Circumference", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(
        text = "Optional precision metrics. Skip any measurement you do not currently have. Note: Body fat and measurements are tracked as estimates, not perfect biological predictors.",
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CyberTextField(value = waist, onValueChange = onWaistChange, label = "Waist (cm)", modifier = Modifier.weight(1f))
        CyberTextField(value = chest, onValueChange = onChestChange, label = "Chest (cm)", modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CyberTextField(value = arm, onValueChange = onArmChange, label = "Arm (cm)", modifier = Modifier.weight(1f))
        CyberTextField(value = leg, onValueChange = onLegChange, label = "Thigh (cm)", modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(12.dp))
    CyberTextField(value = bodyFat, onValueChange = onBodyFatChange, label = "Estimated Body Fat % (Optional)")
}

@Composable
private fun StepPhysiqueGoal(
    primary: PhysiqueGoal,
    onPrimaryChange: (PhysiqueGoal) -> Unit,
    secondary: PhysiqueGoal?,
    onSecondaryChange: (PhysiqueGoal?) -> Unit
) {
    Text(text = "Physique Vision", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(
        text = "Select your primary architectural focus. You can combine with a complementary focus (e.g. Aesthetic + Calisthenics).",
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    PhysiqueGoal.values().forEach { goal ->
        val isPrimary = primary == goal
        val isSecondary = secondary == goal
        val isSelected = isPrimary || isSecondary

        CyberCard(
            modifier = Modifier.padding(vertical = 4.dp),
            borderColor = when {
                isPrimary -> CyanAccent
                isSecondary -> AmberAccent
                else -> ObsidianBorder
            },
            onClick = {
                if (isPrimary) {
                    // keep
                } else if (isSecondary) {
                    onSecondaryChange(null)
                } else {
                    if (secondary == null) {
                        onSecondaryChange(goal)
                    } else {
                        onPrimaryChange(goal)
                    }
                }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = goal.title,
                            color = when {
                                isPrimary -> CyanAccent
                                isSecondary -> AmberAccent
                                else -> TextPrimary
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (isPrimary) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CyberBadge(text = "Primary", color = CyanAccent)
                        } else if (isSecondary) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CyberBadge(text = "Secondary", color = AmberAccent)
                        }
                    }
                    Text(text = goal.subtitle, color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepPriorityMuscles(
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Text(text = "Priority Hypertrophy Areas", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(
        text = "Which anatomical muscle groups do you want ORAXIS to emphasize in volume allocation?",
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MuscleGroup.values().forEach { m ->
            val isSel = selected.contains(m.name)
            CyberChip(
                text = m.title,
                isSelected = isSel,
                onClick = { onToggle(m.name) },
                accentColor = CyanAccent
            )
        }
    }
}

@Composable
private fun StepTimeframe(
    selected: Timeframe,
    onSelect: (Timeframe) -> Unit
) {
    Text(text = "Target Timeframe & Milestones", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(
        text = "Real results require honest physiological timelines. Select your macrocycle duration.",
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    Timeframe.values().forEach { tf ->
        val isSel = selected == tf
        CyberCard(
            modifier = Modifier.padding(vertical = 4.dp),
            borderColor = if (isSel) CyanAccent else ObsidianBorder,
            onClick = { onSelect(tf) }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tf.label, color = if (isSel) CyanAccent else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = tf.description, color = TextSecondary, fontSize = 12.sp)
                }
                if (isSel) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyanAccent)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    CyberCard(borderColor = EmeraldAccent.copy(alpha = 0.5f)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Estimated Progress Window", color = EmeraldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• Noticeable strength jumps: approx 2–4 weeks\n• Visible physique changes: approx 6–12 weeks\n• Long-term physical transformation: approx 6–12+ months\n\nResults depend on sleep, nutrition, genetics, consistency, and safe progression.",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepEnvironmentAndEquipment(
    env: TrainingEnvironment,
    onEnvChange: (TrainingEnvironment) -> Unit,
    selectedEquipment: Set<String>,
    onToggleEquip: (String) -> Unit
) {
    Text(text = "Training Environment & Gear", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(
        text = "ORAXIS strictly programs exercises using only the equipment you select.",
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    Text(text = "Primary Environment", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(6.dp))

    TrainingEnvironment.values().forEach { e ->
        val isSel = env == e
        CyberCard(
            modifier = Modifier.padding(vertical = 3.dp),
            borderColor = if (isSel) CyanAccent else ObsidianBorder,
            onClick = { onEnvChange(e) }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = e.title, color = if (isSel) CyanAccent else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                if (isSel) Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyanAccent)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Available Equipment", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EquipmentType.values().forEach { eq ->
            val isSel = selectedEquipment.contains(eq.name)
            CyberChip(
                text = eq.displayName,
                isSelected = isSel,
                onClick = { onToggleEquip(eq.name) },
                accentColor = CyanAccent
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepTrainingPreferences(
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Text(text = "Training Modalities You Enjoy", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(
        text = "The AI shapes your periodization around what you love while keeping your training balanced.",
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TrainingPreference.values().forEach { pref ->
            val isSel = selected.contains(pref.name)
            CyberChip(
                text = pref.title,
                isSelected = isSel,
                onClick = { onToggle(pref.name) },
                accentColor = AmberAccent
            )
        }
    }
}

@Composable
private fun StepScheduleAndNutrition(
    days: Int, onDaysChange: (Int) -> Unit,
    duration: Int, onDurationChange: (Int) -> Unit,
    diet: String, onDietChange: (String) -> Unit,
    allergies: String, onAllergiesChange: (String) -> Unit
) {
    Text(text = "Schedule & Nutrition", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(
        text = "Configure your weekly rhythm and nutrition safety constraints.",
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    Text(text = "Training Days Per Week: $days days", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(2, 3, 4, 5, 6).forEach { d ->
            val isSel = days == d
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSel) CyanAccent else ObsidianSurfaceElevated)
                    .border(1.dp, if (isSel) CyanAccent else ObsidianBorder, RoundedCornerShape(10.dp))
                    .clickable { onDaysChange(d) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$d d",
                    color = if (isSel) Color(0xFF080B11) else TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Target Session Duration: $duration min", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(15, 30, 45, 60, 75).forEach { dur ->
            val isSel = duration == dur
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSel) AmberAccent else ObsidianSurfaceElevated)
                    .border(1.dp, if (isSel) AmberAccent else ObsidianBorder, RoundedCornerShape(10.dp))
                    .clickable { onDurationChange(dur) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$dur m",
                    color = if (isSel) Color(0xFF080B11) else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
    CyberTextField(value = diet, onValueChange = onDietChange, label = "Dietary Preferences (e.g. Vegetarian, High Protein)")
    Spacer(modifier = Modifier.height(12.dp))
    CyberTextField(value = allergies, onValueChange = onAllergiesChange, label = "Allergies (e.g. Peanuts, Lactose, Shellfish)")
}

@Composable
private fun AiSystemBuildingView() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "rot"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .border(3.dp, Brush.sweepGradient(listOf(CyanAccent, AmberAccent, EmeraldAccent, CyanAccent)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "ORAXIS AI IS BUILDING YOUR SYSTEM",
                color = CyanAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Synthesizing bio-mechanics, progressive overload curves, TDEE caloric partition, and calisthenics skill trees...",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun CyberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextMuted, fontSize = 13.sp) },
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanAccent,
            unfocusedBorderColor = ObsidianBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = ObsidianSurfaceElevated,
            unfocusedContainerColor = ObsidianSurfaceElevated
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}
