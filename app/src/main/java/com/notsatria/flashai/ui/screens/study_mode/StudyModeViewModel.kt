package com.notsatria.flashai.ui.screens.study_mode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashai.domain.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyModeViewModel(private val deckRepository: DeckRepository, private val deckId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(StudyModeUiState())
    val uiState = _uiState.asStateFlow()

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

}