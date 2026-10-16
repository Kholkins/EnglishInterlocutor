package com.kholkins.englishinterlocutor.platform

import com.kholkins.englishinterlocutor.domain.repository.TextToSpeechRepository

/**
 * Actual реализация для получения TextToSpeech Repository на iOS.
 */
actual fun getTextToSpeechRepository(): TextToSpeechRepository {
    return IosTextToSpeechRepository
}
