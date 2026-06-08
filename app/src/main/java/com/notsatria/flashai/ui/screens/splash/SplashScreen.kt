package com.notsatria.flashai.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashai.R
import com.notsatria.flashai.ui.theme.FlashcardTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel = koinViewModel<SplashViewModel>()
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Home -> onNavigateToHome()
            SplashDestination.Login -> onNavigateToLogin()
            SplashDestination.Loading -> Unit
        }
    }

    SplashScreenContent(modifier)
}

@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Box(
            modifier
                .padding(innerPadding)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_flashai),
                null,
                modifier = Modifier.size(300.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    FlashcardTheme {
        SplashScreenContent()
    }
}
