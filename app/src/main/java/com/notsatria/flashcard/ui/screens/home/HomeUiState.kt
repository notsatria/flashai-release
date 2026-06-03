package com.notsatria.flashcard.ui.screens.home

import com.notsatria.flashcard.domain.model.Deck

data class HomeUiState(
    val isLoading: Boolean = true,
    val isLoggedOut: Boolean = false,
    val decks: List<Deck> = emptyList(),
)
