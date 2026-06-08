package com.notsatria.flashai.ui.screens.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashai.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun register() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.email.isBlank() || state.password.length < 6) {
                _showSnackbar.send("Email wajib diisi dan password minimal 6 karakter.")
                return@launch
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
                _showSnackbar.send("Email tidak valid")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                authRepository.signUp(state.email, state.password)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegisterSuccess = true
                    )
                }
            }
                .onFailure { throwable ->
                    Log.e(TAG, "Error on register: ${throwable.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    _showSnackbar.send(throwable.message ?: "Gagal daftar. Coba lagi.")
                }
        }
    }

    private companion object {
        const val TAG = "RegisterViewModel"
    }
}
