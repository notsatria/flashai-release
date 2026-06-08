package com.notsatria.flashai.ui.screens.study_mode

import com.notsatria.flashai.domain.model.Deck

data class StudyModeUiState(
    val isLoading: Boolean = true,
    val deck: Deck? = null,
    val errorMessage: String? = null
)