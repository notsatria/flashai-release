package com.notsatria.flashcard.ui.screens.ai_generate

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashcard.ui.components.AIGenerateButton
import com.notsatria.flashcard.ui.components.CardItem
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.components.FlashCardTopBar
import com.notsatria.flashcard.ui.components.FlashTextField
import com.notsatria.flashcard.ui.screens.MissingDeckScreen
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GenerateAIScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: GenerateAIViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.showSnackbar.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            onBack()
        }
    }

    GenerateAIScreenContent(
        modifier = modifier,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onTopicChange = viewModel::onTopicChange,
        onCardCountChange = viewModel::onCardCountChange,
        onGenerate = viewModel::generateFlashCards,
        onSaveAll = viewModel::saveGeneratedCards,
    )
}

@Composable
fun GenerateAIScreenContent(
    modifier: Modifier = Modifier,
    uiState: GenerateAIUiState = GenerateAIUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBack: () -> Unit = {},
    onTopicChange: (String) -> Unit = {},
    onCardCountChange: (Int) -> Unit = {},
    onGenerate: () -> Unit = {},
    onSaveAll: () -> Unit = {},
) {
    val deck = uiState.deck
    if (!uiState.isLoading && deck == null) {
        MissingDeckScreen(onBack = onBack, modifier = modifier)
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FlashColors.Background,
        topBar = {
            FlashCardTopBar("Generate dengan AI", onBack = onBack)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FlashColors.Background)
                .padding(paddingValues = padding),
            contentPadding = PaddingValues(FlashSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(FlashSpacing.md),
        ) {
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Memuat deck...",
                            style = FlashTypography.bodyLarge,
                            color = FlashColors.Gray400,
                        )
                    }
                }
                return@LazyColumn
            }

            item {
                Column {
                    Text(
                        text = deck?.name ?: "",
                        style = FlashTypography.titleLarge,
                        color = FlashColors.Gray900,
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.xs))
                    Text(
                        text = "Masukkan topik, lalu AI akan membuat flashcard untuk deck ini.",
                        style = FlashTypography.bodyMedium,
                        color = FlashColors.Gray400,
                    )
                }
            }
            item {
                FlashTextField(
                    value = uiState.topic,
                    onValueChange = onTopicChange,
                    placeholder = "Contoh: sistem pencernaan manusia",
                    minLines = 3,
                )
            }
            item {
                Column {
                    Text(
                        "Jumlah kartu:",
                        style = FlashTypography.bodyLarge,
                        color = FlashColors.Gray900
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.sm))
                    Row(horizontalArrangement = Arrangement.spacedBy(FlashSpacing.sm)) {
                        listOf(5, 10).forEach { count ->
                            FilterChip(
                                selected = uiState.cardCount == count,
                                onClick = { onCardCountChange(count) },
                                enabled = !uiState.isGenerating && !uiState.isSaving,
                                label = {
                                    Text(
                                        count.toString(),
                                        style = FlashTypography.labelLarge
                                    )
                                },
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
                    onClick = onGenerate,
                    isLoading = uiState.isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (uiState.generatedCards.isNotEmpty()) {
                item {
                    Text(
                        text = "Hasil (${uiState.generatedCards.size} kartu)",
                        style = FlashTypography.titleMedium,
                        color = FlashColors.Gray900,
                    )
                }
                items(uiState.generatedCards, key = { it.id }) { card ->
                    CardItem(card = card, deckColor = deck?.color ?: FlashColors.Indigo500)
                }
                item {
                    FlashButton(
                        text = "Simpan Semua ke Deck",
                        onClick = onSaveAll,
                        modifier = Modifier.fillMaxWidth(),
                        color = deck?.color ?: FlashColors.Indigo500,
                        isLoading = uiState.isSaving,
                    )
                }
            }
        }
    }
}
