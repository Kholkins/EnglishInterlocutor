package com.kholkins.englishinterlocutor.domain.usecase

import com.kholkins.englishinterlocutor.domain.model.SpeechRecognitionEvent
import com.kholkins.englishinterlocutor.domain.repository.SpeechRecognitionRepository
import kotlinx.coroutines.flow.Flow

class ObserveSpeechRecognitionUseCase(
    private val speechRecognitionRepository: SpeechRecognitionRepository,
) {
    operator fun invoke(): Flow<SpeechRecognitionEvent> =
        speechRecognitionRepository.recognition
}
