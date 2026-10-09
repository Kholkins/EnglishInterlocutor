package com.kholkins.englishinterlocutor.domain.usecase

import app.cash.turbine.test
import com.kholkins.englishinterlocutor.domain.model.AiState
import com.kholkins.englishinterlocutor.domain.repository.AiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddAiDialogueUseCaseTest {

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when AI responds successfully then emits Success with formatted text`() = runTest {
        val userText = "How are you?"
        val expectedResponse = "I'm fine, thanks!|У меня всё хорошо, спасибо!"

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> = flow {
                emit(Result.success(expectedResponse))
            }
        }
        val useCase = AddAiDialogueUseCase(mockRepository)

        useCase(userText).test {
            val state = awaitItem()
            assertTrue(state is AiState.Success)
            assertEquals(expectedResponse, state.text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when AI call fails then emits Error state`() = runTest {
        val userText = "Hello"
        val errorMessage = "Network error"

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> = flow {
                emit(Result.failure(Exception(errorMessage)))
            }
        }
        val useCase = AddAiDialogueUseCase(mockRepository)

        useCase(userText).test {
            val state = awaitItem()
            assertTrue(state is AiState.Error)
            assertEquals(errorMessage, state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `includes user text in prompt sent to AI`() = runTest {
        val userText = "What is your favorite color?"
        var capturedPrompt: String? = null

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> {
                capturedPrompt = text
                return flow { emit(Result.success("I like blue|Мне нравится синий")) }
            }
        }
        val useCase = AddAiDialogueUseCase(mockRepository)

        useCase(userText).test {
            awaitItem()
            assertTrue(capturedPrompt!!.contains(userText))
            assertTrue(capturedPrompt!!.contains("поддержки англоязычного диалога"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when AI call fails with null message then emits Error with default message`() = runTest {
        val userText = "Test"

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> = flow {
                emit(Result.failure(Exception("Ошибка")))
            }
        }
        val useCase = AddAiDialogueUseCase(mockRepository)

        useCase(userText).test {
            val state = awaitItem()
            assertTrue(state is AiState.Error)
            assertEquals("Ошибка", state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits single state for single AI response`() = runTest {
        val userText = "Nice to meet you"

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> = flow {
                emit(Result.success("Nice to meet you too|Рад познакомиться"))
            }
        }
        val useCase = AddAiDialogueUseCase(mockRepository)

        useCase(userText).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handles empty user text`() = runTest {
        var capturedPrompt: String? = null

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> {
                capturedPrompt = text
                return flow { emit(Result.success("Hello|Привет")) }
            }
        }
        val useCase = AddAiDialogueUseCase(mockRepository)

        useCase("").test {
            awaitItem()
            assertTrue(capturedPrompt!!.contains("Формат ответа:"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
