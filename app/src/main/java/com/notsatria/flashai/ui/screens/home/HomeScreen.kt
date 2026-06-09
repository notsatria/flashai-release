package com.notsatria.flashai.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashai.R
import com.notsatria.flashai.domain.model.Deck
import com.notsatria.flashai.ui.components.ConfirmationDialog
import com.notsatria.flashai.ui.components.DeckCard
import com.notsatria.flashai.ui.components.EmptyState
import com.notsatria.flashai.ui.components.FlashButton
import com.notsatria.flashai.ui.theme.FlashColors
import com.notsatria.flashai.ui.theme.FlashSpacing
import com.notsatria.flashai.ui.theme.FlashTypography
import com.notsatria.flashai.utils.rememberSnackbarHostState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onDeckClick: (Deck) -> Unit,
    onAddDeckClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
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

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    HomeScreenContent(
        modifier,
        uiState = uiState,
        onDeckClick = onDeckClick,
        onAddDeckClick = onAddDeckClick,
        onLogoutClick = viewModel::signOut,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    onDeckClick: (Deck) -> Unit = {},
    onAddDeckClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState()
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }

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
        if (uiState.decks.isEmpty()) {
            Column(Modifier
                .fillMaxSize()
                .padding(padding)) {
                HomeHeader(
                    deckCount = uiState.decks.size,
                    onLogoutClick = { showLogoutConfirmation = true },
                    modifier = Modifier.padding(FlashSpacing.lg),
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(FlashSpacing.lg),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmptyState(
                        modifier = Modifier,
                        text = stringResource(R.string.deck_empty)
                    )
                }
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FlashColors.Background)
                .padding(padding),
            contentPadding = PaddingValues(FlashSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(FlashSpacing.md),
        ) {
            item {
                HomeHeader(
                    deckCount = uiState.decks.size,
                    onLogoutClick = { showLogoutConfirmation = true },
                )
            }
            itemsIndexed(uiState.decks, key = { _, deck -> deck.id }) { index, deck ->
                DeckCard(
                    deck = deck,
                    onClick = { onDeckClick(deck) },
                )
            }
            item {
                Spacer(modifier = Modifier.height(FlashSpacing.xxl))
            }
        }
    }

    if (showLogoutConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.logout),
            message = stringResource(R.string.you_will_signed_out_from_this_acc),
            onDismiss = { showLogoutConfirmation = false },
            onConfirm = {
                showLogoutConfirmation = false
                onLogoutClick()
            },
        )
    }
}

@Composable
private fun HomeHeader(
    deckCount: Int,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.halo_happy_studying),
                modifier = Modifier.weight(1f),
                style = FlashTypography.displayLarge,
                color = FlashColors.Gray900,
            )
            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = FlashColors.Gray600,
                )
            }
        }
        Spacer(modifier = Modifier.height(FlashSpacing.xs))
        Text(
            text = stringResource(R.string.you_have_deck_args, deckCount),
            style = FlashTypography.bodyMedium,
            color = FlashColors.Gray400,
        )
        Spacer(modifier = Modifier.height(FlashSpacing.md))
    }
}
