package com.notsatria.flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.notsatria.flashcard.domain.model.Deck
import com.notsatria.flashcard.domain.model.FlashCard
import com.notsatria.flashcard.ui.components.AIGenerateButton
import com.notsatria.flashcard.ui.components.CardItem
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.components.FlashTextField
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.getDeckColor

@Composable
fun GenerateAIScreen(
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
    var topic by remember { mutableStateOf("") }
    var cardCount by remember { mutableIntStateOf(5) }
    var generatedCards by remember { mutableStateOf(emptyList<FlashCard>()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FlashColors.Background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(FlashSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FlashSpacing.md),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = FlashColors.Gray900,
                    )
                }
                Text(
                    text = "Generate dengan AI",
                    modifier = Modifier.weight(1f),
                    style = FlashTypography.titleLarge,
                    color = FlashColors.Gray900,
                )
                Text("AI", style = FlashTypography.labelLarge, color = FlashColors.Cyan500)
            }
        }
        item {
            FlashTextField(
                value = topic,
                onValueChange = { topic = it },
                placeholder = "Masukkan topik...",
                minLines = 3,
            )
        }
        item {
            Column {
                Text("Jumlah kartu:", style = FlashTypography.bodyLarge, color = FlashColors.Gray900)
                Spacer(modifier = Modifier.height(FlashSpacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(FlashSpacing.sm)) {
                    listOf(5, 10).forEach { count ->
                        FilterChip(
                            selected = cardCount == count,
                            onClick = { cardCount = count },
                            label = { Text(count.toString(), style = FlashTypography.labelLarge) },
                            shape = FlashShape.full,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FlashColors.Indigo100,
                                selectedLabelColor = FlashColors.Indigo700,
                            ),
                        )
                    }
                }
            }
        }
        item {
            AIGenerateButton(
                onClick = {
                    val baseTopic = topic.ifBlank { deck.name }
                    generatedCards = List(cardCount) { index ->
                        FlashCard(
                            id = "generated-${index + 1}",
                            question = "Q${index + 1}: Apa konsep penting dari $baseTopic?",
                            answer = "A${index + 1}: Jelaskan konsep $baseTopic secara singkat dan praktis.",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (generatedCards.isNotEmpty()) {
            item {
                Text(
                    text = "Hasil (${generatedCards.size} kartu)",
                    style = FlashTypography.titleMedium,
                    color = FlashColors.Gray900,
                )
            }
            items(generatedCards, key = { it.id }) { card ->
                CardItem(card = card, deckColor = deckColor, onDelete = {})
            }
            item {
                FlashButton(
                    text = "Simpan Semua ke Deck",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    color = deckColor,
                )
            }
        }
    }
}
