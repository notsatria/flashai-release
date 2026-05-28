package com.notsatria.flashcard

import android.app.Application
import com.notsatria.flashcard.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StarterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@StarterApplication)
            modules(appModule)
        }
    }
}
