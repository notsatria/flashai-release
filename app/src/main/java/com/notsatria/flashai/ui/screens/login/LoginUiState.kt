package com.notsatria.flashai.ui.screens.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false
)
