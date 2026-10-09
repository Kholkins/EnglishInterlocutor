package com.kholkins.englishinterlocutor.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiRequest(
    val messages: List<Message>,
    val model: String? = null
)
