package com.kholkins.englishinterlocutor.presentation.speech

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class SpeechUiState(
    val isListening: Boolean = false,
    val partialText: String = "",
    val messages: ImmutableList<SpeechMessage> = persistentListOf(),
    val errorMessage: String? = null,
)

@Stable
data class SpeechMessage(
    val id: Long,
    val englishText: String,
    val russianText: String? = null,
    val isTranslating: Boolean = false,
    val translationError: String? = null,
)