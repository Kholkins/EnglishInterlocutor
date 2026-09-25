package com.kholkins.englishinterlocutor.data.api

import com.kholkins.englishinterlocutor.data.network.dto.TranslateRequest
import com.kholkins.englishinterlocutor.data.network.dto.TranslateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.json.Json

class BackendApi(
    private val client: HttpClient,
    private val baseUrl: String
) {
    @OptIn(InternalAPI::class)
    suspend fun translate(text: String): Result<String> = runCatching {
        val request = TranslateRequest(
            text = text,
            sourceLang = "en",
            targetLang = "ru"
        )

        val jsonString = Json.encodeToString(TranslateRequest.serializer(), request)
        println("[DEBUG] Sending JSON: $jsonString")

        val response = client.post("$baseUrl/api/translate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val rawJson = response.bodyAsText()
        println("[DEBUG] Raw JSON response: $rawJson")

        response.body<TranslateResponse>().translatedText
    }

    suspend fun healthCheck(): Result<Boolean> = runCatching {
        client.get("$baseUrl/api/health").status == HttpStatusCode.OK
    }
}