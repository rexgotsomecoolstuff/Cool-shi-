package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER" or "ORAXIS_AI"
    val message: String,
    val category: String = "GENERAL", // WORKOUT_ADVICE, NUTRITION, RECOVERY, CALISTHENICS
    val timestamp: Long = System.currentTimeMillis()
)
