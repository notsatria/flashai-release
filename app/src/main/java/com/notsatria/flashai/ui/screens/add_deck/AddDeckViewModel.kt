package com.notsatria.flashai.ui.screens.add_deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashai.domain.repository.DeckRepository
import com.notsatria.flashai.ui.components.deckColors
import com.notsatria.flashai.ui.components.deckEmojis
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddDeckViewModel(
    private val deckRepository: DeckRepository,
    deckId: String = "",
) : ViewModel() {
    private val editableDeckId = deckId.takeIf { it.isNotBlank() }
    private var hasLoadedDeck = false

    private val _uiState = MutableStateFlow(
        AddDeckUiState(
            isLoadingDeck = editableDeckId != null,
            deckId = editableDeckId,
            color = deckColors[0].name,
            emoji = deckEmojis[0].name
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

    init {
        editableDeckId?.let(::observeDeck)
    }

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
                if (state.isEditMode && state.deckId != null) {
                    deckRepository.updateDeck(
                        deckId = state.deckId,
                        name = state.title,
                        description = state.description,
                        color = state.color,
                        emoji = state.emoji,
                    )
                } else {
                    deckRepository.createDeck(
                        name = state.title,
                        description = state.description,
                        color = state.color,
                        emoji = state.emoji,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                _showSnackbar.send(throwable.message ?: submitErrorMessage(state.isEditMode))
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }
        }
    }

    fun deleteDeck() {
        viewModelScope.launch {
            val deckId = _uiState.value.deckId
            if (deckId == null) {
                _showSnackbar.send("Deck belum bisa dihapus.")
                return@launch
            }

            _uiState.update { it.copy(isDeleting = true) }
            runCatching {
                deckRepository.deleteDeck(deckId)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isDeleting = false) }
                _showSnackbar.send(throwable.message ?: "Gagal menghapus deck.")
            }.onSuccess {
                _uiState.update { it.copy(isDeleting = false, isSuccess = true) }
            }
        }
    }

    private fun observeDeck(deckId: String) {
        viewModelScope.launch {
            deckRepository.observeDeck(deckId)
                .catch { throwable ->
                    _uiState.update { it.copy(isLoadingDeck = false) }
                    _showSnackbar.send(throwable.message ?: "Gagal memuat deck.")
                }
                .collect { deck ->
                    if (deck == null || hasLoadedDeck) {
                        _uiState.update { it.copy(isLoadingDeck = false) }
                        return@collect
                    }

                    hasLoadedDeck = true
                    _uiState.update {
                        it.copy(
                            isLoadingDeck = false,
                            title = deck.name,
                            description = deck.description,
                            emoji = deckEmojis.firstOrNull { option -> option.emoji == deck.emoji }?.name
                                ?: deckEmojis[0].name,
                            color = deckColors.firstOrNull { option -> option.color == deck.color }?.name
                                ?: deckColors[0].name,
                        )
                    }
                }
        }
    }

    private fun submitErrorMessage(isEditMode: Boolean): String =
        if (isEditMode) "Gagal menyimpan perubahan deck." else "Gagal membuat deck."
}
