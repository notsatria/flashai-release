package com.notsatria.flashcard.domain.repository

import com.notsatria.flashcard.domain.model.FlashCard

interface AIFlashCardRepository {
    suspend fun generateFlashCards(
        deckName: String,
        topic: String,
        cardCount: Int,
    ): List<FlashCard>
}
