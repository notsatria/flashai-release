package com.notsatria.flashcard.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.notsatria.flashcard.domain.repository.AuthRepository

class SplashViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val currentUser = authRepository.currentUser

    val isLoggedIn = currentUser != null
}