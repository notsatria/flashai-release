package com.notsatria.flashcard.model

data class DeckWithCards(
    val deck: Deck,
    val cards: List<FlashCard>
) {
    fun asDeck(): Deck = deck.copy(cards = cards)
}