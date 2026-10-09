package com.kholkins.englishinterlocutor.presentation.speech

import androidx.lifecycle.viewModelScope
import com.kholkins.englishinterlocutor.core.presentation.BaseViewModel
import com.kholkins.englishinterlocutor.domain.model.AiState
import com.kholkins.englishinterlocutor.domain.model.SpeechRecognitionEvent
import com.kholkins.englishinterlocutor.domain.model.TranslateState
import com.kholkins.englishinterlocutor.domain.usecase.AddAiDialogueUseCase
import com.kholkins.englishinterlocutor.domain.usecase.ObserveSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StartAiDialogueUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StartSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StopSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.TranslateEnToRuUseCase
import kotlinx.collections.immutable.toPersistentList
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
    private val translateEnToRuUseCase: TranslateEnToRuUseCase,
    private val startAiDialogueUseCase: StartAiDialogueUseCase,
    private val addAiDialogueUseCase: AddAiDialogueUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SpeechUiState())
    val uiState: StateFlow<SpeechUiState> = _uiState.asStateFlow()

    private var messageIdCounter = 0L

    init {
        startAiDialogue()
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

    fun revealRussianText(speechMessage:SpeechMessage){
        if (!speechMessage.hiddenRussianText.isNullOrEmpty()){
            updateMessage(speechMessage.id) {
                it.copy(
                    russianText = speechMessage.hiddenRussianText,
                    isTranslating = false,
                    isHiddenRightBubble = false
                )
            }
        }
    }

    private fun onRecognitionEvent(event: SpeechRecognitionEvent) {
        when (event) {
            SpeechRecognitionEvent.Idle -> _uiState.update {
                it.copy(isListening = false)
            }

            SpeechRecognitionEvent.Listening -> _uiState.update {
                it.copy(isListening = true, partialText = "", errorMessage = null)
            }

            is SpeechRecognitionEvent.PartialResult -> _uiState.update {
                it.copy(isListening = true, partialText = event.text)
            }

            is SpeechRecognitionEvent.FinalResult -> {
                if (event.text.isBlank()) return

                val id = messageIdCounter++
                val message = SpeechMessage(
                    id = id,
                    englishText = event.text,
                    isTranslating = true,
                )
                _uiState.update {
                    it.copy(
                        isListening = false,
                        partialText = "",
                        messages = it.messages.toPersistentList().add(message),
                        errorMessage = null,
                    )
                }
                translateEnToRu(id, event.text)
                addAiDialogue(event.text)
            }

            is SpeechRecognitionEvent.Error -> _uiState.update {
                it.copy(
                    isListening = false,
                    partialText = "",
                    errorMessage = event.message,
                )
            }
        }
    }

    private fun startAiDialogue() {
        runFlowUseCase(
            useCase = startAiDialogueUseCase,
            params = Unit,
            onEach = { state ->
                when (state) {
                    is AiState.Success -> {
                        val id = messageIdCounter++
                        val listText = state.text.split("|")
                        val message = SpeechMessage(
                            id = id,
                            englishText = listText[0],
                            hiddenRussianText = listText[1],
                            isTranslating = true,
                            isHiddenRightBubble = true
                        )
                        _uiState.update {
                            it.copy(
                                isListening = false,
                                partialText = "",
                                messages = it.messages.toPersistentList().add(message),
                                errorMessage = null
                            )
                        }
                    }
                    is AiState.Error -> {}
                    is AiState.Loading -> Unit
                }
            },
            onError = {},
        )
    }

    private fun addAiDialogue(text: String) {
        runFlowUseCase(
            useCase = addAiDialogueUseCase,
            params = text,
            onEach = { state ->
                when (state) {
                    is AiState.Success -> {
                        val id = messageIdCounter++
                        val listText = state.text.split("|")
                        val message = SpeechMessage(
                            id = id,
                            englishText = listText[0],
                            hiddenRussianText = listText[1],
                            isTranslating = true,
                            isHiddenRightBubble = true
                        )
                        _uiState.update {
                            it.copy(
                                isListening = false,
                                partialText = "",
                                messages = it.messages.toPersistentList().add(message),
                                errorMessage = null
                            )
                        }
                    }
                    is AiState.Error -> {}
                    is AiState.Loading -> Unit
                }
            },
            onError = {},
        )
    }

    private fun translateEnToRu(messageId: Long, text: String) {
        runFlowUseCase(
            useCase = translateEnToRuUseCase,
            params = text,
            onEach = { state ->
                when (state) {
                    is TranslateState.Success -> updateMessage(messageId) {
                        it.copy(russianText = state.text, isTranslating = false)
                    }
                    is TranslateState.Error -> updateMessage(messageId) {
                        it.copy(translationError = state.message, isTranslating = false)
                    }
                    is TranslateState.Loading -> Unit
                }
            },
            onError = {},
        )
    }

    private fun updateMessage(id: Long, transform: (SpeechMessage) -> SpeechMessage) {
        _uiState.update { current ->
            val persistent = current.messages.toPersistentList()
            val index = persistent.indexOfFirst { it.id == id }
            if (index == -1) current
            else current.copy(
                messages = persistent.set(index, transform(persistent[index])),
            )
        }
    }
}
