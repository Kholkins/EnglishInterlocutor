package com.kholkins.englishinterlocutor.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranslateResponse(
    val translatedText: String,
    val sourceText: String,
    val detectedLanguage: String
)