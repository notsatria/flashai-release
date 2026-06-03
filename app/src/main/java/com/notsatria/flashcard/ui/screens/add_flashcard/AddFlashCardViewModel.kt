package com.notsatria.flashcard.ui.screens.add_flashcard

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

class AddFlashCardViewModel(
    private val deckRepository: DeckRepository,
    private val deckId: String,
    cardId: String = "",
) : ViewModel() {
    private val editableCardId = cardId.takeIf { it.isNotBlank() }
    private var hasLoadedCard = false

    private val _uiState = MutableStateFlow(
        AddFlashCardUiState(
            isLoadingCard = editableCardId != null,
            cardId = editableCardId,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

    init {
        editableCardId?.let(::observeCard)
    }

    fun onQuestionChange(value: String) {
        _uiState.update {
            it.copy(question = value)
        }
    }

    fun onAnswerChange(value: String) {
        _uiState.update {
            it.copy(answer = value)
        }
    }

    fun submit() {
        viewModelScope.launch {
            val state = _uiState.value

            if (state.question.isEmpty()) {
                _showSnackbar.send("Pertanyaan tidak boleh kosong.")
                return@launch
            }

            if (state.answer.isEmpty()) {
                _showSnackbar.send("Jawaban tidak boleh kosong.")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                if (state.isEditMode && state.cardId != null) {
                    deckRepository.updateFlashCard(
                        deckId = deckId,
                        card = FlashCard(
                            id = state.cardId,
                            question = state.question,
                            answer = state.answer,
                        )
                    )
                } else {
                    deckRepository.addFlashCard(
                        deckId = deckId,
                        question = state.question,
                        answer = state.answer
                    )
                }
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                _showSnackbar.send(throwable.message ?: submitErrorMessage(state.isEditMode))
            }
        }
    }

    fun deleteFlashCard() {
        viewModelScope.launch {
            val cardId = _uiState.value.cardId
            if (cardId == null) {
                _showSnackbar.send("Flashcard belum bisa dihapus.")
                return@launch
            }

            _uiState.update { it.copy(isDeleting = true) }
            runCatching {
                deckRepository.deleteFlashCard(deckId, cardId)
            }.onSuccess {
                _uiState.update { it.copy(isDeleting = false, isSuccess = true) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isDeleting = false) }
                _showSnackbar.send(throwable.message ?: "Gagal menghapus flashcard.")
            }
        }
    }

    private fun observeCard(cardId: String) {
        viewModelScope.launch {
            deckRepository.observeDeck(deckId)
                .catch { throwable ->
                    _uiState.update { it.copy(isLoadingCard = false) }
                    _showSnackbar.send(throwable.message ?: "Gagal memuat flashcard.")
                }
                .collect { deck ->
                    if (hasLoadedCard) return@collect

                    val card = deck?.cards?.firstOrNull { it.id == cardId }
                    if (card != null) {
                        hasLoadedCard = true
                        _uiState.update {
                            it.copy(
                                isLoadingCard = false,
                                question = card.question,
                                answer = card.answer,
                            )
                        }
                    }
                }
        }
    }

    private fun submitErrorMessage(isEditMode: Boolean): String =
        if (isEditMode) "Gagal memperbarui flashcard." else "Gagal menyimpan flashcard."
}
