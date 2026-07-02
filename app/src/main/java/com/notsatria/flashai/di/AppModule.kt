package com.notsatria.flashai.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.notsatria.flashai.data.repository.GroqAIFlashCardRepository
import com.notsatria.flashai.data.repository.FirebaseAuthRepository
import com.notsatria.flashai.data.repository.FirebaseDeckRepository
import com.notsatria.flashai.domain.repository.AIFlashCardRepository
import com.notsatria.flashai.domain.repository.AuthRepository
import com.notsatria.flashai.domain.repository.DeckRepository
import com.notsatria.flashai.navigation.AppNavigator
import com.notsatria.flashai.navigation.Navigator
import com.notsatria.flashai.ui.screens.add_deck.AddDeckViewModel
import com.notsatria.flashai.ui.screens.add_flashcard.AddFlashCardViewModel
import com.notsatria.flashai.ui.screens.ai_generate.GenerateAIViewModel
import com.notsatria.flashai.ui.screens.detail.DeckDetailViewModel
import com.notsatria.flashai.ui.screens.home.HomeViewModel
import com.notsatria.flashai.ui.screens.login.LoginViewModel
import com.notsatria.flashai.ui.screens.register.RegisterViewModel
import com.notsatria.flashai.ui.screens.splash.SplashViewModel
import com.notsatria.flashai.ui.screens.study_mode.StudyModeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<Navigator> { AppNavigator() }

    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }

    single<AuthRepository> { FirebaseAuthRepository(get()) }
    single<DeckRepository> { FirebaseDeckRepository(get(), get()) }
    single<AIFlashCardRepository> { GroqAIFlashCardRepository() }

    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::HomeViewModel)
    viewModel { parameters ->
        AddDeckViewModel(deckRepository = get(), deckId = parameters.get())
    }
    viewModel { parameters ->
        DeckDetailViewModel(deckId = parameters.get(), deckRepository = get())
    }
    viewModel { parameters ->
        AddFlashCardViewModel(
            deckId = parameters.get(),
            cardId = parameters.get(),
            deckRepository = get(),
        )
    }
    viewModel { parameters ->
        GenerateAIViewModel(
            deckId = parameters.get(),
            deckRepository = get(),
            aiFlashCardRepository = get(),
        )
    }
    viewModel { parameters ->
        StudyModeViewModel(get(), parameters.get())
    }
}
