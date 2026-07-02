package com.notsatria.flashai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.notsatria.flashai.R
import com.notsatria.flashai.ui.components.FlashButton
import com.notsatria.flashai.ui.theme.FlashColors
import com.notsatria.flashai.ui.theme.FlashSpacing
import com.notsatria.flashai.ui.theme.FlashTypography

@Composable
fun MissingDeckScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FlashColors.Background)
            .padding(FlashSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.deck_not_found),
            style = FlashTypography.titleLarge,
            color = FlashColors.Gray900,
        )
        Text(
            text = stringResource(R.string.back_to_previous_page),
            style = FlashTypography.bodyMedium,
            color = FlashColors.Gray400,
        )
        FlashButton(text = "Kembali", onClick = onBack)
    }
}
