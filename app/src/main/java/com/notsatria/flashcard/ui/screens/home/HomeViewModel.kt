package com.notsatria.flashcard.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashcard.domain.repository.AuthRepository
import com.notsatria.flashcard.domain.repository.DeckRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init

class HomeViewModel(
    private val deckRepository: DeckRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackBar = Channel<String>()
    val showSnackbar = _showSnackBar.receiveAsFlow()

    init {
        viewModelScope.launch {
            deckRepository.observerDecks()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat deck."
                        )
                    }
                }
                .collect { decks ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            decks = decks,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun deleteDeck(deckId: String) {
        viewModelScope.launch {
            runCatching {
                deckRepository.deleteDeck(deckId = deckId)
            }.onSuccess {
                _showSnackBar.send("Deck berhasil dihapus.")
            }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Gagal menghapus deck.")
                    }
                }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}