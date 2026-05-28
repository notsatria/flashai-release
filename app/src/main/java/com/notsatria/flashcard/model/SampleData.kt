package com.notsatria.flashcard.model

val sampleDecks = listOf(
    Deck(
        id = "kotlin-basics",
        name = "Kotlin Basics",
        cards = listOf(
            FlashCard(
                id = "coroutine",
                question = "Apa itu coroutine di Kotlin?",
                answer = "Coroutine adalah unit eksekusi ringan untuk menjalankan pekerjaan asynchronous tanpa memblokir thread.",
            ),
            FlashCard(
                id = "sealed-class",
                question = "Kapan memakai sealed class?",
                answer = "Sealed class cocok untuk merepresentasikan pilihan state yang terbatas dan diketahui saat compile time.",
            ),
            FlashCard(
                id = "data-class",
                question = "Apa kelebihan data class?",
                answer = "Data class otomatis menyediakan equals, hashCode, toString, copy, dan componentN.",
            ),
        ),
    ),
    Deck(
        id = "compose-ui",
        name = "Jetpack Compose",
        cards = listOf(
            FlashCard(
                id = "composable",
                question = "Apa itu composable function?",
                answer = "Composable function adalah fungsi yang mendeskripsikan bagian UI dan dapat dikomposisi ulang oleh Compose.",
            ),
            FlashCard(
                id = "state",
                question = "Apa arti state hoisting?",
                answer = "State hoisting memindahkan state ke caller agar composable lebih mudah dipakai ulang dan diuji.",
            ),
        ),
    ),
    Deck(
        id = "ai-prompts",
        name = "AI Prompts",
        cards = listOf(
            FlashCard(
                id = "json-output",
                question = "Mengapa prompt perlu format JSON?",
                answer = "Format JSON membuat respons AI lebih mudah diparse dan divalidasi oleh aplikasi.",
            ),
        ),
    ),
)
