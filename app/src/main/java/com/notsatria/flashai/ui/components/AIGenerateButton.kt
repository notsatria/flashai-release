package com.notsatria.flashai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notsatria.flashai.ui.theme.FlashColors
import com.notsatria.flashai.ui.theme.FlashShape
import com.notsatria.flashai.ui.theme.FlashSpacing
import com.notsatria.flashai.ui.theme.FlashTypography
import com.notsatria.flashai.ui.theme.flashShadow

@Composable
fun AIGenerateButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .flashShadow(color = FlashColors.Cyan500.copy(alpha = 0.24f), borderRadius = 50.dp, blurRadius = 8.dp)
            .clip(FlashShape.full)
            .background(Brush.horizontalGradient(listOf(FlashColors.Cyan500, FlashColors.Indigo500)))
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(horizontal = FlashSpacing.lg, vertical = FlashSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FlashSpacing.sm),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                )
                Text("Generating...", style = FlashTypography.labelLarge, color = Color.White)
            } else {
                Text("✨", style = FlashTypography.labelLarge, color = Color.White)
                Text("Generate dengan AI", style = FlashTypography.labelLarge, color = Color.White)
            }
        }
    }
}
