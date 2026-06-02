package com.notsatria.flashcard.ui.screens.add_deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashcard.domain.repository.DeckRepository
import com.notsatria.flashcard.ui.components.deckColors
import com.notsatria.flashcard.ui.components.deckEmojis
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddDeckViewModel(private val deckRepository: DeckRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AddDeckUiState(
            color = deckColors[0].name,
            emoji = deckEmojis[0].name
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onEmojiChange(value: String) {
        _uiState.update { it.copy(emoji = value) }
    }

    fun onColorChange(value: String) {
        _uiState.update { it.copy(color = value) }
    }

    fun addDeck() {
        viewModelScope.launch {
            val state = _uiState.value

            if (state.title.isBlank()) {
                _showSnackbar.send("Nama deck tidak boleh kosong.")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                deckRepository.createDeck(state.title, state.description, state.color, state.emoji)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                _showSnackbar.send(throwable.message ?: "Gagal membuat deck.")
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }
        }
    }
}
