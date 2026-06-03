package com.notsatria.flashcard.ui.screens.study_mode

import com.notsatria.flashcard.domain.model.Deck

data class StudyModeUiState(
    val isLoading: Boolean = true,
    val deck: Deck? = null,
    val errorMessage: String? = null
)