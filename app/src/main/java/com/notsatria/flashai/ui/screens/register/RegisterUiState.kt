package com.notsatria.flashai.ui.screens.register

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isRegisterSuccess: Boolean = false
)
