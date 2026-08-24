package com.example.ui.screens.workout

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.MuscleGroup
import com.example.data.model.WorkoutExerciseItem
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionLog
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkoutScreen(
    workoutPlans: List<WorkoutPlan>,
    exercises: List<Exercise>,
    sessionLogs: List<WorkoutSessionLog>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedMuscle: String,
    onMuscleSelect: (String) -> Unit,
    selectedEquip: String,
    onEquipSelect: (String) -> Unit,
    onParsePlanItems: (WorkoutPlan) -> List<WorkoutExerciseItem>,
    onStartWorkout: (WorkoutPlan) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Routines", "Exercise Library", "History")

    var selectedExerciseForDetail by remember { mutableStateOf<Exercise?>(null) }
    var selectedPlanForPreview by remember { mutableStateOf<WorkoutPlan?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = "WORKOUT ENGINE",
                color = CyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Intelligent Training Splits",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ObsidianBg,
            contentColor = CyanAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyanAccent,
                    height = 2.dp
                )
            },
            divider = { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ObsidianBorder)) }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("workout_tab_$index"),
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) CyanAccent else TextSecondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> WorkoutRoutinesTab(
                plans = workoutPlans,
                onParseItems = onParsePlanItems,
                onSelectPlan = { selectedPlanForPreview = it },
                onStartWorkout = onStartWorkout
            )
            1 -> ExerciseLibraryTab(
                exercises = exercises,
                searchQuery = searchQuery,
                onSearchChange = onSearchChange,
                selectedMuscle = selectedMuscle,
                onMuscleSelect = onMuscleSelect,
                selectedEquip = selectedEquip,
                onEquipSelect = onEquipSelect,
                onSelectExercise = { selectedExerciseForDetail = it }
            )
            2 -> WorkoutHistoryTab(sessionLogs = sessionLogs)
        }
    }

    // Exercise Detail Sheet
    if (selectedExerciseForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedExerciseForDetail = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ObsidianSurfaceElevated,
            dragHandle = null
        ) {
            ExerciseDetailContent(
                exercise = selectedExerciseForDetail!!,
                onClose = { selectedExerciseForDetail = null }
            )
        }
    }

    // Plan Preview Sheet
    if (selectedPlanForPreview != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedPlanForPreview = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ObsidianSurfaceElevated,
            dragHandle = null
        ) {
            WorkoutPlanPreviewContent(
                plan = selectedPlanForPreview!!,
                items = onParsePlanItems(selectedPlanForPreview!!),
                onStart = {
                    val p = selectedPlanForPreview!!
                    selectedPlanForPreview = null
                    onStartWorkout(p)
                },
                onClose = { selectedPlanForPreview = null }
            )
        }
    }
}

@Composable
private fun WorkoutRoutinesTab(
    plans: List<WorkoutPlan>,
    onParseItems: (WorkoutPlan) -> List<WorkoutExerciseItem>,
    onSelectPlan: (WorkoutPlan) -> Unit,
    onStartWorkout: (WorkoutPlan) -> Unit
) {
    if (plans.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No routines generated yet.", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(plans) { plan ->
            val items = onParseItems(plan)
            CyberCard(
                borderColor = if (plan.isCompletedToday) EmeraldAccent.copy(alpha = 0.5f) else ObsidianBorder,
                onClick = { onSelectPlan(plan) },
                modifier = Modifier.testTag("workout_plan_item_${plan.id}")
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
                                    text = plan.title,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (plan.isCompletedToday) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = "DONE", color = EmeraldAccent)
                                }
                            }
                            Text(
                                text = plan.subtitle,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        CyberBadge(text = "${plan.estimatedDurationMin}m", color = AmberAccent)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Muscles: ${plan.targetMuscleGroups.replace(",", " • ")}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${items.size} Exercises: " + items.take(3).joinToString(", ") { it.exerciseName } + if (items.size > 3) "..." else "",
                        color = CyanAccent,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onStartWorkout(plan) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF080B11), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "START", color = Color(0xFF080B11), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExerciseLibraryTab(
    exercises: List<Exercise>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedMuscle: String,
    onMuscleSelect: (String) -> Unit,
    selectedEquip: String,
    onEquipSelect: (String) -> Unit,
    onSelectExercise: (Exercise) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search 60+ exercises, calisthenics...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exercise_search_input"),
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

        Spacer(modifier = Modifier.height(10.dp))

        // Muscle filter chips horizontal flow
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL", "CHEST", "BACK", "SHOULDERS", "LEGS", "BICEPS", "TRICEPS", "CORE").forEach { m ->
                CyberChip(
                    text = m,
                    isSelected = selectedMuscle.equals(m, ignoreCase = true),
                    onClick = { onMuscleSelect(m) },
                    accentColor = CyanAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Exercise List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(exercises) { ex ->
                CyberCard(
                    onClick = { onSelectExercise(ex) },
                    modifier = Modifier.testTag("exercise_item_${ex.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ex.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (ex.isCalisthenics) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = "CALISTHENICS", color = EmeraldAccent)
                                }
                            }
                            Text(
                                text = "Target: ${ex.primaryMuscle} • Gear: ${ex.equipmentRequired.replace(",", ", ")}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        CyberBadge(text = ex.difficulty, color = AmberAccent)
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutHistoryTab(sessionLogs: List<WorkoutSessionLog>) {
    if (sessionLogs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "No completed sessions logged yet.", color = TextSecondary, fontSize = 14.sp)
                Text(text = "Complete your first workout to record volume.", color = TextMuted, fontSize = 12.sp)
            }
        }
        return
    }

    val dateFmt = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sessionLogs) { log ->
            CyberCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = log.planTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        CyberBadge(text = "RPE ${log.perceivedExertionRpe}", color = CyanAccent)
                    }
                    Text(
                        text = dateFmt.format(Date(log.dateEpoch)),
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Duration: ${log.durationSeconds / 60} min", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Sets: ${log.totalSetsCompleted}", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Volume: ${log.totalVolumeKg.toInt()} kg", color = EmeraldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseDetailContent(
    exercise: Exercise,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = exercise.name, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Pattern: ${exercise.movementPattern} • Primary: ${exercise.primaryMuscle}", color = CyanAccent, fontSize = 12.sp)
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CyberCard {
            Column {
                Text(text = "TECHNIQUE INSTRUCTIONS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = exercise.instructions, color = TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CyberCard(borderColor = AmberAccent.copy(alpha = 0.5f)) {
            Column {
                Text(text = "COMMON MISTAKES TO AVOID", color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = exercise.commonMistakes, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }

        if (exercise.progression.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            CyberCard(borderColor = EmeraldAccent.copy(alpha = 0.4f)) {
                Column {
                    Text(text = "PROGRESSION PATH", color = EmeraldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = exercise.progression, color = TextSecondary, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WorkoutPlanPreviewContent(
    plan: WorkoutPlan,
    items: List<WorkoutExerciseItem>,
    onStart: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = plan.title, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "${plan.estimatedDurationMin} minutes • ${items.size} exercises", color = CyanAccent, fontSize = 12.sp)
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Warmup Box
        CyberCard(borderColor = AmberAccent.copy(alpha = 0.4f)) {
            Column {
                Text(text = "WARMUP GUIDANCE", color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = plan.warmupGuidance, color = TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "EXERCISE LINEUP", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        items.forEachIndexed { idx, item ->
            CyberCard(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${idx + 1}. ${item.exerciseName}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${item.sets} sets x ${item.repsTarget} • Rest: ${item.restSec}s • ${item.tempo}", color = TextSecondary, fontSize = 11.sp)
                    }
                    CyberBadge(text = item.rpeTarget, color = CyanAccent)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF080B11))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "START SESSION NOW", color = Color(0xFF080B11), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}
