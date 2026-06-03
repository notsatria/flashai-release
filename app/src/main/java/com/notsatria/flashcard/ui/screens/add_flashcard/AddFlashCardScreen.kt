package com.notsatria.flashcard.ui.screens.add_flashcard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashcard.ui.components.ConfirmationDialog
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.components.FlashCardTopBar
import com.notsatria.flashcard.ui.components.FlashTextField
import com.notsatria.flashcard.ui.components.LoadingScreen
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.FlashcardTheme
import com.notsatria.flashcard.utils.rememberSnackbarHostState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddFlashCardScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: AddFlashCardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarHostState()

    LaunchedEffect(Unit) {
        viewModel.showSnackbar.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBack()
        }
    }

    AddFlashCardScreenContent(
        modifier,
        onBack = onBack,
        onSave = viewModel::submit,
        uiState = uiState,
        onQuestionChange = viewModel::onQuestionChange,
        onAnswerChange = viewModel::onAnswerChange,
        onDelete = viewModel::deleteFlashCard,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun AddFlashCardScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
    onQuestionChange: (String) -> Unit = {},
    onAnswerChange: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    uiState: AddFlashCardUiState = AddFlashCardUiState()
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        modifier,
        topBar = {
            FlashCardTopBar(
                title = if (uiState.isEditMode) "Edit Flashcard" else "Tambah Flashcard",
                onBack = onBack
            )
        }, snackbarHost = {
            SnackbarHost(snackbarHostState)
        }) { padding ->
        if (uiState.isLoadingCard) {
            LoadingScreen()
            return@Scaffold
        }

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(FlashSpacing.md)
        ) {
            item {
                Surface(
                    Modifier.fillMaxWidth(),
                    color = FlashColors.Cyan200,
                    shape = FlashShape.medium
                ) {
                    Row(Modifier.padding(16.dp)) {
                        Icon(Icons.Default.AutoGraph, null, tint = FlashColors.Teal200)
                        Spacer(Modifier.width(FlashSpacing.md))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Siap membuat kartu baru?",
                                color = FlashColors.Teal200,
                                style = FlashTypography.bodyLarge,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Buat pertanyaan yang menantang untuk memperkuat ingatanmu.",
                                style = FlashTypography.bodySmall
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(FlashSpacing.lg)) }
            item {
                Surface(
                    border = BorderStroke(color = FlashColors.Gray200, width = 1.dp),
                    shape = FlashShape.medium
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Pertanyaan", style = FlashTypography.bodyLarge)
                        Spacer(Modifier.height(FlashSpacing.md))
                        FlashTextField(
                            modifier = Modifier,
                            value = uiState.question,
                            onValueChange = onQuestionChange,
                            placeholder = "Apa ibukota indonesia?",
                            minLines = 3
                        )
                        Spacer(Modifier.height(FlashSpacing.lg))
                        Text("Jawaban", style = FlashTypography.bodyLarge)
                        Spacer(Modifier.height(FlashSpacing.md))
                        FlashTextField(
                            modifier = Modifier,
                            value = uiState.answer,
                            onValueChange = onAnswerChange,
                            placeholder = "Ibukota Indonesia adalah Jakarta (saat ini) dan sedang dalam proses transisi ke Nusantara",
                            minLines = 4
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(FlashSpacing.lg)) }
            item {
                FlashButton(
                    text = if (uiState.isEditMode) "Simpan Perubahan" else "Simpan Kartu",
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState.isLoading
                )
            }
            if (uiState.isEditMode) {
                item { Spacer(Modifier.height(FlashSpacing.md)) }
                item {
                    FlashButton(
                        text = "Hapus Flashcard",
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.fillMaxWidth(),
                        color = FlashColors.DeckPink,
                        isLoading = uiState.isDeleting,
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Hapus flashcard?",
            message = "Flashcard ini akan dihapus dari deck.",
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                showDeleteConfirmation = false
                onDelete()
            },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    FlashcardTheme {
        AddFlashCardScreenContent()
    }
}
