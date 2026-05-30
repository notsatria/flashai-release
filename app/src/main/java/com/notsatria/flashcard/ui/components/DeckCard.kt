package com.notsatria.flashcard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.flashShadow
import com.notsatria.flashcard.ui.theme.getDeckColor

@Composable
fun DeckCard(
    deck: Deck,
    deckIndex: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
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
        Row(
            modifier = Modifier.padding(FlashSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(FlashShape.medium)
                    .background(deckColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("📚", style = FlashTypography.titleLarge, color = deckColor)
            }

            Spacer(modifier = Modifier.width(FlashSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(deck.name, style = FlashTypography.titleMedium, color = FlashColors.Gray900)
                Spacer(modifier = Modifier.size(FlashSpacing.xs))
                Box(
                    modifier = Modifier
                        .clip(FlashShape.full)
                        .background(deckColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "${deck.cardCount} kartu",
                        style = FlashTypography.labelMedium,
                        color = deckColor,
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = FlashColors.Gray400,
                )
            }
        }
    }
}
