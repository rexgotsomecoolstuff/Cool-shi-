package com.example.ui.screens.progress

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Achievement
import com.example.data.model.BodyMeasurement
import com.example.data.model.PerformancePR
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyReport
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.SimpleSparkLineChart
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    profile: UserProfile,
    measurements: List<BodyMeasurement>,
    prs: List<PerformancePR>,
    achievements: List<Achievement>,
    weeklyReports: List<WeeklyReport>,
    isGeneratingReport: Boolean,
    onLogMeasurement: (Float, Float?, Float?, Float?, Float?, Float?, String) -> Unit,
    onUpdatePR: (String, Float, String, String) -> Unit,
    onGenerateWeeklyReport: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Metrics & Charts", "PR Wall", "Trophy Shelf", "Weekly Reports")

    var showLogMeasurementDialog by remember { mutableStateOf(false) }
    var prToUpdate by remember { mutableStateOf<PerformancePR?>(null) }

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
                    text = "PROGRESS & ANALYTICS",
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Physique Evolution",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { showLogMeasurementDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("log_measurement_button")
            ) {
                Icon(imageVector = Icons.Default.Straighten, contentDescription = null, tint = Color(0xFF080B11), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "LOG STATS", color = Color(0xFF080B11), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
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
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("progress_tab_$index"),
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) CyanAccent else TextSecondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> MetricsAndChartsTab(
                profile = profile,
                measurements = measurements
            )
            1 -> PrWallTab(
                prs = prs,
                onSelectPR = { prToUpdate = it }
            )
            2 -> TrophyShelfTab(achievements = achievements)
            3 -> WeeklyReportsTab(
                reports = weeklyReports,
                isGenerating = isGeneratingReport,
                onGenerate = onGenerateWeeklyReport
            )
        }
    }

    // Measurement Dialog
    if (showLogMeasurementDialog) {
        var weightInput by remember { mutableStateOf(profile.currentWeightKg.toString()) }
        var waistInput by remember { mutableStateOf(profile.waistCm?.toString() ?: "") }
        var chestInput by remember { mutableStateOf(profile.chestCm?.toString() ?: "") }
        var armInput by remember { mutableStateOf(profile.armCm?.toString() ?: "") }
        var legInput by remember { mutableStateOf(profile.legCm?.toString() ?: "") }
        var bfInput by remember { mutableStateOf(profile.bodyFatEstimate?.toString() ?: "") }
        var notesInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLogMeasurementDialog = false },
            containerColor = ObsidianSurfaceElevated,
            title = { Text(text = "Log Body Measurements", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Weight (kg)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = waistInput,
                            onValueChange = { waistInput = it },
                            label = { Text("Waist (cm)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = chestInput,
                            onValueChange = { chestInput = it },
                            label = { Text("Chest (cm)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = armInput,
                            onValueChange = { armInput = it },
                            label = { Text("Arm (cm)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = legInput,
                            onValueChange = { legInput = it },
                            label = { Text("Thigh (cm)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bfInput,
                            onValueChange = { bfInput = it },
                            label = { Text("Body Fat %", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightInput.toFloatOrNull() ?: profile.currentWeightKg
                        onLogMeasurement(
                            w,
                            waistInput.toFloatOrNull(),
                            chestInput.toFloatOrNull(),
                            armInput.toFloatOrNull(),
                            legInput.toFloatOrNull(),
                            bfInput.toFloatOrNull(),
                            notesInput
                        )
                        showLogMeasurementDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "SAVE ENTRY", color = Color(0xFF080B11), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogMeasurementDialog = false }) {
                    Text(text = "CANCEL", color = TextSecondary)
                }
            }
        )
    }

    // PR Update Dialog
    if (prToUpdate != null) {
        val pr = prToUpdate!!
        var prValueInput by remember { mutableStateOf(pr.recordValue.toString()) }

        AlertDialog(
            onDismissRequest = { prToUpdate = null },
            containerColor = ObsidianSurfaceElevated,
            title = { Text(text = "Update Record: ${pr.exerciseName}", color = TextPrimary) },
            text = {
                Column {
                    Text(text = "Current Best: ${pr.recordValue} ${pr.unit}", color = CyanAccent, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = prValueInput,
                        onValueChange = { prValueInput = it },
                        label = { Text("New Record (${pr.unit})", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val v = prValueInput.toFloatOrNull() ?: pr.recordValue
                        onUpdatePR(pr.exerciseName, v, pr.unit, pr.category)
                        prToUpdate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Text(text = "UPDATE PR", color = Color(0xFF080B11), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { prToUpdate = null }) {
                    Text(text = "CANCEL", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun MetricsAndChartsTab(
    profile: UserProfile,
    measurements: List<BodyMeasurement>
) {
    val weightData = if (measurements.isNotEmpty()) {
        measurements.map { it.weightKg }
    } else {
        listOf(profile.currentWeightKg - 1.2f, profile.currentWeightKg - 0.8f, profile.currentWeightKg - 0.4f, profile.currentWeightKg)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CyberCard(borderColor = CyanAccent.copy(alpha = 0.4f)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "WEIGHT EVOLUTION CURVE", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${profile.currentWeightKg} kg", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        }
                        if (profile.targetWeightKg != null) {
                            CyberBadge(text = "Target: ${profile.targetWeightKg}kg", color = EmeraldAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SimpleSparkLineChart(
                        dataPoints = weightData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        lineColor = CyanAccent
                    )
                }
            }
        }

        item {
            Text(text = "CURRENT MEASUREMENT SNAPSHOT", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CyberCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Text(text = "Waist", color = TextMuted, fontSize = 11.sp)
                        Text(text = "${profile.waistCm ?: "--"} cm", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                CyberCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Text(text = "Chest", color = TextMuted, fontSize = 11.sp)
                        Text(text = "${profile.chestCm ?: "--"} cm", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                CyberCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Text(text = "Arms", color = TextMuted, fontSize = 11.sp)
                        Text(text = "${profile.armCm ?: "--"} cm", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrWallTab(
    prs: List<PerformancePR>,
    onSelectPR: (PerformancePR) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(prs) { pr ->
            CyberCard(
                onClick = { onSelectPR(pr) },
                modifier = Modifier.testTag("pr_item_${pr.exerciseName.lowercase().replace(" ", "_")}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = pr.exerciseName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "Category: ${pr.category} • Updated: ${pr.achievedDateString}", color = TextSecondary, fontSize = 11.sp)
                    }
                    CyberBadge(text = "${pr.recordValue} ${pr.unit}", color = AmberAccent)
                }
            }
        }
    }
}

@Composable
private fun TrophyShelfTab(achievements: List<Achievement>) {
    val unlockedCount = achievements.count { it.isUnlocked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CyberCard(borderColor = EmeraldAccent.copy(alpha = 0.4f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "ACHIEVEMENT MASTERY", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$unlockedCount / ${achievements.size} Unlocked", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(32.dp))
                }
            }
        }

        items(achievements) { ach ->
            CyberCard(
                borderColor = if (ach.isUnlocked) EmeraldAccent.copy(alpha = 0.5f) else ObsidianBorder
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (ach.isUnlocked) EmeraldAccent.copy(alpha = 0.15f) else ObsidianSurfaceElevated)
                            .border(1.dp, if (ach.isUnlocked) EmeraldAccent else ObsidianBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (ach.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (ach.isUnlocked) EmeraldAccent else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ach.title,
                            color = if (ach.isUnlocked) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(text = ach.description, color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyReportsTab(
    reports: List<WeeklyReport>,
    isGenerating: Boolean,
    onGenerate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Button(
                onClick = onGenerate,
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("generate_weekly_report_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF080B11), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ORAXIS AI IS SYNTHESIZING REPORT...", color = Color(0xFF080B11), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF080B11), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "GENERATE ORAXIS WEEKLY AUDIT", color = Color(0xFF080B11), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
        }

        if (reports.isEmpty()) {
            item {
                CyberCard {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No weekly audits generated yet. Tap above to synthesize this week's audit.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(reports) { rep ->
                CyberCard(
                    borderColor = PurpleAccent.copy(alpha = 0.5f)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "ORAXIS AUDIT: ${rep.weekIdentifier}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            CyberBadge(text = "${rep.startDateString} - ${rep.endDateString}", color = PurpleAccent)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "🌟 Key Achievement", color = EmeraldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = rep.mainAchievement, color = TextPrimary, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "⚠️ Limiter & Bottleneck", color = AmberAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = rep.biggestLimiter, color = TextPrimary, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "🎯 Next Week's Blueprint Priorities", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = rep.nextWeekPriorities, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
