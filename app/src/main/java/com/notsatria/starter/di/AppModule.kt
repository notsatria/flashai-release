package com.notsatria.starter.di

import com.notsatria.starter.navigation.AppNavigator
import com.notsatria.starter.navigation.Navigator
import org.koin.dsl.module

val appModule = module {
    single<Navigator> { AppNavigator() }
}
