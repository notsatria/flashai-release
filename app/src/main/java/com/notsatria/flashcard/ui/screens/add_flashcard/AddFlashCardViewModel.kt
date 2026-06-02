package com.notsatria.flashcard.ui.screens.add_flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashcard.domain.model.FlashCard
import com.notsatria.flashcard.domain.repository.DeckRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddFlashCardViewModel(
    private val deckRepository: DeckRepository,
    private val deckId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddFlashCardUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

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
                deckRepository.addFlashCard(
                    deckId = deckId,
                    question = state.question,
                    answer = state.answer
                )
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                _showSnackbar.send(throwable.message ?: "Gagal menyimpan flashcard.")
            }
        }
    }

    fun deleteFlashCard(cardId: String) {
        viewModelScope.launch {
            runCatching {
                deckRepository.deleteFlashCard(deckId, cardId)
            }.onFailure { throwable ->
                _showSnackbar.send(throwable.message ?: "Gagal menghapus flashcard.")
            }
        }
    }

    fun updateFlashCard(card: FlashCard) {
        viewModelScope.launch {
            runCatching {
                deckRepository.updateFlashCard(deckId, card)
            }.onFailure { throwable ->
                _showSnackbar.send(throwable.message ?: "Gagal memperbarui flashcard.")
            }
        }
    }
}