package com.notsatria.flashai.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.notsatria.flashai.ui.theme.FlashTypography
import com.notsatria.flashai.ui.theme.FlashcardTheme

@Composable
fun EmptyState(modifier: Modifier = Modifier, text: String) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text, textAlign = TextAlign.Center, style = FlashTypography.bodyLarge)
    }
}

@Preview
@Composable
private fun Preview() {
    FlashcardTheme {
        EmptyState(text = "Kosong")
    }
}