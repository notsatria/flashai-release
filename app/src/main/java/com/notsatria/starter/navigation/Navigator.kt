package com.notsatria.starter.navigation

import androidx.navigation3.runtime.NavBackStack

interface Navigator {
    fun setBackStack(backStack: NavBackStack)
    fun navigateTo(route: AppRoute)
    fun navigateBack(): Boolean
}
