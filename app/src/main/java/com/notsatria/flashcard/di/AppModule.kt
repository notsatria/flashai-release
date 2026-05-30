package com.notsatria.flashcard.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.notsatria.flashcard.data.repository.FirebaseAuthRepository
import com.notsatria.flashcard.data.repository.FirebaseDeckRepository
import com.notsatria.flashcard.domain.repository.AuthRepository
import com.notsatria.flashcard.domain.repository.DeckRepository
import com.notsatria.flashcard.navigation.AppNavigator
import com.notsatria.flashcard.navigation.Navigator
import com.notsatria.flashcard.ui.screens.login.LoginViewModel
import com.notsatria.flashcard.ui.screens.register.RegisterViewModel
import com.notsatria.flashcard.ui.screens.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<Navigator> { AppNavigator() }

    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }

    single<AuthRepository> { FirebaseAuthRepository(get()) }
    single<DeckRepository> { FirebaseDeckRepository(get(), get()) }

    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}
