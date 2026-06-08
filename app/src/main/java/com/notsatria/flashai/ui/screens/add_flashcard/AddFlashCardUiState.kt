package com.notsatria.flashai.ui.screens.add_flashcard

data class AddFlashCardUiState(
    val isLoading: Boolean = false,
    val isLoadingCard: Boolean = false,
    val isDeleting: Boolean = false,
    val isSuccess: Boolean = false,
    val cardId: String? = null,
    val question: String = "",
    val answer: String = ""
) {
    val isEditMode: Boolean
        get() = cardId != null
}
