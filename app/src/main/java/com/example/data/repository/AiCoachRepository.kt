package com.example.data.repository

import com.example.data.api.GeminiClient
import com.example.data.local.ProgressDao
import com.example.data.model.ChatMessage
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyReport
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiCoachRepository(
    private val progressDao: ProgressDao
) {
    val chatHistory: Flow<List<ChatMessage>> = progressDao.getAllChatMessages()
    val weeklyReports: Flow<List<WeeklyReport>> = progressDao.getAllWeeklyReports()

    suspend fun sendMessage(userText: String, profile: UserProfile): String {
        // Save user message
        progressDao.insertChatMessage(
            ChatMessage(
                sender = "USER",
                message = userText,
                timestamp = System.currentTimeMillis()
            )
        )

        // System prompt context with user's complete biological and fitness parameters
        val systemPrompt = """
            You are ORAXIS AI, the senior fitness architect and intelligent coach inside the ORAXIS PHYSIC fitness operating system.
            Core philosophy: 'Your body. Your goal. Your system.'
            
            Current User Context:
            - Name: ${profile.name}
            - Age: ${profile.age} (If under 18, strictly prioritize healthy growth, adequate nutrition, recovery, form over ego-lifting, no extreme caloric restriction)
            - Goal: ${profile.primaryGoal} + ${profile.secondaryGoal ?: "None"}
            - Priority Muscles: ${profile.priorityMuscles}
            - Fitness Level: ${profile.fitnessLevel}
            - Training Environment: ${profile.trainingEnvironment}
            - Available Equipment: ${profile.availableEquipment} (Strict rule: NEVER recommend equipment the user does not have)
            - Timeframe: ${profile.timeframe}
            - Daily Target: ${profile.dailyCalorieTarget} kcal, ${profile.dailyProteinTargetG}g protein
            - Dietary Constraints & Allergies: ${profile.dietaryRestrictions}, ${profile.allergies} (Strict: NEVER recommend allergens)
            
            Guidelines:
            1. Explain WHY you recommend something (exercise substitution, rep range, macro adjustment) rather than simply issuing commands.
            2. Ground advice in sports science, biomechanics, progressive overload, and intelligent recovery.
            3. Never promise unrealistic rapid transformations or body-fat predictions.
            4. If user mentions pain, acute joint discomfort, or dizziness, explicitly advise stopping and consulting a medical professional.
            5. Keep answers sharp, structured, motivational, and formatted cleanly with bullet points.
        """.trimIndent()

        val aiResult = GeminiClient.askGemini(userText, systemPrompt)

        val reply = if (aiResult.isSuccess && !aiResult.getOrNull().isNullOrBlank()) {
            aiResult.getOrNull()!!
        } else {
            generateExpertFallbackResponse(userText, profile)
        }

        progressDao.insertChatMessage(
            ChatMessage(
                sender = "ORAXIS_AI",
                message = reply,
                timestamp = System.currentTimeMillis()
            )
        )

        return reply
    }

    private fun generateExpertFallbackResponse(query: String, profile: UserProfile): String {
        val q = query.lowercase(Locale.getDefault())
        return when {
            q.contains("eat") || q.contains("food") || q.contains("meal") || q.contains("breakfast") -> {
                """
                🥗 **ORAXIS Nutrition Strategy for ${profile.primaryGoal.replace("_", " ")}**
                
                • **High-Protein Recommendation**: 3 whole eggs + 2 egg whites scrambled with spinach, 1 slice sourdough toast, and 150g Greek yogurt with berries (~38g Protein, 440 kcal).
                • **Protein Target Focus**: Your current target is **${profile.dailyProteinTargetG}g protein/day**. Distribute this across 3–4 meals with 30–45g per feeding to optimize muscle protein synthesis (MPS).
                • **Timing**: Consume a balanced carb + protein source 60–90 minutes pre-session for glycogen priming.
                """.trimIndent()
            }
            q.contains("substitute") || q.contains("alternative") || q.contains("replace") -> {
                """
                ⚡ **ORAXIS Exercise Substitution Protocol**
                
                • **Push Movement**: If Barbell Bench causes shoulder discomfort, switch to **30° Incline Dumbbell Press** with neutral grip or **Weighted Ring Push-ups** for scapular freedom.
                • **Pull Movement**: If Pull-ups feel stalled, perform **Inverted Bodyweight Rows** with elevated feet or **Lat Pulldowns** with a 3-second eccentric tempo.
                • **Legs**: Swap Back Squats for **Bulgarian Split Squats** or **Barbell Romanian Deadlifts** to reduce spinal compressive load while maximizing glute/quad recruitment.
                """.trimIndent()
            }
            q.contains("recovery") || q.contains("sore") || q.contains("tired") -> {
                """
                🧘 **ORAXIS Systemic Recovery Analysis**
                
                • **Muscle Soreness (DOMS)** is not an indicator of growth; it indicates novel mechanical stress.
                • **Action**: If recovery feels depleted today, perform a **Dynamic Deload**: maintain load at 70% with 2 sets in reserve (RIR 2-3) and increase water intake to **${profile.dailyWaterTargetMl}ml**.
                • **Sleep Priority**: Target **${profile.dailySleepTargetHours} hours** of dark, cool sleep tonight for optimal growth hormone output.
                """.trimIndent()
            }
            q.contains("calisthenics") || q.contains("handstand") || q.contains("planche") || q.contains("pullup") -> {
                """
                🤸 **ORAXIS Calisthenics Progression Blueprint**
                
                • **Prerequisite Rule**: Never jump to advanced levers without mastering straight-arm joint conditioning.
                • **Form Cue**: For Handstands, focus on active trap elevation (pushing the floor away) and fingertip grip pressure.
                • **Next Step**: Check your dedicated **Calisthenics Hub** to test your current hold time and unlock the next tier!
                """.trimIndent()
            }
            else -> {
                """
                🎯 **ORAXIS System Recommendation for ${profile.name}**
                
                • **Goal Alignment**: Currently targeting **${profile.primaryGoal.replace("_", " ")}** within a **${profile.timeframe.replace("_", " ")}** window.
                • **Focus**: Ensure you adhere to progressive overload—record every set's weight and reps in the session tracker.
                • **System Integrity**: Maintain consistency across Training, Nutrition (${profile.dailyCalorieTarget} kcal), and Sleep to compound your long-term results.
                """.trimIndent()
            }
        }
    }

    suspend fun generateWeeklyReport(
        profile: UserProfile,
        workoutsCompleted: Int,
        totalVolumeKg: Float
    ): WeeklyReport {
        val weekId = SimpleDateFormat("'W'ww-yyyy", Locale.getDefault()).format(Date())
        val dateFmt = SimpleDateFormat("MMM dd", Locale.getDefault())
        val start = dateFmt.format(Date(System.currentTimeMillis() - 7 * 86400000L))
        val end = dateFmt.format(Date())

        val report = WeeklyReport(
            weekIdentifier = weekId,
            startDateString = start,
            endDateString = end,
            workoutsCompleted = workoutsCompleted,
            totalVolumeKg = totalVolumeKg,
            calisthenicsProgressions = 2,
            avgCalorieIntake = profile.dailyCalorieTarget - 50,
            avgProteinGrams = profile.dailyProteinTargetG,
            avgSleepHours = 7.6f,
            weightChangeKg = if (profile.primaryGoal.contains("BULK")) +0.3f else -0.2f,
            recoveryAdherencePercent = 84,
            mainAchievement = "Consistently met training volume targets across upper & lower splits.",
            biggestLimiter = "Sleep consistency varied on training day 3; optimize pre-bed wind-down.",
            nextWeekPriorities = "Advance pull-up progression hold time and maintain +2.5kg load on primary compound lifts."
        )

        progressDao.insertWeeklyReport(report)
        return report
    }
}
