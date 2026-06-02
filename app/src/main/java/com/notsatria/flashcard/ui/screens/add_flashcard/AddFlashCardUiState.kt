package com.notsatria.flashcard.ui.screens.add_flashcard

data class AddFlashCardUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val question: String = "",
    val answer: String = ""
)