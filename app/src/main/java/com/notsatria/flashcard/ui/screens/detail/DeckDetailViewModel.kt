package com.notsatria.flashcard.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashcard.domain.model.FlashCard
import com.notsatria.flashcard.domain.repository.DeckRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckDetailViewModel(
    private val deckId: String,
    private val deckRepository: DeckRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeckDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

    init {
        viewModelScope.launch {
            deckRepository.observeDeck(deckId = deckId)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat kartu."
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

    fun addFlashCard(question: String, answer: String) {
        if (question.isBlank() || answer.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                deckRepository.addFlashCard(
                    deckId = deckId,
                    question = question,
                    answer = answer
                )
            }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal menambahkan kartu."
                        )
                    }
                }
                .onSuccess {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }

    fun deleteFlashCard(cardId: String) {
        viewModelScope.launch {
            runCatching {
                deckRepository.deleteFlashCard(deckId, cardId)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Gagal menghapus kartu.")
                }
            }
        }
    }

    fun updateFlashCard(card: FlashCard) {
        viewModelScope.launch {
            runCatching {
                deckRepository.updateFlashCard(deckId, card)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Gagal menyimpan kartu.")
                }
            }
        }
    }
}