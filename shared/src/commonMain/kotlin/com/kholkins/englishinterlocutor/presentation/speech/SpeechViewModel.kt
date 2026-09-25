package com.kholkins.englishinterlocutor.presentation.speech

import androidx.lifecycle.viewModelScope
import com.kholkins.englishinterlocutor.core.presentation.BaseViewModel
import com.kholkins.englishinterlocutor.domain.model.SpeechRecognitionEvent
import com.kholkins.englishinterlocutor.domain.model.TranslateState
import com.kholkins.englishinterlocutor.domain.usecase.ObserveSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StartSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StopSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.TranslateEnToRuUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SpeechViewModel(
    observeSpeechRecognitionUseCase: ObserveSpeechRecognitionUseCase,
    private val startSpeechRecognitionUseCase: StartSpeechRecognitionUseCase,
    private val stopSpeechRecognitionUseCase: StopSpeechRecognitionUseCase,
    private val translateEnToRuUseCase: TranslateEnToRuUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SpeechUiState())
    val uiState: StateFlow<SpeechUiState> = _uiState.asStateFlow()

    init {
        observeSpeechRecognitionUseCase()
            .onEach(::onRecognitionEvent)
            .launchIn(viewModelScope)
    }

    fun onHoldStart() {
        startSpeechRecognitionUseCase()
    }

    fun onHoldEnd() {
        stopSpeechRecognitionUseCase()
    }

    override fun onCleared() {
        stopSpeechRecognitionUseCase()
        super.onCleared()
    }

    private fun onRecognitionEvent(event: SpeechRecognitionEvent) {
        _uiState.update { current ->
            when (event) {
                SpeechRecognitionEvent.Idle -> current.copy(isListening = false)
                SpeechRecognitionEvent.Listening -> current.copy(
                    isListening = true,
                    partialText = "",
                    errorMessage = null,
                )
                is SpeechRecognitionEvent.PartialResult -> current.copy(
                    isListening = true,
                    partialText = event.text,
                )
                is SpeechRecognitionEvent.FinalResult -> {
                    translateEnToRu(event.text)
                    current.copy(
                        isListening = false,
                        recognizedText = event.text,
                        partialText = "",
                        errorMessage = null,
                    )
                }
                is SpeechRecognitionEvent.Error -> current.copy(
                    isListening = false,
                    partialText = "",
                    errorMessage = event.message,
                )
            }
        }
    }

    private fun translateEnToRu(text: String){
        runFlowUseCase(
            useCase = translateEnToRuUseCase,
            params = text,
            onEach = { translatedState ->
                when (translatedState) {
                    is TranslateState.Success -> {
                        _uiState.update { current ->
                            current.copy(
                                translatedText = translatedState.text
                            )
                        }
                    }
                    is TranslateState.Error -> {
                        _uiState.update { current ->
                            current.copy(
                                errorMessage = translatedState.message
                            )
                        }
                    }
                    is TranslateState.Loading -> {}
                }

            },
            onError = {}
        )
    }
}
