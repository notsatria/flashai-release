package com.notsatria.flashcard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.FlashcardTheme
import com.notsatria.flashcard.ui.theme.flashShadow
import com.notsatria.flashcard.ui.theme.getDeckColor

@Composable
fun DeckCard(
    deck: Deck,
    deckIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val deckColor = getDeckColor(deckIndex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .flashShadow(color = deckColor.copy(alpha = 0.25f), borderRadius = 24.dp)
            .clickable(onClick = onClick),
        shape = FlashShape.large,
        colors = CardDefaults.cardColors(containerColor = FlashColors.Surface),
    ) {
        Column(
            modifier = Modifier.padding(FlashSpacing.md),
        ) {
            Row(Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(FlashShape.medium)
                        .background(deck.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(deck.emoji, style = FlashTypography.titleLarge, color = deckColor)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(FlashShape.full)
                        .background(deck.color.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "${deck.cardCount} kartu",
                        style = FlashTypography.labelMedium,
                        color = deck.color,
                    )
                }
            }

            Spacer(modifier = Modifier.height(FlashSpacing.md))

            Text(deck.name, style = FlashTypography.titleMedium, color = FlashColors.Gray900)

            Spacer(modifier = Modifier.height(FlashSpacing.md))

            LinearProgressIndicator(
                progress = { 0.4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = deck.color,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            Spacer(modifier = Modifier.height(FlashSpacing.md))

            Row(Modifier.fillMaxWidth()) {
                Text("Progress: 40%")
                Spacer(Modifier.weight(1f))
                Text(
                    "Lanjut Belajar",
                    color = FlashColors.Indigo500,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    FlashcardTheme {
        DeckCard(
            deck = Deck(
                id = "",
                name = "Belajar Bahasa Inggris",
                cards = listOf(
                ),
                emoji = deckEmojis[0].emoji,
                color = deckColors[0].color
            ),
            deckIndex = 0,
            onClick = {},
        )
    }
}