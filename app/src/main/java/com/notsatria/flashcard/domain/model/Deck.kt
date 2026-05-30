package com.notsatria.flashcard.domain.model

data class Deck(
    val id: String,
    val name: String,
    val cards: List<FlashCard>,
) {
    val cardCount: Int
        get() = cards.size
}
