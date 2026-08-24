package com.example.ui.screens.nutrition

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.MealLog
import com.example.data.model.MealType
import com.example.data.model.UserProfile
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberChip
import com.example.ui.components.MacroProgressBar
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NutritionScreen(
    profile: UserProfile,
    meals: List<MealLog>,
    aiSuggestion: String?,
    isAiLoading: Boolean,
    onLogMeal: (MealType, String, String, Int, Float, Float, Float) -> Unit,
    onDeleteMeal: (Long) -> Unit,
    onAskAi: (String) -> Unit,
    onClearAiSuggestion: () -> Unit
) {
    var showAddMealDialog by remember { mutableStateOf(false) }
    var selectedMealTypeForAdd by remember { mutableStateOf(MealType.BREAKFAST) }

    val totalCalories = meals.sumOf { it.caloriesKcal }
    val totalProtein = meals.sumOf { it.proteinG.toDouble() }.toFloat()
    val totalCarbs = meals.sumOf { it.carbsG.toDouble() }.toFloat()
    val totalFat = meals.sumOf { it.fatG.toDouble() }.toFloat()

    val calorieTarget = profile.dailyCalorieTarget.coerceAtLeast(1500)
    val calRatio = (totalCalories.toFloat() / calorieTarget).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NUTRITION SYSTEM",
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Macro Fuel & Energy",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { showAddMealDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("add_food_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF080B11), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "LOG FOOD", color = Color(0xFF080B11), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Main Macro Target Card
            item {
                CyberCard(
                    borderColor = CyanAccent.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("nutrition_summary_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "CALORIE ENERGY TARGET", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "$totalCalories / $calorieTarget kcal",
                                    color = TextPrimary,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            CircularProgressMeter(
                                progress = calRatio,
                                size = 64.dp,
                                strokeWidth = 6.dp,
                                color = CyanAccent
                            ) {
                                Text(
                                    text = "${(calRatio * 100).toInt()}%",
                                    color = CyanAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        MacroProgressBar(
                            label = "Protein (${(totalProtein / profile.dailyProteinTargetG * 100).toInt()}%)",
                            current = totalProtein,
                            target = profile.dailyProteinTargetG.toFloat(),
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MacroProgressBar(
                            label = "Carbohydrates (${(totalCarbs / profile.dailyCarbTargetG * 100).toInt()}%)",
                            current = totalCarbs,
                            target = profile.dailyCarbTargetG.toFloat(),
                            color = AmberAccent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MacroProgressBar(
                            label = "Fats (${(totalFat / profile.dailyFatTargetG * 100).toInt()}%)",
                            current = totalFat,
                            target = profile.dailyFatTargetG.toFloat(),
                            color = EmeraldAccent
                        )
                    }
                }
            }

            // AI Meal Suggestion Box
            item {
                CyberCard(
                    borderColor = PurpleAccent.copy(alpha = 0.4f)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "ORAXIS AI NUTRITION ASSISTANT", color = PurpleAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            if (aiSuggestion != null) {
                                Text(
                                    text = "Dismiss",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    modifier = Modifier.clickable { onClearAiSuggestion() }
                                )
                            }
                        }

                        if (isAiLoading) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PurpleAccent, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Synthesizing macro meal strategy...", color = TextSecondary, fontSize = 12.sp)
                            }
                        } else if (aiSuggestion != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = aiSuggestion, color = TextPrimary, fontSize = 12.sp, lineHeight = 17.sp)
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Ask AI to generate meals matching your macro partition:", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "High protein breakfast",
                                    "Quick 30g protein snack",
                                    "Post-workout meal"
                                ).forEach { prompt ->
                                    CyberChip(
                                        text = prompt,
                                        isSelected = false,
                                        onClick = { onAskAi(prompt) },
                                        accentColor = PurpleAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Meal Categories and Logs
            item {
                Text(
                    text = "TODAY'S LOGGED MEALS",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (meals.isEmpty()) {
                item {
                    CyberCard {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No meals recorded today yet. Tap 'Log Food' to track.", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(meals) { meal ->
                    CyberCard(
                        modifier = Modifier.testTag("logged_meal_${meal.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = meal.foodName,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = meal.mealType, color = AmberAccent)
                                }
                                Text(
                                    text = "${meal.portionDescription} • ${meal.caloriesKcal} kcal",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "P: ${meal.proteinG}g | C: ${meal.carbsG}g | F: ${meal.fatG}g",
                                    color = CyanAccent,
                                    fontSize = 11.sp
                                )
                            }

                            IconButton(onClick = { onDeleteMeal(meal.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }

    // Add Meal Dialog
    if (showAddMealDialog) {
        var foodName by remember { mutableStateOf("") }
        var portion by remember { mutableStateOf("1 serving") }
        var caloriesText by remember { mutableStateOf("350") }
        var proteinText by remember { mutableStateOf("30") }
        var carbsText by remember { mutableStateOf("25") }
        var fatText by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showAddMealDialog = false },
            containerColor = ObsidianSurfaceElevated,
            title = { Text(text = "Log Meal / Food", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    // Meal Type Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER, MealType.SNACK).forEach { mt ->
                            val isSel = selectedMealTypeForAdd == mt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) CyanAccent else ObsidianBorder)
                                    .clickable { selectedMealTypeForAdd = mt },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mt.name.take(4),
                                    color = if (isSel) Color(0xFF080B11) else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food / Meal Name (e.g., Grilled Chicken & Rice)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = caloriesText,
                            onValueChange = { caloriesText = it },
                            label = { Text("Calories (kcal)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = proteinText,
                            onValueChange = { proteinText = it },
                            label = { Text("Protein (g)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = carbsText,
                            onValueChange = { carbsText = it },
                            label = { Text("Carbs (g)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = fatText,
                            onValueChange = { fatText = it },
                            label = { Text("Fat (g)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = foodName.ifBlank { "Logged Meal" }
                        val cal = caloriesText.toIntOrNull() ?: 300
                        val p = proteinText.toFloatOrNull() ?: 20f
                        val c = carbsText.toFloatOrNull() ?: 30f
                        val f = fatText.toFloatOrNull() ?: 10f

                        onLogMeal(selectedMealTypeForAdd, name, portion, cal, p, c, f)
                        showAddMealDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "ADD MEAL", color = Color(0xFF080B11), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMealDialog = false }) {
                    Text(text = "CANCEL", color = TextSecondary)
                }
            }
        )
    }
}
