package com.kholkins.englishinterlocutor.platform

import com.kholkins.englishinterlocutor.domain.repository.TextToSpeechRepository

actual fun getTextToSpeechRepository(): TextToSpeechRepository {
    val activity = CurrentActivityHolder.get()
        ?: throw IllegalStateException("Activity not available for TTS initialization")
    
    return AndroidTextToSpeechRepository(activity.applicationContext)
}
