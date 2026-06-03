package com.notsatria.flashcard.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.notsatria.flashcard.ui.theme.FlashColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashCardTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = { Text(title, color = FlashColors.Indigo500) },
        navigationIcon = {
            if (onBack != null)
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null, tint = FlashColors.Indigo500)
                }
        }, actions = actions
    )
}