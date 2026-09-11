package com.kholkins.englishinterlocutor.domain.repository

import com.kholkins.englishinterlocutor.domain.model.SpeechRecognitionEvent
import kotlinx.coroutines.flow.Flow

interface SpeechRecognitionRepository {
    val recognition: Flow<SpeechRecognitionEvent>
    fun startListening()
    fun stopListening()
}
