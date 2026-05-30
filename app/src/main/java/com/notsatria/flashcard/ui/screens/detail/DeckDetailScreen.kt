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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.notsatria.flashcard.model.Deck
import com.notsatria.flashcard.ui.components.AIGenerateButton
import com.notsatria.flashcard.ui.components.CardItem
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.screens.MissingDeckScreen
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.getDeckColor

@Composable
fun DeckDetailScreen(
    deck: Deck?,
    deckIndex: Int,
    onBack: () -> Unit,
    onStudyClick: () -> Unit,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (deck == null) {
        MissingDeckScreen(onBack = onBack, modifier = modifier)
        return
    }

    val deckColor = getDeckColor(deckIndex)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FlashColors.Background,
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
                FlashButton(
                    text = "+ Tambah",
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    color = deckColor,
                )
            }
        },
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
                    deck = deck,
                    deckColor = deckColor,
                    onBack = onBack,
                    onStudyClick = onStudyClick,
                )
            }
            items(deck.cards, key = { it.id }) { card ->
                CardItem(card = card, deckColor = deckColor, onDelete = {})
            }
        }
    }
}

@Composable
private fun DeckHeader(
    deck: Deck,
    deckColor: Color,
    onBack: () -> Unit,
    onStudyClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(FlashShape.large)
            .background(Brush.horizontalGradient(listOf(deckColor, deckColor.copy(alpha = 0.78f))))
            .statusBarsPadding()
            .padding(FlashSpacing.md),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White,
                    )
                }
                Text(
                    text = deck.name,
                    modifier = Modifier.weight(1f),
                    style = FlashTypography.titleLarge,
                    color = Color.White,
                )
                IconButton(onClick = onStudyClick) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mulai belajar",
                        tint = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            Text(
                text = "${deck.cardCount} kartu",
                style = FlashTypography.bodyLarge,
                color = Color.White.copy(alpha = 0.78f),
            )
        }
    }
}
