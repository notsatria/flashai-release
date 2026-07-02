package com.notsatria.flashai.ui.screens.detail

import com.notsatria.flashai.domain.model.Deck

data class DeckDetailUiState(
    val isLoading: Boolean = true,
    val deck: Deck? = null,
    val errorMessage: String? = null
)