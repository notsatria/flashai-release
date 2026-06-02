package com.notsatria.flashcard.domain.repository

import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.domain.model.FlashCard
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun observerDecks(): Flow<List<Deck>>
    fun observeDeck(deckId: String): Flow<Deck?>

    suspend fun createDeck(name: String, description: String?, color: String, emoji: String)
    suspend fun deleteDeck(deckId: String)
    suspend fun addFlashCard(deckId: String, question: String, answer: String)
    suspend fun deleteFlashCard(deckId: String, cardId: String)
    suspend fun updateFlashCard(deckId: String, card: FlashCard)
}