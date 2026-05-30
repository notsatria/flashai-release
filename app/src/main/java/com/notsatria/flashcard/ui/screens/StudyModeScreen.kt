package com.notsatria.flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.ui.components.FlipCard
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.getDeckColor

@Composable
fun StudyModeScreen(
    deck: Deck?,
    deckIndex: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (deck == null) {
        MissingDeckScreen(onBack = onBack, modifier = modifier)
        return
    }

    val deckColor = getDeckColor(deckIndex)
    var currentIndex by remember(deck.id) { mutableIntStateOf(0) }
    val currentCard = deck.cards.getOrNull(currentIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FlashColors.Background)
            .statusBarsPadding()
            .padding(FlashSpacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = FlashColors.Gray900,
                )
            }
            Text(
                text = deck.name,
                modifier = Modifier.weight(1f),
                style = FlashTypography.titleMedium,
                color = FlashColors.Gray900,
            )
        }

        Spacer(modifier = Modifier.height(FlashSpacing.lg))

        if (currentCard == null) {
            Text(
                text = "Deck ini belum punya kartu.",
                style = FlashTypography.bodyLarge,
                color = FlashColors.Gray600,
            )
            return
        }

        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / deck.cards.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(FlashShape.full),
            color = deckColor,
            trackColor = deckColor.copy(alpha = 0.2f),
        )
        Spacer(modifier = Modifier.height(FlashSpacing.sm))
        Text(
            text = "${currentIndex + 1}/${deck.cards.size}",
            style = FlashTypography.labelMedium,
            color = FlashColors.Gray400,
        )
        Spacer(modifier = Modifier.height(FlashSpacing.xl))

        FlipCard(
            question = currentCard.question,
            answer = currentCard.answer,
            deckColor = deckColor,
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FlashSpacing.md),
        ) {
            StudyNavButton(
                text = "Prev",
                deckColor = deckColor,
                enabled = currentIndex > 0,
                modifier = Modifier.weight(1f),
                onClick = { currentIndex-- },
            )
            StudyNavButton(
                text = "Next",
                deckColor = deckColor,
                enabled = currentIndex < deck.cards.lastIndex,
                modifier = Modifier.weight(1f),
                onClick = { currentIndex++ },
            )
        }
    }
}

@Composable
private fun StudyNavButton(
    text: String,
    deckColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = FlashShape.full,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = deckColor,
            disabledContentColor = FlashColors.Gray400,
        ),
    ) {
        Text(text = text, style = FlashTypography.labelLarge)
    }
}
