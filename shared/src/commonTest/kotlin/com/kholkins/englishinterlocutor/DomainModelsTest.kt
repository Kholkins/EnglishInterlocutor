package com.kholkins.englishinterlocutor

import com.kholkins.englishinterlocutor.domain.model.AiState
import com.kholkins.englishinterlocutor.domain.model.SpeechRecognitionEvent
import com.kholkins.englishinterlocutor.domain.model.TranslateState
import com.kholkins.englishinterlocutor.presentation.speech.SpeechMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Тесты для domain models - проверка sealed interfaces и data class свойств.
 */
class DomainModelsTest {

    @Test
    fun `AiState Success contains text`() {
        val text = "Hello|Привет"
        val state = AiState.Success(text)

        assertTrue(state is AiState.Success)
        assertEquals(text, state.text)
    }

    @Test
    fun `AiState Error contains error message`() {
        val message = "Network error"
        val state = AiState.Error(message)

        assertTrue(state is AiState.Error)
        assertEquals(message, state.message)
    }

    @Test
    fun `AiState Loading is singleton`() {
        val state1 = AiState.Loading
        val state2 = AiState.Loading

        assertTrue(state1 is AiState.Loading)
        assertTrue(state1 === state2)
    }

    @Test
    fun `AiState is exhaustive sealed interface`() {
        val states: List<AiState> = listOf(
            AiState.Loading,
            AiState.Success("Hi|Привет"),
            AiState.Error("Error")
        )

        assertEquals(3, states.size)

        states.forEach { state ->
            when (state) {
                is AiState.Loading -> assertTrue(true)
                is AiState.Success -> assertTrue(state.text.isNotEmpty())
                is AiState.Error -> assertTrue(state.message.isNotEmpty())
            }
        }
    }

    @Test
    fun `TranslateState Success contains translated text`() {
        val text = "Привет"
        val state = TranslateState.Success(text)

        assertTrue(state is TranslateState.Success)
        assertEquals(text, state.text)
    }

    @Test
    fun `TranslateState Error contains error message`() {
        val message = "Translation failed"
        val state = TranslateState.Error(message)

        assertTrue(state is TranslateState.Error)
        assertEquals(message, state.message)
    }

    @Test
    fun `TranslateState Loading is singleton`() {
        val state1 = TranslateState.Loading
        val state2 = TranslateState.Loading

        assertTrue(state1 is TranslateState.Loading)
        assertTrue(state1 === state2)
    }

    @Test
    fun `SpeechRecognitionEvent Idle is singleton`() {
        val state1 = SpeechRecognitionEvent.Idle
        val state2 = SpeechRecognitionEvent.Idle

        assertTrue(state1 is SpeechRecognitionEvent.Idle)
        assertTrue(state1 === state2)
    }

    @Test
    fun `SpeechRecognitionEvent Listening is singleton`() {
        val state1 = SpeechRecognitionEvent.Listening
        val state2 = SpeechRecognitionEvent.Listening

        assertTrue(state1 is SpeechRecognitionEvent.Listening)
        assertTrue(state1 === state2)
    }

    @Test
    fun `SpeechRecognitionEvent PartialResult contains text`() {
        val text = "Hello, how"
        val event = SpeechRecognitionEvent.PartialResult(text)

        assertTrue(event is SpeechRecognitionEvent.PartialResult)
        assertEquals(text, event.text)
    }

    @Test
    fun `SpeechRecognitionEvent FinalResult contains text`() {
        val text = "How are you?"
        val event = SpeechRecognitionEvent.FinalResult(text)

        assertTrue(event is SpeechRecognitionEvent.FinalResult)
        assertEquals(text, event.text)
    }

    @Test
    fun `SpeechRecognitionEvent Error contains message`() {
        val message = "Speech recognition failed"
        val event = SpeechRecognitionEvent.Error(message)

        assertTrue(event is SpeechRecognitionEvent.Error)
        assertEquals(message, event.message)
    }

    @Test
    fun `SpeechRecognitionEvent is exhaustive sealed interface`() {
        val events: List<SpeechRecognitionEvent> = listOf(
            SpeechRecognitionEvent.Idle,
            SpeechRecognitionEvent.Listening,
            SpeechRecognitionEvent.PartialResult("partial"),
            SpeechRecognitionEvent.FinalResult("final"),
            SpeechRecognitionEvent.Error("error")
        )

        assertEquals(5, events.size)

        var idleCount = 0
        var listeningCount = 0
        var partialCount = 0
        var finalCount = 0
        var errorCount = 0

        events.forEach { event ->
            when (event) {
                SpeechRecognitionEvent.Idle -> idleCount++
                SpeechRecognitionEvent.Listening -> listeningCount++
                is SpeechRecognitionEvent.PartialResult -> {
                    partialCount++
                    assertTrue(event.text.isNotEmpty())
                }
                is SpeechRecognitionEvent.FinalResult -> {
                    finalCount++
                    assertTrue(event.text.isNotEmpty())
                }
                is SpeechRecognitionEvent.Error -> {
                    errorCount++
                    assertTrue(event.message.isNotEmpty())
                }
            }
        }

        assertEquals(1, idleCount)
        assertEquals(1, listeningCount)
        assertEquals(1, partialCount)
        assertEquals(1, finalCount)
        assertEquals(1, errorCount)
    }

    @Test
    fun `SpeechMessage has correct properties`() {
        val message = SpeechMessage(
            id = 1L,
            englishText = "Hello",
            russianText = "Привет",
            hiddenRussianText = "Скрытый перевод",
            isTranslating = false,
            isHiddenRightBubble = true,
            translationError = null
        )

        assertEquals(1L, message.id)
        assertEquals("Hello", message.englishText)
        assertEquals("Привет", message.russianText)
        assertEquals("Скрытый перевод", message.hiddenRussianText)
        assertFalse(message.isTranslating)
        assertTrue(message.isHiddenRightBubble)
        assertNull(message.translationError)
    }

    @Test
    fun `SpeechMessage default values are correct`() {
        val message = SpeechMessage(id = 1L, englishText = "Hello")

        assertEquals(1L, message.id)
        assertEquals("Hello", message.englishText)
        assertNull(message.russianText)
        assertNull(message.hiddenRussianText)
        assertFalse(message.isTranslating)
        assertFalse(message.isHiddenRightBubble)
        assertNull(message.translationError)
    }

    @Test
    fun `SpeechMessage copy works correctly`() {
        val original = SpeechMessage(id = 1L, englishText = "Hello")
        val updated = original.copy(russianText = "Привет", isTranslating = false)

        assertEquals(original.id, updated.id)
        assertEquals(original.englishText, updated.englishText)
        assertEquals("Привет", updated.russianText)
        assertFalse(updated.isTranslating)
    }

    @Test
    fun `SpeechMessage equals and hashCode`() {
        val message1 = SpeechMessage(id = 1L, englishText = "Hello")
        val message2 = SpeechMessage(id = 1L, englishText = "Hello")
        val message3 = SpeechMessage(id = 2L, englishText = "Hello")

        assertEquals(message1, message2)
        assertFalse(message1 == message3)
        assertEquals(message1.hashCode(), message2.hashCode())
    }

    @Test
    fun `SpeechMessage toString contains properties`() {
        val message = SpeechMessage(id = 1L, englishText = "Hello", russianText = "Привет")
        val toString = message.toString()

        assertTrue(toString.contains("SpeechMessage"))
        assertTrue(toString.contains("1"))
        assertTrue(toString.contains("Hello"))
    }
}
