package com.notsatria.flashcard.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Register : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object AddDeck : AppRoute

    @Serializable
    data class DeckDetail(val deckId: String) : AppRoute

    @Serializable
    data class StudyMode(val deckId: String) : AppRoute

    @Serializable
    data class GenerateAI(val deckId: String) : AppRoute
}
