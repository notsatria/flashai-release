package com.notsatria.flashcard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.ui.screens.ai_generate.GenerateAIScreen
import com.notsatria.flashcard.ui.screens.ai_generate.GenerateAIViewModel
import com.notsatria.flashcard.ui.screens.detail.DeckDetailScreen
import com.notsatria.flashcard.ui.screens.home.HomeScreen
import com.notsatria.flashcard.ui.screens.study_mode.StudyModeScreen
import com.notsatria.flashcard.ui.screens.add_deck.AddDeckScreen
import com.notsatria.flashcard.ui.screens.add_deck.AddDeckViewModel
import com.notsatria.flashcard.ui.screens.add_flashcard.AddFlashCardScreen
import com.notsatria.flashcard.ui.screens.add_flashcard.AddFlashCardViewModel
import com.notsatria.flashcard.ui.screens.detail.DeckDetailViewModel
import com.notsatria.flashcard.ui.screens.login.LoginScreen
import com.notsatria.flashcard.ui.screens.register.RegisterScreen
import com.notsatria.flashcard.ui.screens.splash.SplashScreen
import com.notsatria.flashcard.ui.screens.study_mode.StudyModeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navigator: Navigator = koinInject(),
) {
    val backStack = rememberNavBackStack(AppRoute.Splash)

    SideEffect {
        navigator.setBackStack(backStack)
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { count ->
            repeat(count) {
                navigator.navigateBack()
            }
        },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<AppRoute.Splash> {
                SplashScreen(
                    onNavigateToHome = {
                        navigator.navigateAndPopUpTo(AppRoute.Home)
                    },
                    onNavigateToLogin = {
                        navigator.navigateAndPopUpTo(AppRoute.Login)
                    }
                )
            }
            entry<AppRoute.Login> {
                LoginScreen(
                    onNavigateToRegister = {
                        navigator.navigateAndPopUpTo(AppRoute.Register)
                    },
                    onNavigateToHome = {
                        navigator.navigateAndPopUpTo(AppRoute.Home)
                    }
                )
            }
            entry<AppRoute.Register> {
                RegisterScreen(
                    onNavigateToLogin = {
                        navigator.navigateAndPopUpTo(AppRoute.Login)
                    },
                    onNavigateToHome = {
                        navigator.navigateAndPopUpTo(AppRoute.Home)
                    }
                )
            }
            entry<AppRoute.Home> {
                HomeScreen(
                    onDeckClick = { deck -> navigator.navigateTo(AppRoute.DeckDetail(deck.id)) },
                    onAddDeckClick = {
                        navigator.navigateTo(AppRoute.AddDeck())
                    }
                )
            }
            entry<AppRoute.AddDeck> {
                val viewModel: AddDeckViewModel = koinViewModel(
                    parameters = { parametersOf(it.deckId.orEmpty()) }
                )
                AddDeckScreen(onBack = {
                    navigator.navigateBack()
                }, viewModel = viewModel)
            }
            entry<AppRoute.DeckDetail> { route ->
                val viewModel: DeckDetailViewModel = koinViewModel(
                    parameters = { parametersOf(route.deckId) }
                )
                DeckDetailScreen(
                    onBack = { navigator.navigateBack() },
                    onStudyClick = { navigator.navigateTo(AppRoute.StudyMode(route.deckId)) },
                    onGenerateClick = { navigator.navigateTo(AppRoute.GenerateAI(route.deckId)) },
                    onAddFlashCardClick = { navigator.navigateTo(AppRoute.AddFlashCard(route.deckId)) },
                    onEditFlashCardClick = { cardId ->
                        navigator.navigateTo(AppRoute.AddFlashCard(route.deckId, cardId))
                    },
                    onEditDeckClick = { navigator.navigateTo(AppRoute.AddDeck(route.deckId)) },
                    viewModel = viewModel
                )
            }
            entry<AppRoute.AddFlashCard> { route ->
                val viewModel: AddFlashCardViewModel = koinViewModel(
                    parameters = { parametersOf(route.deckId, route.cardId.orEmpty()) }
                )
                AddFlashCardScreen(
                    onBack = { navigator.navigateBack() },
                    viewModel = viewModel
                )
            }
            entry<AppRoute.StudyMode> { route ->
                val viewModel: StudyModeViewModel = koinViewModel(
                    parameters = { parametersOf(route.deckId) }
                )
                StudyModeScreen(
                    onBack = { navigator.navigateBack() },
                    viewModel = viewModel
                )
            }
            entry<AppRoute.GenerateAI> { route ->
                val viewModel: GenerateAIViewModel = koinViewModel(
                    parameters = { parametersOf(route.deckId) }
                )
                GenerateAIScreen(
                    onBack = { navigator.navigateBack() },
                    viewModel = viewModel,
                )
            }
        },
    )
}
