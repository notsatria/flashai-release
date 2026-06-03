package com.notsatria.flashcard.ui.components

import androidx.compose.ui.graphics.Color
import com.notsatria.flashcard.ui.theme.FlashColors

data class DeckIcon(
    val name: String,
    val emoji: String,
)

data class DeckColor(
    val name: String,
    val color: Color,
)

fun getColorFromName(name: String): Color {
    val nameIndex = deckColors.indexOfFirst { deckColor ->
        deckColor.name == name
    }
    return if (nameIndex == -1) deckColors[0].color else deckColors[nameIndex].color
}

fun getEmojiFromName(name: String): String {
    val nameIndex = deckEmojis.indexOfFirst { deckEmoji ->
        deckEmoji.name == name
    }
    return if (nameIndex == -1) deckEmojis[0].emoji else deckEmojis[nameIndex].emoji
}

val deckEmojis = listOf(
    DeckIcon(
        "book",
        "\uD83D\uDCDA"
    ),
    DeckIcon(
        "tube",
        "\uD83E\uDDEA"
    ),
    DeckIcon(
        "globe",
        "\uD83C\uDF0E"
    ),
    DeckIcon(
        "art",
        "\uD83C\uDFA8"
    ),
    DeckIcon(
        "laptop",
        "\uD83D\uDCBB"
    ),
    DeckIcon(
        "sport",
        "⛹\uFE0F"
    ),
)

val deckColors = listOf(
    DeckColor("indigo", FlashColors.Indigo500),
    DeckColor("purple", FlashColors.DeckPurple),
    DeckColor("pink", FlashColors.DeckPink),
    DeckColor("green", FlashColors.DeckGreen),
    DeckColor("orange", FlashColors.DeckOrange),
    DeckColor("teal", FlashColors.DeckTeal),
)
