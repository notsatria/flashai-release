package com.notsatria.flashcard.data.repository

import android.R.attr.name
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.notsatria.flashcard.data.dto.DeckDTO
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.domain.model.FlashCard
import com.notsatria.flashcard.domain.repository.DeckRepository
import com.notsatria.flashcard.ui.components.getColorFromName
import com.notsatria.flashcard.ui.components.getEmojiFromName
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.text.trim

class FirebaseDeckRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : DeckRepository {
    override fun observerDecks(): Flow<List<Deck>> = callbackFlow {
        val uid = getCurrentUid()
        val registration = decksRef(uid = uid)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val decks = snapshots?.documents.orEmpty().map { document ->
                    Deck(
                        id = document.id,
                        name = document.getString("name").orEmpty(),
                        color = getColorFromName(document.getString("color").orEmpty()),
                        emoji = getEmojiFromName(document.getString("emoji").orEmpty()),
                        cards = emptyList()
                    )
                }
                trySend(decks)
            }

        awaitClose { registration.remove() }
    }

    override fun observeDeck(deckId: String): Flow<Deck?> = callbackFlow {
        val uid = getCurrentUid()
        val deckDocument = decksRef(uid).document(deckId)
        val cardsCollection = deckDocument.collection(CARDS_COLLECTION)

        var currentDeckName: String? = null
        var currentDeckDesc: String? = null
        var currentDeckColor: String? = null
        var currentDeckEmoji: String? = null
        var currentCards: List<FlashCard> = emptyList()

        fun sendDeck() {
            val name = currentDeckName
            val desc = currentDeckDesc
            val color = currentDeckColor
            val emoji = currentDeckEmoji
            trySend(
                if (name == null) {
                    null
                } else {
                    Deck(
                        id = deckId,
                        name = name,
                        description = desc,
                        color = getColorFromName(color!!),
                        emoji = emoji!!,
                        cards = currentCards
                    )
                }
            )
        }

        val deckRegistration = deckDocument.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            currentDeckName = snapshot?.takeIf { it.exists() }?.getString("name")
            currentDeckDesc = snapshot?.takeIf { it.exists() }?.getString("description")
            currentDeckColor = snapshot?.takeIf { it.exists() }?.getString("color")
            currentDeckEmoji = snapshot?.takeIf { it.exists() }?.getString("emoji")
            sendDeck()
        }

        val cardsRegistration = cardsCollection
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                currentCards = snapshots?.documents.orEmpty().map { document ->
                    FlashCard(
                        id = document.id,
                        question = document.getString("question").orEmpty(),
                        answer = document.getString("answer").orEmpty(),
                    )
                }
                sendDeck()
            }

        awaitClose {
            deckRegistration.remove()
            cardsRegistration.remove()
        }
    }

    override suspend fun createDeck(
        name: String,
        description: String?,
        color: String,
        emoji: String,
    ) {
        val uid = getCurrentUid()
        val now = Timestamp.now()
        decksRef(uid).add(
            mapOf(
                "name" to name.trim(),
                "description" to description?.trim(),
                "color" to color,
                "emoji" to emoji,
                "createdAt" to now,
                "updatedAt" to now
            )
        ).await()
    }

    override suspend fun deleteDeck(deckId: String) {
        val uid = getCurrentUid()
        decksRef(uid).document(deckId).delete().await()
    }

    override suspend fun addFlashCard(
        deckId: String,
        question: String,
        answer: String
    ) {
        val uid = getCurrentUid()
        val now = Timestamp.now()
        decksRef(uid).document(deckId).collection(CARDS_COLLECTION).add(
            mapOf(
                "question" to question.trim(),
                "answer" to answer.trim(),
                "createdAt" to now,
                "updatedAt" to now
            )
        ).await()
    }

    override suspend fun deleteFlashCard(deckId: String, cardId: String) {
        val uid = getCurrentUid()
        decksRef(uid).document(deckId).collection(CARDS_COLLECTION).document(cardId).delete()
            .await()
    }

    override suspend fun updateFlashCard(
        deckId: String,
        card: FlashCard
    ) {
        val uid = getCurrentUid()
        val now = Timestamp.now()
        decksRef(uid).document(deckId).collection(CARDS_COLLECTION).document(card.id).update(
            mapOf(
                "question" to card.question.trim(),
                "answer" to card.answer.trim(),
                "updatedAt" to now
            )
        ).await()
    }

    private fun decksRef(uid: String) =
        firestore.collection(USERS_COLLECTION).document(uid).collection(DECKS_COLLECTION)

    private fun getCurrentUid(): String =
        auth.currentUser?.uid ?: error("User must be signed in before accessing decks.")

    private companion object {
        const val USERS_COLLECTION = "users"
        const val DECKS_COLLECTION = "decks"
        const val CARDS_COLLECTION = "cards"
    }
}