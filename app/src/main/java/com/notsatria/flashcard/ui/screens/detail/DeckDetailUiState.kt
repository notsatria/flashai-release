package com.notsatria.flashcard.ui.screens.detail

import com.notsatria.flashcard.domain.model.Deck

data class DeckDetailUiState(
    val isLoading: Boolean = true,
    val deck: Deck? = null,
    val errorMessage: String? = null
)