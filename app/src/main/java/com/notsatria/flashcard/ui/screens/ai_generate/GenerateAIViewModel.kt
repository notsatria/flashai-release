package com.notsatria.flashcard.ui.screens.ai_generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashcard.domain.repository.AIFlashCardRepository
import com.notsatria.flashcard.domain.repository.DeckRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenerateAIViewModel(
    private val deckId: String,
    private val deckRepository: DeckRepository,
    private val aiFlashCardRepository: AIFlashCardRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GenerateAIUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

    init {
        viewModelScope.launch {
            deckRepository.observeDeck(deckId)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat deck."
                        )
                    }
                }
                .collect { deck ->
                    _uiState.update {
                        it.copy(isLoading = false, deck = deck, errorMessage = null)
                    }
                }
        }
    }

    fun onTopicChange(value: String) {
        _uiState.update { it.copy(topic = value) }
    }

    fun onCardCountChange(value: Int) {
        _uiState.update { it.copy(cardCount = value) }
    }

    fun generateFlashCards() {
        viewModelScope.launch {
            val state = _uiState.value
            val deck = state.deck

            if (deck == null) {
                _showSnackbar.send("Deck tidak ditemukan.")
                return@launch
            }

            val topic = state.topic.ifBlank { deck.name }
            if (topic.isBlank()) {
                _showSnackbar.send("Topik tidak boleh kosong.")
                return@launch
            }

            _uiState.update {
                it.copy(isGenerating = true, generatedCards = emptyList(), errorMessage = null)
            }

            runCatching {
                aiFlashCardRepository.generateFlashCards(
                    deckName = deck.name,
                    topic = topic,
                    cardCount = state.cardCount,
                )
            }.onSuccess { cards ->
                _uiState.update {
                    it.copy(isGenerating = false, generatedCards = cards)
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = throwable.message ?: "Gagal generate flashcard."
                    )
                }
                _showSnackbar.send(throwable.message ?: "Gagal generate flashcard.")
            }
        }
    }

    fun saveGeneratedCards() {
        viewModelScope.launch {
            val cards = _uiState.value.generatedCards

            if (cards.isEmpty()) {
                _showSnackbar.send("Belum ada flashcard untuk disimpan.")
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                cards.forEach { card ->
                    deckRepository.addFlashCard(
                        deckId = deckId,
                        question = card.question,
                        answer = card.answer,
                    )
                }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, isSaveSuccess = true) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Gagal menyimpan flashcard."
                    )
                }
                _showSnackbar.send(throwable.message ?: "Gagal menyimpan flashcard.")
            }
        }
    }
}
