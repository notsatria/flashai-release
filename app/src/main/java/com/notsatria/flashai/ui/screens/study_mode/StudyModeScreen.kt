package com.notsatria.flashai.ui.screens.study_mode

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashai.R
import com.notsatria.flashai.ui.components.FlashCardTopBar
import com.notsatria.flashai.ui.components.LoadingScreen
import com.notsatria.flashai.ui.screens.MissingDeckScreen
import com.notsatria.flashai.ui.theme.FlashColors
import com.notsatria.flashai.ui.theme.FlashShape
import com.notsatria.flashai.ui.theme.FlashSpacing
import com.notsatria.flashai.ui.theme.FlashTypography
import com.notsatria.flashai.ui.theme.FlashcardTheme
import com.notsatria.flashai.ui.theme.flashShadow
import org.koin.androidx.compose.koinViewModel

@Composable
fun StudyModeScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: StudyModeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StudyModeScreenContent(modifier, onBack = onBack, uiState = uiState)
}

@Composable
fun StudyModeScreenContent(
    modifier: Modifier = Modifier,
    uiState: StudyModeUiState = StudyModeUiState(),
    onBack: () -> Unit = {},
) {
    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    if (uiState.deck == null) {
        MissingDeckScreen(onBack = onBack, modifier = modifier)
        return
    }

    var currentIndex by remember(uiState.deck.id) { mutableIntStateOf(0) }
    val currentCard = uiState.deck.cards.getOrNull(currentIndex)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FlashColors.Background,
        topBar = {
            FlashCardTopBar(title = uiState.deck.name, onBack = onBack)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FlashColors.Background)
                .padding(padding)
                .padding(horizontal = FlashSpacing.lg, vertical = FlashSpacing.xl),
        ) {
            if (currentCard == null) {
                Text(
                    text = stringResource(R.string.this_deck_doesnt_have_card),
                    style = FlashTypography.bodyLarge,
                    color = FlashColors.Gray600,
                )
                return@Column
            }

            StudyProgress(
                currentIndex = currentIndex,
                totalCards = uiState.deck.cards.size,
                deckColor = uiState.deck.color,
            )

            Spacer(modifier = Modifier.height(32.dp))

            StudyFlashCard(
                question = currentCard.question,
                answer = currentCard.answer,
                deckColor = uiState.deck.color,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .heightIn(min = 360.dp, max = 480.dp),
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FlashSpacing.md),
            ) {
                StudyNavButton(
                    text = stringResource(R.string.prev),
                    deckColor = uiState.deck.color,
                    enabled = currentIndex > 0,
                    isPrimary = false,
                    modifier = Modifier.weight(1f),
                    onClick = { currentIndex-- },
                )
                StudyNavButton(
                    text = stringResource(R.string.next),
                    deckColor = uiState.deck.color,
                    enabled = currentIndex < uiState.deck.cards.lastIndex,
                    isPrimary = true,
                    modifier = Modifier.weight(1f),
                    onClick = { currentIndex++ },
                )
            }
        }
    }
}
@Composable
private fun StudyProgress(
    currentIndex: Int,
    totalCards: Int,
    deckColor: Color,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = stringResource(R.string.study_progress),
                modifier = Modifier.weight(1f),
                style = FlashTypography.labelMedium,
                color = deckColor,
            )
            Text(
                text = "${currentIndex + 1} / $totalCards",
                style = FlashTypography.titleLarge,
                color = deckColor,
            )
        }
        Spacer(modifier = Modifier.height(FlashSpacing.sm))
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalCards.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(FlashShape.full),
            color = deckColor,
            trackColor = deckColor.copy(alpha = 0.14f),
        )
    }
}

@Composable
private fun StudyFlashCard(
    question: String,
    answer: String,
    deckColor: Color,
    modifier: Modifier = Modifier,
) {
    var isFlipped by remember(question, answer) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "studyCardRotation",
    )
    val showingFront = rotation <= 90f

    Box(
        modifier = modifier
            .flashShadow(
                color = deckColor.copy(alpha = 0.36f),
                borderRadius = 32.dp,
                blurRadius = 20.dp
            )
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(FlashShape.large)
            .background(
                brush = if (showingFront) {
                    Brush.linearGradient(
                        colors = listOf(deckColor.copy(alpha = 0.92f), deckColor),
                        start = Offset.Zero,
                        end = Offset.Infinite,
                    )
                } else {
                    Brush.linearGradient(listOf(FlashColors.Indigo100, FlashColors.Surface))
                }
            )
            .clickable { isFlipped = !isFlipped }
            .padding(horizontal = FlashSpacing.xl, vertical = 44.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (showingFront) {
            StudyCardFront(question = question)
        } else {
            StudyCardBack(answer = answer, deckColor = deckColor)
        }
    }
}

@Composable
private fun StudyCardFront(question: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.68f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "?",
                    style = FlashTypography.titleLarge,
                    color = FlashColors.Indigo500.copy(alpha = 0.72f),
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = question,
            style = FlashTypography.displayLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(52.dp))
        Text(
            text = stringResource(R.string.tap_to_see_answer),
            style = FlashTypography.labelMedium,
            color = Color.White.copy(alpha = 0.72f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StudyCardBack(
    answer: String,
    deckColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.graphicsLayer { rotationY = 180f },
    ) {
        Text(
            text = stringResource(R.string.answer),
            style = FlashTypography.titleMedium,
            color = deckColor,
        )
        Spacer(modifier = Modifier.height(FlashSpacing.lg))
        Text(
            text = answer,
            style = FlashTypography.titleLarge,
            color = FlashColors.Gray900,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(FlashSpacing.xl))
        Text(
            text = stringResource(R.string.tap_to_go_back_question),
            style = FlashTypography.labelMedium,
            color = FlashColors.Gray400,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StudyNavButton(
    text: String,
    deckColor: Color,
    enabled: Boolean,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (isPrimary) Color.White else deckColor

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(64.dp)
            .flashShadow(
                color = deckColor.copy(alpha = if (enabled) 0.28f else 0.08f),
                borderRadius = 12.dp,
                blurRadius = 8.dp,
            ),
        shape = FlashShape.small,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isPrimary) deckColor else FlashColors.Surface,
            disabledContainerColor = if (isPrimary) deckColor.copy(alpha = 0.35f) else FlashColors.Surface,
            contentColor = contentColor,
            disabledContentColor = FlashColors.Gray400,
        ),
        border = BorderStroke(2.dp, if (enabled) deckColor else FlashColors.Gray200),
    ) {
        if (!isPrimary) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = contentColor,
            )
            Spacer(modifier = Modifier.width(FlashSpacing.sm))
        }
        Text(text = text, style = FlashTypography.labelLarge)
        if (isPrimary) {
            Spacer(modifier = Modifier.width(FlashSpacing.sm))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = contentColor,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    FlashcardTheme {
        StudyModeScreenContent()
    }
}