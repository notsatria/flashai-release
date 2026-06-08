package com.notsatria.flashai.domain.repository

import com.notsatria.flashai.domain.model.FlashCard

interface AIFlashCardRepository {
    suspend fun generateFlashCards(
        deckName: String,
        topic: String,
        cardCount: Int,
    ): List<FlashCard>
}
