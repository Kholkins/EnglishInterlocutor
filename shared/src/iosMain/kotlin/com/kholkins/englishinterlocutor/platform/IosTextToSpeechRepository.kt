package com.kholkins.englishinterlocutor.platform

import com.kholkins.englishinterlocutor.domain.repository.TextToSpeechRepository
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.*

/**
 * iOS реализация TextTo Speech Repository.
 * Использует AVFAudio AVSpeechSynthesizer для озвучки.
 */
object IosTextToSpeechRepository : TextToSpeechRepository {

    private var speechSynthesizer: AVSpeechSynthesizer? = null
    private var speechUtterance: AVSpeechUtterance? = null

    init {
        speechSynthesizer = AVSpeechSynthesizer()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun speak(text: String, language: String) {
        // Останавливаем текущую озвучку
        stop()

        // Создаем utterance
        speechUtterance = AVSpeechUtterance(string = text)

        // Устанавливаем язык
        val voice = when (language) {
            "ru-RU" -> AVSpeechSynthesisVoice.voiceWithLanguage("ru-RU")
            else -> AVSpeechSynthesisVoice.voiceWithLanguage("en-US")
        }
        speechUtterance?.voice = voice

        // Настраиваем скорость и высоту голоса
        speechUtterance?.rate = AVSpeechUtteranceDefaultSpeechRate * 0.9f
        speechUtterance?.pitchMultiplier = 1.0f

        // Озвучиваем
        speechSynthesizer?.speakUtterance(speechUtterance!!)
    }

    override fun stop() {
        speechSynthesizer?.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    override fun shutdown() {
        stop()
        speechSynthesizer = null
        speechUtterance = null
    }
}
