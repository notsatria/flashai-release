package com.notsatria.flashcard.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.ui.components.DeckCard
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.utils.rememberSnackbarHostState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onDeckClick: (Deck) -> Unit,
    onAddDeckClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarHostState()

    LaunchedEffect(Unit) {
        viewModel.showSnackbar.collect { message ->
            snackbarHostState.showSnackbar(message = message)
        }
    }

    HomeScreenContent(
        modifier,
        uiState = uiState,
        onDeckClick = onDeckClick,
        onAddDeckClick = onAddDeckClick,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    onDeckClick: (Deck) -> Unit = {},
    onAddDeckClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState()
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FlashColors.Background,
        floatingActionButton = {
            FlashButton(text = "+ Deck Baru", onClick = onAddDeckClick)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FlashColors.Background)
                .padding(padding),
            contentPadding = PaddingValues(FlashSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(FlashSpacing.md),
        ) {
            item {
                Column {
                    Text(
                        text = "Halo, Selamat Belajar!👋",
                        style = FlashTypography.displayLarge,
                        color = FlashColors.Gray900,
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.xs))
                    Text(
                        text = "Kamu punya ${uiState.decks.size} deck aktif",
                        style = FlashTypography.bodyMedium,
                        color = FlashColors.Gray400,
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.md))
                }
            }
            itemsIndexed(uiState.decks, key = { _, deck -> deck.id }) { index, deck ->
                DeckCard(
                    deck = deck,
                    deckIndex = index,
                    onClick = { onDeckClick(deck) },
                    onDelete = {},
                )
            }
            item {
                Spacer(modifier = Modifier.height(FlashSpacing.xxl))
            }
        }
    }
}
