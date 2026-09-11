package com.kholkins.englishinterlocutor.data.repository

import com.kholkins.englishinterlocutor.domain.model.SpeechRecognitionEvent
import com.kholkins.englishinterlocutor.domain.repository.SpeechRecognitionRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.setActive
import platform.Foundation.NSLocale
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import platform.Speech.SFSpeechRecognitionTask

@OptIn(ExperimentalForeignApi::class)
class IosSpeechRecognitionRepository : SpeechRecognitionRepository {

    private val events = MutableSharedFlow<SpeechRecognitionEvent>(extraBufferCapacity = 64)
    private val recognizer = SFSpeechRecognizer(locale = NSLocale(localeIdentifier = "en-US"))

    private var audioEngine: AVAudioEngine? = null
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? = null
    private var recognitionTask: SFSpeechRecognitionTask? = null
    private var isListening = false
    private var wantsListening = false
    private var lastPartialText = ""

    override val recognition: Flow<SpeechRecognitionEvent> = events.asSharedFlow()

    override fun startListening() {
        if (isListening) return
        wantsListening = true
        if (!recognizer.isAvailable()) {
            wantsListening = false
            events.tryEmit(
                SpeechRecognitionEvent.Error("Speech recognition is not available on this device."),
            )
            return
        }

        SFSpeechRecognizer.requestAuthorization { status ->
            if (!wantsListening) return@requestAuthorization
            if (status != SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized) {
                wantsListening = false
                events.tryEmit(
                    SpeechRecognitionEvent.Error(
                        "Allow speech recognition, then hold the button and speak.",
                    ),
                )
                return@requestAuthorization
            }
            AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                if (!wantsListening) return@requestRecordPermission
                if (!granted) {
                    wantsListening = false
                    events.tryEmit(
                        SpeechRecognitionEvent.Error(
                            "Allow microphone access, then hold the button and speak.",
                        ),
                    )
                    return@requestRecordPermission
                }
                startEngine()
            }
        }
    }

    override fun stopListening() {
        wantsListening = false
        if (!isListening) return
        isListening = false
        recognitionRequest?.endAudio()
        stopAudioEngine()
    }

    private fun startEngine() {
        if (isListening) return
        if (!wantsListening) return

        runCatching { recognitionTask?.cancel() }
        recognitionTask = null
        stopAudioEngine()

        val speechRecognizer = recognizer
        val session = AVAudioSession.sharedInstance()
        val sessionError = runCatching {
            session.setCategory(AVAudioSessionCategoryRecord, error = null)
            session.setActive(true, error = null)
        }.exceptionOrNull()

        if (sessionError != null) {
            wantsListening = false
            events.tryEmit(SpeechRecognitionEvent.Error("Could not start the microphone."))
            return
        }

        val request = SFSpeechAudioBufferRecognitionRequest().apply {
            shouldReportPartialResults = true
        }
        val engine = AVAudioEngine()
        val inputNode = engine.inputNode
        val format = inputNode.outputFormatForBus(0u)

        lastPartialText = ""
        recognitionRequest = request
        audioEngine = engine
        isListening = true
        events.tryEmit(SpeechRecognitionEvent.Listening)

        recognitionTask = speechRecognizer.recognitionTaskWithRequest(request) { result, error ->
            val text = result?.bestTranscription?.formattedString.orEmpty()
            if (text.isNotBlank()) {
                lastPartialText = text
                if (result?.isFinal() == true || !isListening) {
                    emitFinal(text)
                    cleanupTask()
                } else {
                    events.tryEmit(SpeechRecognitionEvent.PartialResult(text))
                }
            }

            if (error != null && isListening.not()) {
                val fallback = lastPartialText
                if (fallback.isNotBlank()) {
                    emitFinal(fallback)
                } else {
                    events.tryEmit(
                        SpeechRecognitionEvent.Error("Could not recognize English speech. Try again."),
                    )
                }
                cleanupTask()
            }
        }

        inputNode.installTapOnBus(
            bus = 0u,
            bufferSize = 1024u,
            format = format,
        ) { buffer, _ ->
            if (buffer != null) {
                recognitionRequest?.appendAudioPCMBuffer(buffer)
            }
        }

        engine.prepare()
        val started = engine.startAndReturnError(null)
        if (!started) {
            isListening = false
            wantsListening = false
            cleanupTask()
            events.tryEmit(SpeechRecognitionEvent.Error("Could not start the microphone."))
            return
        }
        if (!wantsListening) {
            isListening = false
            recognitionRequest?.endAudio()
            stopAudioEngine()
        }
    }

    private fun emitFinal(text: String) {
        lastPartialText = ""
        if (text.isBlank()) {
            events.tryEmit(
                SpeechRecognitionEvent.Error("Could not recognize English speech. Try again."),
            )
        } else {
            events.tryEmit(SpeechRecognitionEvent.FinalResult(text))
        }
    }

    private fun stopAudioEngine() {
        val engine = audioEngine
        runCatching { engine?.stop() }
        runCatching { engine?.inputNode?.removeTapOnBus(0u) }
        audioEngine = null
        recognitionRequest = null
    }

    private fun cleanupTask() {
        isListening = false
        recognitionTask = null
        stopAudioEngine()
    }
}
