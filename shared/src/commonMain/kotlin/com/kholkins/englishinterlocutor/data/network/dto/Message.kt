package com.kholkins.englishinterlocutor.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: String,
    val text: String
)
