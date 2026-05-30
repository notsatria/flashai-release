package com.notsatria.flashcard.navigation

import androidx.navigation3.runtime.NavBackStack

interface Navigator {
    fun setBackStack(backStack: NavBackStack)
    fun navigateTo(route: AppRoute)
    fun navigateBack(): Boolean
    fun navigateAndPopUpTo(route: AppRoute)
}
