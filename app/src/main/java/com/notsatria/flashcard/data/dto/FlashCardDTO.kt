package com.notsatria.flashcard.data.dto

import com.google.firebase.Timestamp
import com.notsatria.flashcard.domain.model.FlashCard

data class FlashCardDTO(
    val question: String = "",
    val answer: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
) {
    fun toDomain(id: String): FlashCard = FlashCard(
        id = id,
        question = question,
        answer = answer,
    )
}

data class FlashCardRequest(
    val question: String,
    val answer: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
)