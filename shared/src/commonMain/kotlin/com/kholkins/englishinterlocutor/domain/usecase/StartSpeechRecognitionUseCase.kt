package com.kholkins.englishinterlocutor.domain.usecase

import com.kholkins.englishinterlocutor.domain.repository.SpeechRecognitionRepository

class StartSpeechRecognitionUseCase(
    private val speechRecognitionRepository: SpeechRecognitionRepository,
) {
    operator fun invoke() {
        speechRecognitionRepository.startListening()
    }
}
