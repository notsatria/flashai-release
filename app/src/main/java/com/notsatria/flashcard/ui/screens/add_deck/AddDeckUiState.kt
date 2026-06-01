package com.notsatria.flashcard.ui.screens.add_deck

data class AddDeckUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val title: String = "",
    val description: String? = null,
    val emoji: String = "",
    val color: String = ""
)