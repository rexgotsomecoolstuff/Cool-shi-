package com.example.ui.screens.calisthenics

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.CalisthenicsSkill
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CalisthenicsScreen(
    skills: List<CalisthenicsSkill>,
    onTestSkill: (CalisthenicsSkill, String, Boolean, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Skills", "Foundations", "Elite Levers")

    var skillToTest by remember { mutableStateOf<CalisthenicsSkill?>(null) }

    val filteredSkills = when (selectedTab) {
        1 -> skills.filter { it.difficulty == "BEGINNER" || it.difficulty == "NOVICE" }
        2 -> skills.filter { it.difficulty == "INTERMEDIATE" || it.difficulty == "ADVANCED" || it.difficulty == "ELITE" }
        else -> skills
    }

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
                text = "CALISTHENICS MASTERY",
                color = EmeraldAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Bodyweight Skill Trees",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ObsidianBg,
            contentColor = EmeraldAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EmeraldAccent,
                    height = 2.dp
                )
            },
            divider = { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ObsidianBorder)) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("calisthenics_tab_$index"),
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) EmeraldAccent else TextSecondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredSkills) { skill ->
                val progressRatio = skill.currentLevel.toFloat() / skill.maxLevel.toFloat()
                CyberCard(
                    borderColor = if (skill.isMastered) EmeraldAccent else ObsidianBorder,
                    modifier = Modifier.testTag("calisthenics_skill_${skill.id}")
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
                                        text = skill.skillTreeName,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    if (skill.isMastered) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        CyberBadge(text = "MASTERED", color = EmeraldAccent)
                                    }
                                }
                                Text(
                                    text = "Current: Level ${skill.currentLevel} of ${skill.maxLevel} • Best: ${skill.bestRecord}",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            CyberBadge(
                                text = "Lvl ${skill.currentLevel}",
                                color = if (skill.isMastered) EmeraldAccent else CyanAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progressRatio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (skill.isMastered) EmeraldAccent else CyanAccent,
                            trackColor = ObsidianBorder
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Prerequisites: ${skill.prerequisites}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { skillToTest = skill },
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .border(1.dp, EmeraldAccent, RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = EmeraldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LOG LEVEL TEST",
                                    color = EmeraldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Skill Test Dialog
    if (skillToTest != null) {
        val skill = skillToTest!!
        var scoreInput by remember { mutableStateOf("") }
        var formChecked by remember { mutableStateOf(true) }
        var passedChoice by remember { mutableStateOf(true) }
        var notesInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { skillToTest = null },
            containerColor = ObsidianSurfaceElevated,
            title = {
                Text(
                    text = "Skill Test: ${skill.skillTreeName}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Test Level ${skill.currentLevel + 1} progression milestone.",
                        color = CyanAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Standard Prerequisite: ${skill.prerequisites}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = scoreInput,
                        onValueChange = { scoreInput = it },
                        label = { Text("Score Achieved (e.g., 12 reps or 15s hold)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldAccent,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { formChecked = !formChecked }
                    ) {
                        Checkbox(
                            checked = formChecked,
                            onCheckedChange = { formChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldAccent)
                        )
                        Text(
                            text = "Strict form maintained (no excessive momentum or form breakdown)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (passedChoice) EmeraldAccent else ObsidianBorder)
                                .clickable { passedChoice = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PASSED (+1 Lvl)",
                                color = if (passedChoice) Color(0xFF080B11) else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!passedChoice) AmberAccent else ObsidianBorder)
                                .clickable { passedChoice = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PRACTICE ATTEMPT",
                                color = if (!passedChoice) Color(0xFF080B11) else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score = scoreInput.ifBlank { "Logged" }
                        onTestSkill(skill, score, passedChoice && formChecked, notesInput)
                        skillToTest = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "RECORD TEST", color = Color(0xFF080B11), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { skillToTest = null }) {
                    Text(text = "CANCEL", color = TextSecondary)
                }
            }
        )
    }
}
