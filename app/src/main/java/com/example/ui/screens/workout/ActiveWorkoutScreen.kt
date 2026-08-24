package com.example.ui.screens.workout

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkoutExerciseItem
import com.example.data.model.WorkoutPlan
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ActiveWorkoutState

@Composable
fun ActiveWorkoutScreen(
    state: ActiveWorkoutState,
    onLogSet: (WorkoutExerciseItem, Int, Float, Int, Float) -> Unit,
    onSkipRest: () -> Unit,
    onFinishWorkout: (rpe: Int, feedback: String) -> Unit,
    onCancel: () -> Unit
) {
    val plan = state.plan ?: return
    var showFinishDialog by remember { mutableStateOf(false) }
    var showCancelConfirmDialog by remember { mutableStateOf(false) }

    // Map of exerciseIndex_setNumber -> logged status
    val setWeightMap = remember { mutableStateMapOf<String, String>() }
    val setRepsMap = remember { mutableStateMapOf<String, String>() }

    val minutes = state.elapsedSeconds / 60
    val seconds = state.elapsedSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

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
                    IconButton(onClick = { showCancelConfirmDialog = true }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = TextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = plan.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = formattedTime, color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = { showFinishDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("finish_workout_button")
                    ) {
                        Text(text = "FINISH", color = Color(0xFF080B11), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(state.items) { exIdx, exItem ->
                    CyberCard(
                        borderColor = ObsidianBorder
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${exIdx + 1}. ${exItem.exerciseName}",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${exItem.sets} Sets x ${exItem.repsTarget} • Tempo: ${exItem.tempo}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                CyberBadge(text = exItem.rpeTarget, color = CyanAccent)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Set rows
                            for (setNum in 1..exItem.sets) {
                                val setKey = "${exIdx}_${setNum}"
                                val isDone = state.completedSetLogs.any {
                                    it.exerciseId == exItem.exerciseId && it.setNumber == setNum
                                }

                                val weightVal = setWeightMap[setKey] ?: (if (exItem.weightKg > 0) exItem.weightKg.toString() else "0")
                                val repsVal = setRepsMap[setKey] ?: exItem.repsTarget.filter { it.isDigit() }.take(2).ifEmpty { "10" }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "Set $setNum", color = TextMuted, fontSize = 12.sp, modifier = Modifier.width(40.dp))

                                    // Weight input
                                    OutlinedTextField(
                                        value = weightVal,
                                        onValueChange = { setWeightMap[setKey] = it },
                                        label = { Text("kg", fontSize = 10.sp) },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = CyanAccent,
                                            unfocusedBorderColor = ObsidianBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedContainerColor = ObsidianSurfaceElevated,
                                            unfocusedContainerColor = ObsidianSurfaceElevated
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )

                                    // Reps input
                                    OutlinedTextField(
                                        value = repsVal,
                                        onValueChange = { setRepsMap[setKey] = it },
                                        label = { Text("reps", fontSize = 10.sp) },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = CyanAccent,
                                            unfocusedBorderColor = ObsidianBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedContainerColor = ObsidianSurfaceElevated,
                                            unfocusedContainerColor = ObsidianSurfaceElevated
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )

                                    // Checkmark Log Button
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isDone) EmeraldAccent else ObsidianSurfaceElevated)
                                            .border(1.dp, if (isDone) EmeraldAccent else ObsidianBorder, RoundedCornerShape(8.dp))
                                            .clickable {
                                                val w = weightVal.toFloatOrNull() ?: 0f
                                                val r = repsVal.toIntOrNull() ?: 10
                                                onLogSet(exItem, setNum, w, r, 8f)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Log Set",
                                            tint = if (isDone) Color(0xFF080B11) else TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Rest Timer Floating Banner
            AnimatedVisibility(
                visible = state.isRestTimerActive,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressMeter(
                                progress = state.restTimerRemainingSeconds / 90f,
                                size = 44.dp,
                                strokeWidth = 4.dp,
                                color = CyanAccent
                            ) {
                                Text(
                                    text = "${state.restTimerRemainingSeconds}s",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Rest Interval", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Catch breath & hydrate", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = onSkipRest,
                            colors = ButtonDefaults.buttonColors(containerColor = ObsidianBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(text = "SKIP", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Finish Workout Dialog
    if (showFinishDialog) {
        var rpeRating by remember { mutableFloatStateOf(8f) }
        var feedbackChoice by remember { mutableStateOf("SOLID") }

        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            containerColor = ObsidianSurfaceElevated,
            title = { Text(text = "Complete Workout", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "How intense was today's training session?",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Rate Session RPE: ${rpeRating.toInt()}/10", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Slider(
                        value = rpeRating,
                        onValueChange = { rpeRating = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Session Feedback", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("EASY" to "Easy", "SOLID" to "Solid", "STRUGGLED" to "Heavy").forEach { (code, label) ->
                            val isSel = feedbackChoice == code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanAccent else ObsidianBorder)
                                    .clickable { feedbackChoice = code },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color(0xFF080B11) else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        onFinishWorkout(rpeRating.toInt(), feedbackChoice)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "SAVE & LOG SESSION", color = Color(0xFF080B11), fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text(text = "CANCEL", color = TextSecondary)
                }
            }
        )
    }

    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            containerColor = ObsidianSurfaceElevated,
            title = { Text(text = "Discard Active Workout?", color = TextPrimary) },
            text = { Text(text = "Your current session timer and unsaved sets will be discarded.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelConfirmDialog = false
                        onCancel()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(text = "DISCARD", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmDialog = false }) {
                    Text(text = "RESUME", color = CyanAccent)
                }
            }
        )
    }
}
