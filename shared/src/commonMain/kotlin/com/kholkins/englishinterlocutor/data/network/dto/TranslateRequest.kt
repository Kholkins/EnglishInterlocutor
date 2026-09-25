package com.kholkins.englishinterlocutor.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranslateRequest(
    val text: String,
    val sourceLang: String,
    val targetLang: String
)