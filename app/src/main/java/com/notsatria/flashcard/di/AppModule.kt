package com.notsatria.flashcard.di

import com.notsatria.flashcard.navigation.AppNavigator
import com.notsatria.flashcard.navigation.Navigator
import org.koin.dsl.module

val appModule = module {
    single<Navigator> { AppNavigator() }
}
