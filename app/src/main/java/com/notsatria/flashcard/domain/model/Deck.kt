package com.notsatria.flashcard.domain.model

import androidx.compose.ui.graphics.Color

data class Deck(
    val id: String = "",
    val name: String = "",
    val emoji: String = "",
    val description: String? = null,
    val color: Color = Color.White,
    val cards: List<FlashCard> = emptyList(),
) {
    val cardCount: Int
        get() = cards.size
}
