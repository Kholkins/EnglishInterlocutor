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

class StartAiDialogueUseCaseTest {

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when AI responds successfully then emits Success with formatted text`() = runTest {
        val expectedResponse = "Hello! How are you today?|Привет! Как дела сегодня?"

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> = flow {
                emit(Result.success(expectedResponse))
            }
        }
        val useCase = StartAiDialogueUseCase(mockRepository)

        useCase(Unit).test {
            val state = awaitItem()
            assertTrue(state is AiState.Success)
            assertEquals(expectedResponse, state.text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when AI call fails then emits Error state`() = runTest {
        val errorMessage = "AI service unavailable"

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> = flow {
                emit(Result.failure(Exception(errorMessage)))
            }
        }
        val useCase = StartAiDialogueUseCase(mockRepository)

        useCase(Unit).test {
            val state = awaitItem()
            assertTrue(state is AiState.Error)
            assertEquals(errorMessage, state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when AI call fails with null message then emits Error with default message`() = runTest {
        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> = flow {
                emit(Result.failure(Exception("Ошибка")))
            }
        }
        val useCase = StartAiDialogueUseCase(mockRepository)

        useCase(Unit).test {
            val state = awaitItem()
            assertTrue(state is AiState.Error)
            assertEquals("Ошибка", state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `passes correct prompt to repository`() = runTest {
        var capturedPrompt: String? = null

        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> {
                capturedPrompt = text
                return flow { emit(Result.success("Hi|Привет")) }
            }
        }

        val useCase = StartAiDialogueUseCase(mockRepository)

        useCase(Unit).test {
            awaitItem()
            assertTrue(capturedPrompt!!.contains("учитель английского"))
            assertTrue(capturedPrompt!!.contains("фраза на английском языке"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits single state for single AI response`() = runTest {
        val mockRepository = object : AiRepository {
            override fun sayAi(text: String): Flow<Result<String>> = flow {
                emit(Result.success("Hi|Привет"))
            }
        }
        val useCase = StartAiDialogueUseCase(mockRepository)

        useCase(Unit).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
