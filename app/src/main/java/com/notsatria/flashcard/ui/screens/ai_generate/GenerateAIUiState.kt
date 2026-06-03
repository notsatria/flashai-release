package com.notsatria.flashcard.ui.screens.ai_generate

import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.domain.model.FlashCard

data class GenerateAIUiState(
    val isLoading: Boolean = true,
    val isGenerating: Boolean = false,
    val isSaving: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val deck: Deck? = null,
    val topic: String = "",
    val cardCount: Int = 5,
    val generatedCards: List<FlashCard> = emptyList(),
    val errorMessage: String? = null,
)
