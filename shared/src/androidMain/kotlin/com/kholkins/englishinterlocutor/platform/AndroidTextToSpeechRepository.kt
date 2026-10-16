package com.kholkins.englishinterlocutor.platform

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.kholkins.englishinterlocutor.domain.repository.TextToSpeechRepository
import java.util.Locale

class AndroidTextToSpeechRepository(
    private val context: Context,
) : TextToSpeechRepository, TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    init {
        val appContext = context.applicationContext
        textToSpeech = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            if (result == null || result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech?.setLanguage(Locale.ENGLISH)
            }

            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                }

                override fun onDone(utteranceId: String?) {
                }

                override fun onError(utteranceId: String?) {
                }
            })

            isInitialized = true
        } else {
            isInitialized = false
        }
    }

    override fun speak(text: String, language: String) {
        if (!isInitialized) return
        if (text.isBlank()) return

        val locale = try {
            val parts = language.split("-")
            Locale(parts[0], parts[1])
        } catch (e: Exception) {
            Locale.US
        }

        val utteranceId = "tts_${System.currentTimeMillis()}"
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        textToSpeech?.apply {
            stop()
            setLanguage(locale)
            setPitch(1.0f)
            setSpeechRate(0.9f)
            speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        }
    }

    override fun stop() {
        textToSpeech?.stop()
    }

    override fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
