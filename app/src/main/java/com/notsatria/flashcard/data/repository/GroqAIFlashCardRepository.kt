package com.notsatria.flashcard.data.repository

import com.notsatria.flashcard.BuildConfig
import com.notsatria.flashcard.domain.model.FlashCard
import com.notsatria.flashcard.domain.repository.AIFlashCardRepository
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class GroqAIFlashCardRepository(
    private val apiKey: String = BuildConfig.GROQ_API_KEY,
    private val json: Json = Json { ignoreUnknownKeys = true },
) : AIFlashCardRepository {
    override suspend fun generateFlashCards(
        deckName: String,
        topic: String,
        cardCount: Int,
    ): List<FlashCard> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            error("GROQ_API_KEY belum diset di local.properties.")
        }

        val connection = (URL(GROQ_CHAT_COMPLETIONS_URL).openConnection() as HttpURLConnection)
            .apply {
                requestMethod = "POST"
                connectTimeout = 30_000
                readTimeout = 60_000
                doOutput = true
                setRequestProperty("Authorization", "Bearer $apiKey")
                setRequestProperty("Content-Type", "application/json")
            }

        runCatching {
            connection.outputStream.use { output ->
                output.write(buildRequestBody(deckName, topic, cardCount).encodeToByteArray())
            }

            val responseBody = if (connection.responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            }

            if (connection.responseCode !in 200..299) {
                error(parseGroqError(responseBody) ?: "Groq API gagal (${connection.responseCode}).")
            }

            parseFlashCards(responseBody, cardCount)
        }.also {
            connection.disconnect()
        }.getOrThrow()
    }

    private fun buildRequestBody(deckName: String, topic: String, cardCount: Int): String {
        val prompt = """
            Buat $cardCount flashcard untuk deck "$deckName".
            Topik: "$topic".

            Aturan:
            - Gunakan Bahasa Indonesia.
            - Pertanyaan harus jelas dan cocok untuk belajar aktif.
            - Jawaban harus ringkas, akurat, dan mudah dipahami.
            - Kembalikan tepat $cardCount kartu.
        """.trimIndent()

        val body = buildJsonObject {
            put("model", MODEL)
            put("temperature", 0.4)
            put("max_tokens", 1600)
            put(
                "messages",
                buildJsonArray {
                    add(
                        buildJsonObject {
                            put("role", "system")
                            put(
                                "content",
                                "Kamu adalah pembuat flashcard belajar. Balas hanya JSON valid."
                            )
                        }
                    )
                    add(
                        buildJsonObject {
                            put("role", "user")
                            put("content", prompt)
                        }
                    )
                }
            )
            put("response_format", flashCardResponseFormat())
        }

        return json.encodeToString(JsonObject.serializer(), body)
    }

    private fun flashCardResponseFormat(): JsonObject = buildJsonObject {
        put("type", "json_schema")
        put(
            "json_schema",
            buildJsonObject {
                put("name", "flashcard_generation")
                put("strict", true)
                put(
                    "schema",
                    buildJsonObject {
                        put("type", "object")
                        put("additionalProperties", false)
                        put("required", buildJsonArray { add(JsonPrimitive("cards")) })
                        put(
                            "properties",
                            buildJsonObject {
                                put(
                                    "cards",
                                    buildJsonObject {
                                        put("type", "array")
                                        put(
                                            "items",
                                            buildJsonObject {
                                                put("type", "object")
                                                put("additionalProperties", false)
                                                put(
                                                    "required",
                                                    buildJsonArray {
                                                        add(JsonPrimitive("question"))
                                                        add(JsonPrimitive("answer"))
                                                    }
                                                )
                                                put(
                                                    "properties",
                                                    buildJsonObject {
                                                        put(
                                                            "question",
                                                            buildJsonObject { put("type", "string") }
                                                        )
                                                        put(
                                                            "answer",
                                                            buildJsonObject { put("type", "string") }
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    private fun parseFlashCards(responseBody: String, requestedCount: Int): List<FlashCard> {
        val root = json.parseToJsonElement(responseBody).jsonObject
        val content = root["choices"]
            ?.jsonArray
            ?.firstOrNull()
            ?.jsonObject
            ?.get("message")
            ?.jsonObject
            ?.get("content")
            ?.jsonPrimitive
            ?.content
            ?: error("Respons Groq tidak berisi hasil kartu.")

        val generated = json.decodeFromString(
            GeneratedFlashCardsDto.serializer(),
            content.extractJsonObject()
        )

        return generated.cards
            .take(requestedCount)
            .mapIndexed { index, card ->
                FlashCard(
                    id = "generated-${System.currentTimeMillis()}-$index",
                    question = card.question.trim(),
                    answer = card.answer.trim(),
                )
            }
            .filter { it.question.isNotBlank() && it.answer.isNotBlank() }
            .also {
                if (it.isEmpty()) error("Groq tidak menghasilkan flashcard yang valid.")
            }
    }

    private fun parseGroqError(responseBody: String): String? = runCatching {
        json.parseToJsonElement(responseBody)
            .jsonObject["error"]
            ?.jsonObject
            ?.get("message")
            ?.jsonPrimitive
            ?.content
    }.getOrNull()

    private fun String.extractJsonObject(): String {
        val trimmed = trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) return trimmed

        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1)
        }
        return trimmed
    }

    private companion object {
        const val GROQ_CHAT_COMPLETIONS_URL = "https://api.groq.com/openai/v1/chat/completions"
        const val MODEL = "openai/gpt-oss-120b"
    }
}

@Serializable
private data class GeneratedFlashCardsDto(
    val cards: List<GeneratedFlashCardDto>,
)

@Serializable
private data class GeneratedFlashCardDto(
    val question: String,
    val answer: String,
)
