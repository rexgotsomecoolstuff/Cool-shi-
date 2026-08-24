package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CalisthenicsSkill
import com.example.data.model.CalisthenicsTestLog
import com.example.data.repository.CalisthenicsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalisthenicsViewModel(
    private val calisthenicsRepository: CalisthenicsRepository
) : ViewModel() {

    val allSkills: StateFlow<List<CalisthenicsSkill>> = calisthenicsRepository.allSkills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testLogs: StateFlow<List<CalisthenicsTestLog>> = calisthenicsRepository.allTestLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSkill = MutableStateFlow<CalisthenicsSkill?>(null)
    val selectedSkill = _selectedSkill.asStateFlow()

    fun selectSkill(skill: CalisthenicsSkill) {
        _selectedSkill.value = skill
    }

    fun submitSkillTest(
        skill: CalisthenicsSkill,
        scoreAchieved: String,
        passed: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            calisthenicsRepository.logSkillTest(
                skill = skill,
                levelTested = skill.currentLevel,
                scoreAchieved = scoreAchieved,
                passed = passed,
                notes = notes
            )
            _selectedSkill.value = null
        }
    }
}
