package com.kholkins.englishinterlocutor.domain.repository

interface TextToSpeechRepository {

    fun speak(text: String, language: String = "en-US")

    fun stop()

    fun shutdown()
}