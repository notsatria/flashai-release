package com.notsatria.flashai.domain.model

data class DeckWithCards(
    val deck: Deck,
    val cards: List<FlashCard>
) {
    fun asDeck(): Deck = deck.copy(cards = cards)
}