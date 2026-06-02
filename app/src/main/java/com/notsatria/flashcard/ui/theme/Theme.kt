package com.notsatria.flashcard.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FlashLightColorScheme = lightColorScheme(
    primary = FlashColors.Indigo500,
    onPrimary = Color.White,
    primaryContainer = FlashColors.Indigo100,
    onPrimaryContainer = FlashColors.Indigo700,
    secondary = FlashColors.Cyan500,
    onSecondary = Color.White,
    tertiary = FlashColors.Amber500,
    background = FlashColors.Background,
    onBackground = FlashColors.Gray900,
    surface = FlashColors.Surface,
    onSurface = FlashColors.Gray900,
    surfaceVariant = FlashColors.Gray100,
    onSurfaceVariant = FlashColors.Gray600,
    outline = FlashColors.Gray200,
)

@Composable
fun FlashcardTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        val insets = WindowCompat.getInsetsController(window, view)
        insets.isAppearanceLightStatusBars = true
        insets.isAppearanceLightNavigationBars = true
    }
    MaterialTheme(
        colorScheme = FlashLightColorScheme,
        typography = FlashTypography,
        content = content,
    )
}
