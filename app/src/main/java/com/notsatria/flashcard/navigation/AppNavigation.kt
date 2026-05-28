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
import com.notsatria.flashcard.ui.screens.HomeScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navigator: Navigator = koinInject(),
) {
    val backStack = rememberNavBackStack(AppRoute.Home)

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
            entry<AppRoute.Home> {
                HomeScreen()
            }
        },
    )
}
