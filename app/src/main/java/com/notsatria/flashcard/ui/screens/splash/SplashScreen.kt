package com.notsatria.flashcard.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel = koinViewModel<SplashViewModel>()

    LaunchedEffect(Unit) {
        if (viewModel.isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    SplashScreenContent(modifier)
}

@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Box(modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("FlashCard")
        }
    }
}