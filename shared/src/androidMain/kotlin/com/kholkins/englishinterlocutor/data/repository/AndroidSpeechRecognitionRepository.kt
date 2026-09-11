package com.kholkins.englishinterlocutor.data.repository

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.kholkins.englishinterlocutor.domain.model.SpeechRecognitionEvent
import com.kholkins.englishinterlocutor.domain.repository.SpeechRecognitionRepository
import com.kholkins.englishinterlocutor.platform.CurrentActivityHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Locale

class AndroidSpeechRecognitionRepository(
    private val context: Context,
) : SpeechRecognitionRepository {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val events = MutableSharedFlow<SpeechRecognitionEvent>(extraBufferCapacity = 64)
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var wantsListening = false
    private var lastPartialText = ""

    override val recognition: Flow<SpeechRecognitionEvent> = events.asSharedFlow()

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            events.tryEmit(SpeechRecognitionEvent.Listening)
        }

        override fun onBeginningOfSpeech() = Unit

        override fun onRmsChanged(rmsdB: Float) = Unit

        override fun onBufferReceived(buffer: ByteArray?) = Unit

        override fun onEndOfSpeech() = Unit

        override fun onError(error: Int) {
            isListening = false
            if (error == SpeechRecognizer.ERROR_CLIENT && lastPartialText.isNotBlank()) {
                emitFinal(lastPartialText)
                return
            }
            val message = when (error) {
                SpeechRecognizer.ERROR_NO_MATCH ->
                    "Could not recognize English speech. Try again."
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                    "No speech detected."
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                    "Microphone permission is required."
                SpeechRecognizer.ERROR_NETWORK,
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
                    -> "Network error. Check your connection."
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                    "Speech recognizer is busy. Try again."
                else -> "Speech recognition failed."
            }
            events.tryEmit(SpeechRecognitionEvent.Error(message))
        }

        override fun onResults(results: Bundle?) {
            isListening = false
            val text = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                .orEmpty()
                .ifBlank { lastPartialText }
            emitFinal(text)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val text = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                .orEmpty()
            if (text.isNotBlank()) {
                lastPartialText = text
                events.tryEmit(SpeechRecognitionEvent.PartialResult(text))
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    override fun startListening() {
        mainHandler.post {
            wantsListening = true
            if (isListening) return@post
            if (!hasMicrophonePermission()) {
                wantsListening = false
                requestMicrophonePermission()
                events.tryEmit(
                    SpeechRecognitionEvent.Error(
                        "Allow microphone access, then hold the button and speak.",
                    ),
                )
                return@post
            }
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                wantsListening = false
                events.tryEmit(
                    SpeechRecognitionEvent.Error(
                        "Speech recognition is not available on this device.",
                    ),
                )
                return@post
            }

            lastPartialText = ""
            val recognizer = ensureRecognizer() ?: return@post
            isListening = true
            events.tryEmit(SpeechRecognitionEvent.Listening)
            recognizer.startListening(recognitionIntent())
            if (!wantsListening) {
                isListening = false
                recognizer.stopListening()
            }
        }
    }

    override fun stopListening() {
        mainHandler.post {
            wantsListening = false
            if (!isListening) return@post
            isListening = false
            speechRecognizer?.stopListening()
        }
    }

    private fun emitFinal(text: String) {
        lastPartialText = ""
        if (text.isBlank()) {
            events.tryEmit(SpeechRecognitionEvent.Error("Could not recognize English speech. Try again."))
        } else {
            events.tryEmit(SpeechRecognitionEvent.FinalResult(text))
        }
    }

    private fun ensureRecognizer(): SpeechRecognizer? {
        val existing = speechRecognizer
        if (existing != null) return existing
        return try {
            SpeechRecognizer.createSpeechRecognizer(context).also { recognizer ->
                recognizer.setRecognitionListener(recognitionListener)
                speechRecognizer = recognizer
            }
        } catch (_: Exception) {
            events.tryEmit(
                SpeechRecognitionEvent.Error("Could not start speech recognition."),
            )
            null
        }
    }

    private fun recognitionIntent(): Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.US.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

    private fun hasMicrophonePermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    private fun requestMicrophonePermission() {
        val activity = CurrentActivityHolder.get() ?: return
        activity.requestPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            MICROPHONE_PERMISSION_REQUEST_CODE,
        )
    }

    private companion object {
        const val MICROPHONE_PERMISSION_REQUEST_CODE = 1001
    }
}
