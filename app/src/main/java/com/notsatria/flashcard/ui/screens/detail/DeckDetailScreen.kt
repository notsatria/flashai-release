package com.notsatria.flashcard.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.ui.components.AIGenerateButton
import com.notsatria.flashcard.ui.components.CardItem
import com.notsatria.flashcard.ui.components.EmptyState
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.components.FlashCardTopBar
import com.notsatria.flashcard.ui.screens.MissingDeckScreen
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.FlashcardTheme
import com.notsatria.flashcard.ui.theme.flashShadow
import com.notsatria.flashcard.utils.rememberSnackbarHostState
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeckDetailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onAddFlashCardClick: () -> Unit,
    onStudyClick: () -> Unit,
    onGenerateClick: () -> Unit,
    onEditFlashCardClick: (String) -> Unit,
    onEditDeckClick: (String) -> Unit,
    viewModel: DeckDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarHostState()

    LaunchedEffect(Unit) {
        viewModel.showSnackbar.collect { message ->
            snackbarHostState.showSnackbar(message = message)
        }
    }

    DeckDetailScreenContent(
        modifier,
        uiState = uiState,
        onBack = onBack,
        onStudyClick = onStudyClick,
        onGenerateClick = onGenerateClick,
        onAddFlashCardClick = onAddFlashCardClick,
        onEditFlashCardClick = onEditFlashCardClick,
        onEditDeckClick = onEditDeckClick,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreenContent(
    modifier: Modifier = Modifier,
    uiState: DeckDetailUiState = DeckDetailUiState(),
    onBack: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    onGenerateClick: () -> Unit = {},
    onAddFlashCardClick: () -> Unit = {},
    onEditFlashCardClick: (String) -> Unit = {},
    onEditDeckClick: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState()
) {
    if (uiState.deck == null) {
        MissingDeckScreen(onBack = onBack, modifier = modifier)
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FlashColors.Background,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            FlashCardTopBar("Detail Deck", onBack = onBack, actions = {
                IconButton(onClick = { onEditDeckClick(uiState.deck.id) }) {
                    Icon(Icons.Default.Edit, null)
                }
            })
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FlashColors.Surface)
                    .navigationBarsPadding()
                    .padding(FlashSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(FlashSpacing.sm),
            ) {
                AIGenerateButton(
                    onClick = onGenerateClick,
                    modifier = Modifier.weight(1f),
                )
            }
        },
        floatingActionButton = {
            FlashButton(
                text = "+ Tambah",
                onClick = onAddFlashCardClick,
                color = uiState.deck.color,
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(FlashSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(FlashSpacing.md),
        ) {
            item {
                DeckHeader(
                    deck = uiState.deck,
                    onStudyClick = onStudyClick,
                )
            }
            item { Spacer(Modifier.height(FlashSpacing.md)) }
            item {
                Text("Daftar Kartu", style = FlashTypography.titleMedium)
            }
            item {
                if (uiState.deck.cards.isEmpty()) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .fillParentMaxHeight(0.5f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyState(text = "Flashcard masih kosong.")
                    }
                }
            }
            items(uiState.deck.cards, key = { it.id }) { card ->
                CardItem(
                    card = card,
                    deckColor = uiState.deck.color,
                    onEdit = { onEditFlashCardClick(card.id) },
                )
            }
        }
    }
}

@Composable
private fun DeckHeader(
    deck: Deck,
    onStudyClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .flashShadow(
                color = deck.color.copy(alpha = 0.25f),
                borderRadius = 24.dp,
                blurRadius = 32.dp,
            )
            .clip(FlashShape.large)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        deck.color,
                        deck.color.copy(alpha = 0.78f)
                    )
                )
            )
            .padding(FlashSpacing.md),
    ) {
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(FlashShape.full)
                    .background(color = Color.White.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "${deck.cardCount} kartu",
                    style = FlashTypography.bodyMedium,
                    color = Color.White.copy(alpha = 0.78f),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = deck.name,
                    style = FlashTypography.titleLarge,
                    color = Color.White,
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onStudyClick) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mulai belajar",
                        tint = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            if (deck.description != null)
                Text(
                    text = deck.description,
                    style = FlashTypography.bodyMedium,
                    color = Color.White,
                )
        }
    }
}

@Preview
@Composable
private fun DeckHeaderPreview() {
    FlashcardTheme {
        DeckHeader(
            deck = Deck(
                color = FlashColors.Indigo500,
                name = "Deck baru",
                description = "Halo"
            )
        ) { }
    }
}
