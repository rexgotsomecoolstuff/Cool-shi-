package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.UserProfile
import com.example.data.repository.AiCoachRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AiCoachViewModel(
    private val aiCoachRepository: AiCoachRepository
) : ViewModel() {

    val chatMessages: StateFlow<List<ChatMessage>> = aiCoachRepository.chatHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isResponding = MutableStateFlow(false)
    val isResponding = _isResponding.asStateFlow()

    fun sendMessage(text: String, profile: UserProfile) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isResponding.value = true
            aiCoachRepository.sendMessage(text, profile)
            _isResponding.value = false
        }
    }
}
