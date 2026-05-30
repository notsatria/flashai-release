package com.notsatria.flashcard.data.dto

import com.google.firebase.Timestamp
import com.notsatria.flashcard.domain.model.Deck

data class DeckDTO(
    val id: String,
    val name: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
) {
    fun toDomain(): Deck = Deck(
        id = id,
        name = name,
        cards = emptyList()
    )
}

data class DeckRequest(
    val name: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
)