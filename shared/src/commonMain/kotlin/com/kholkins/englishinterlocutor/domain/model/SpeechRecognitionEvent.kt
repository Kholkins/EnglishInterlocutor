package com.kholkins.englishinterlocutor.domain.model

sealed interface SpeechRecognitionEvent {
    data object Idle : SpeechRecognitionEvent
    data object Listening : SpeechRecognitionEvent
    data class PartialResult(val text: String) : SpeechRecognitionEvent
    data class FinalResult(val text: String) : SpeechRecognitionEvent
    data class Error(val message: String) : SpeechRecognitionEvent
}
