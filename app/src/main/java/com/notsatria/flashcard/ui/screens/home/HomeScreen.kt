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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.ui.components.DeckCard
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography

@Composable
fun HomeScreen(
    decks: List<Deck>,
    onDeckClick: (Deck) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FlashColors.Background,
        floatingActionButton = {
            FlashButton(text = "+ Deck Baru", onClick = {})
        },
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
                        text = "Kamu punya ${decks.size} deck aktif",
                        style = FlashTypography.bodyMedium,
                        color = FlashColors.Gray400,
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.md))
                }
            }
            itemsIndexed(decks, key = { _, deck -> deck.id }) { index, deck ->
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
