package com.notsatria.flashcard.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashShape
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.flashShadow

@Composable
fun FlipCard(
    question: String,
    answer: String,
    deckColor: Color = FlashColors.Indigo500,
    modifier: Modifier = Modifier,
) {
    var isFlipped by remember(question, answer) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "flipCardRotation",
    )
    val showingFront = rotation <= 90f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .flashShadow(color = deckColor.copy(alpha = 0.24f), borderRadius = 24.dp, blurRadius = 12.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(FlashShape.large)
            .background(
                brush = if (showingFront) {
                    Brush.linearGradient(
                        colors = listOf(deckColor, deckColor.copy(alpha = 0.75f)),
                        start = Offset.Zero,
                        end = Offset.Infinite,
                    )
                } else {
                    Brush.linearGradient(listOf(FlashColors.Indigo100, FlashColors.Surface))
                },
            )
            .clickable { isFlipped = !isFlipped }
            .padding(FlashSpacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        if (showingFront) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("?", style = FlashTypography.displayLarge, color = Color.White)
                Spacer(modifier = Modifier.height(FlashSpacing.md))
                Text(
                    text = question,
                    style = FlashTypography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(FlashSpacing.lg))
                Text(
                    text = "Tap untuk lihat jawaban",
                    style = FlashTypography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer { rotationY = 180f },
            ) {
                Text("OK", style = FlashTypography.titleLarge, color = deckColor)
                Spacer(modifier = Modifier.height(FlashSpacing.md))
                Text(
                    text = answer,
                    style = FlashTypography.bodyLarge,
                    color = FlashColors.Gray900,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
