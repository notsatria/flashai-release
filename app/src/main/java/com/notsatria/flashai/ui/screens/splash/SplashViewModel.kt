package com.notsatria.flashai.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.authState
                .catch {
                    _destination.update { SplashDestination.Login }
                }
                .collect { user ->
                    _destination.update {
                        if (user == null) {
                            SplashDestination.Login
                        } else {
                            SplashDestination.Home
                        }
                    }
                }
        }
    }
}

sealed interface SplashDestination {
    data object Loading : SplashDestination
    data object Login : SplashDestination
    data object Home : SplashDestination
}
