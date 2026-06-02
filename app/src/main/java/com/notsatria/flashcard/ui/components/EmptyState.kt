package com.notsatria.flashcard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.flashcard.R
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.FlashcardTheme

@Composable
fun EmptyState(modifier: Modifier = Modifier, text: String) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(R.drawable.il_empty_state),
            null,
            modifier = Modifier.size(200.dp),
            colorFilter = ColorFilter.tint(
                FlashColors.Indigo500
            )
        )
        Spacer(Modifier.height(16.dp))
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