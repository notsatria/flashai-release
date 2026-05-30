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
import com.notsatria.flashcard.domain.model.sampleDecks
import com.notsatria.flashcard.ui.screens.detail.DeckDetailScreen
import com.notsatria.flashcard.ui.screens.GenerateAIScreen
import com.notsatria.flashcard.ui.screens.home.HomeScreen
import com.notsatria.flashcard.ui.screens.StudyModeScreen
import com.notsatria.flashcard.ui.screens.login.LoginScreen
import com.notsatria.flashcard.ui.screens.register.RegisterScreen
import com.notsatria.flashcard.ui.screens.splash.SplashScreen
import org.koin.compose.koinInject

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
                    decks = sampleDecks,
                    onDeckClick = { deck -> navigator.navigateTo(AppRoute.DeckDetail(deck.id)) },
                )
            }
            entry<AppRoute.DeckDetail> { route ->
                val deck = sampleDecks.firstOrNull { it.id == route.deckId }
                DeckDetailScreen(
                    deck = deck,
                    deckIndex = sampleDecks.indexOfFirst { it.id == route.deckId }.coerceAtLeast(0),
                    onBack = { navigator.navigateBack() },
                    onStudyClick = { navigator.navigateTo(AppRoute.StudyMode(route.deckId)) },
                    onGenerateClick = { navigator.navigateTo(AppRoute.GenerateAI(route.deckId)) },
                )
            }
            entry<AppRoute.StudyMode> { route ->
                val deck = sampleDecks.firstOrNull { it.id == route.deckId }
                StudyModeScreen(
                    deck = deck,
                    deckIndex = sampleDecks.indexOfFirst { it.id == route.deckId }.coerceAtLeast(0),
                    onBack = { navigator.navigateBack() },
                )
            }
            entry<AppRoute.GenerateAI> { route ->
                val deck = sampleDecks.firstOrNull { it.id == route.deckId }
                GenerateAIScreen(
                    deck = deck,
                    deckIndex = sampleDecks.indexOfFirst { it.id == route.deckId }.coerceAtLeast(0),
                    onBack = { navigator.navigateBack() },
                )
            }
        },
    )
}
