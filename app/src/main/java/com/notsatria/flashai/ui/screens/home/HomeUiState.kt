package com.notsatria.flashai.ui.screens.home

import com.notsatria.flashai.domain.model.Deck

data class HomeUiState(
    val isLoading: Boolean = true,
    val isLoggedOut: Boolean = false,
    val decks: List<Deck> = emptyList(),
)
