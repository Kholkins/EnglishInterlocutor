package com.kholkins.englishinterlocutor.presentation.speech

data class SpeechUiState(
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val partialText: String = "",
    val errorMessage: String? = null,
)
