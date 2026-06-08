package com.notsatria.flashai.ui.screens.add_deck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashai.ui.components.ConfirmationDialog
import com.notsatria.flashai.ui.components.DeckColor
import com.notsatria.flashai.ui.components.DeckIcon
import com.notsatria.flashai.ui.components.FlashButton
import com.notsatria.flashai.ui.components.FlashCardTopBar
import com.notsatria.flashai.ui.components.FlashTextField
import com.notsatria.flashai.ui.components.LoadingScreen
import com.notsatria.flashai.ui.components.deckColors
import com.notsatria.flashai.ui.components.deckEmojis
import com.notsatria.flashai.ui.theme.FlashColors
import com.notsatria.flashai.ui.theme.FlashSpacing
import com.notsatria.flashai.ui.theme.FlashcardTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddDeckScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: AddDeckViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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

    AddDeckScreenContent(
        modifier = modifier,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onEmojiChange = viewModel::onEmojiChange,
        onColorChange = viewModel::onColorChange,
        onAddDeck = viewModel::addDeck,
        onDeleteDeck = viewModel::deleteDeck,
    )
}

@Composable
fun AddDeckScreenContent(
    modifier: Modifier = Modifier,
    uiState: AddDeckUiState = AddDeckUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBack: () -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    onEmojiChange: (String) -> Unit = {},
    onColorChange: (String) -> Unit = {},
    onAddDeck: () -> Unit = {},
    onDeleteDeck: () -> Unit = {},
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        modifier,
        topBar = {
            FlashCardTopBar(
                title = if (uiState.isEditMode) "Edit Deck" else "Buat Deck Baru",
                onBack = onBack
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { innerPadding ->
        if (uiState.isLoadingDeck) {
            LoadingScreen()
            return@Scaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Nama Deck")
            Spacer(Modifier.height(8.dp))
            FlashTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                placeholder = "Contoh: Biologi Dasar"
            )
            Spacer(Modifier.height(16.dp))
            Row {
                Text("Deskripsi")
                Spacer(Modifier.width(4.dp))
                Text("(opsional)")
            }
            Spacer(Modifier.height(8.dp))
            FlashTextField(
                value = uiState.description.orEmpty(),
                onValueChange = onDescriptionChange,
                placeholder = "Apa yang akan kamu pelajari di deck ini?",
                minLines = 3
            )
            Spacer(Modifier.height(8.dp))
            Text("Pilih Ikon Deck")
            Spacer(Modifier.height(8.dp))
            DeckIconChooser(
                selectedIconName = uiState.emoji.ifBlank { deckEmojis.first().name },
                onIconSelected = { onEmojiChange(it.name) },
            )
            Spacer(Modifier.height(16.dp))
            Text("Warna Tema Deck")
            Spacer(Modifier.height(8.dp))
            DeckColorChooser(
                selectedColorName = uiState.color.ifBlank { deckColors.first().name },
                onColorSelected = { onColorChange(it.name) },
            )
            Spacer(Modifier.height(FlashSpacing.lg))
            FlashButton(
                text = if (uiState.isEditMode) "Simpan Perubahan" else "Buat Deck Sekarang",
                onClick = onAddDeck,
                modifier = Modifier.fillMaxWidth(),
                isLoading = uiState.isLoading,
            )
            if (uiState.isEditMode) {
                Spacer(Modifier.height(12.dp))
                FlashButton(
                    text = "Hapus Deck",
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    color = FlashColors.DeckPink,
                    isLoading = uiState.isDeleting,
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Hapus deck?",
            message = "Deck dan kartu di dalamnya akan dihapus.",
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                showDeleteConfirmation = false
                onDeleteDeck()
            },
        )
    }
}

@Composable
fun DeckIconChooser(
    selectedIconName: String,
    onIconSelected: (DeckIcon) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier.fillMaxWidth()) {
        items(deckEmojis, key = { it.name }) { icon ->
            val isSelected = icon.name == selectedIconName
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) FlashColors.Indigo50 else FlashColors.Surface,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) FlashColors.Indigo500 else FlashColors.Gray100,
                ),
                modifier = Modifier.clickable {
                    onIconSelected(icon)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        icon.emoji,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
fun DeckColorChooser(
    selectedColorName: String,
    onColorSelected: (DeckColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier.fillMaxWidth()) {
        items(deckColors, key = { it.name }) { deckColor ->
            val isSelected = deckColor.name == selectedColorName
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { onColorSelected(deckColor) },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color.White)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(deckColor.color),
                    )
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        border = BorderStroke(3.dp, deckColor.color),
                    ) {}
                } else {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = deckColor.color,
                        border = BorderStroke(2.dp, Color.White),
                        shadowElevation = 2.dp,
                    ) {}
                }
            }
            Spacer(Modifier.width(14.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    FlashcardTheme {
        AddDeckScreenContent()
    }
}

@Preview
@Composable
private fun DeckIconChooserPreview() {
    DeckIconChooser(
        selectedIconName = "book",
        onIconSelected = {},
    )
}

@Preview
@Composable
private fun DeckColorChooserPreview() {
    DeckColorChooser(
        selectedColorName = "indigo",
        onColorSelected = {},
    )
}
