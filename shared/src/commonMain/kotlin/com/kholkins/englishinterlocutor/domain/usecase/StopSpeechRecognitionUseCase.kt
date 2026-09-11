package com.kholkins.englishinterlocutor.domain.usecase

import com.kholkins.englishinterlocutor.domain.repository.SpeechRecognitionRepository

class StopSpeechRecognitionUseCase(
    private val speechRecognitionRepository: SpeechRecognitionRepository,
) {
    operator fun invoke() {
        speechRecognitionRepository.stopListening()
    }
}
