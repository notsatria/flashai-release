package com.notsatria.flashcard.ui.theme

import androidx.compose.ui.graphics.Color

object FlashColors {
    val Indigo50 = Color(0xFFEEF2FF)
    val Indigo100 = Color(0xFFE0E7FF)
    val Indigo300 = Color(0xFFA5B4FC)
    val Indigo500 = Color(0xFF6366F1)
    val Indigo600 = Color(0xFF4F46E5)
    val Indigo700 = Color(0xFF4338CA)

    val Cyan200 = Color(0xFFdaf3ff)
    val Cyan400 = Color(0xFF22D3EE)
    val Cyan500 = Color(0xFF06B6D4)

    val Amber400 = Color(0xFFFBBF24)
    val Amber500 = Color(0xFFF59E0B)

    val DeckBlue = Color(0xFF6366F1)
    val DeckPurple = Color(0xFFA855F7)
    val DeckPink = Color(0xFFEC4899)
    val DeckGreen = Color(0xFF22C55E)
    val DeckOrange = Color(0xFFF97316)
    val DeckTeal = Color(0xFF14B8A6)
    val Teal200 = Color(0xFF006879)

    val Gray50 = Color(0xFFF9FAFB)
    val Gray100 = Color(0xFFF3F4F6)
    val Gray200 = Color(0xFFE5E7EB)
    val Gray400 = Color(0xFF9CA3AF)
    val Gray600 = Color(0xFF4B5563)
    val Gray900 = Color(0xFF111827)

    val Background = Color(0xFFF5F7FF)
    val Surface = Color(0xFFFFFFFF)
}

val deckColorPalette = listOf(
    FlashColors.DeckBlue,
    FlashColors.DeckPurple,
    FlashColors.DeckPink,
    FlashColors.DeckGreen,
    FlashColors.DeckOrange,
    FlashColors.DeckTeal,
)

fun getDeckColor(index: Int): Color = deckColorPalette[index % deckColorPalette.size]
