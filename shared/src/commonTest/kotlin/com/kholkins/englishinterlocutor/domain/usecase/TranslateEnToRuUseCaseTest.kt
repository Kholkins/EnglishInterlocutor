package com.kholkins.englishinterlocutor.domain.usecase

import app.cash.turbine.test
import com.kholkins.englishinterlocutor.domain.model.TranslateState
import com.kholkins.englishinterlocutor.domain.repository.TranslationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TranslateEnToRuUseCaseTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when translation succeeds then emits Success state`() = runTest {
        val englishText = "Hello, how are you?"
        val expectedRussian = "Привет, как дела?"

        val mockRepository = object : TranslationRepository {
            override fun translateEnToRu(text: String): Flow<Result<String>> = flow {
                emit(Result.success(expectedRussian))
            }
        }
        val useCase = TranslateEnToRuUseCase(mockRepository)

        useCase(englishText).test {
            val state = awaitItem()
            assertTrue(state is TranslateState.Success)
            assertEquals(expectedRussian, state.text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when translation fails then emits Error state`() = runTest {
        val englishText = "Hello"
        val errorMessage = "Translation service unavailable"

        val mockRepository = object : TranslationRepository {
            override fun translateEnToRu(text: String): Flow<Result<String>> = flow {
                emit(Result.failure(Exception(errorMessage)))
            }
        }
        val useCase = TranslateEnToRuUseCase(mockRepository)

        useCase(englishText).test {
            val state = awaitItem()
            assertTrue(state is TranslateState.Error)
            assertEquals(errorMessage, state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when translation fails with null message then emits Error with default message`() = runTest {
        val englishText = "Hello"

        val mockRepository = object : TranslationRepository {
            override fun translateEnToRu(text: String): Flow<Result<String>> = flow {
                emit(Result.failure(Exception("Ошибка")))
            }
        }
        val useCase = TranslateEnToRuUseCase(mockRepository)

        useCase(englishText).test {
            val state = awaitItem()
            assertTrue(state is TranslateState.Error)
            assertEquals("Ошибка", state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `translates different texts correctly`() = runTest {
        val testCases = listOf(
            "Good morning" to "Доброе утро",
            "Thank you" to "Спасибо",
            "See you later" to "Увидимся позже",
        )

        for ((english, expectedRussian) in testCases) {
            val mockRepository = object : TranslationRepository {
                override fun translateEnToRu(text: String): Flow<Result<String>> = flow {
                    emit(Result.success(expectedRussian))
                }
            }
            val useCase = TranslateEnToRuUseCase(mockRepository)

            useCase(english).test {
                val state = awaitItem()
                assertTrue(state is TranslateState.Success)
                assertEquals(expectedRussian, state.text)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `emits single state for single translation request`() = runTest {
        val englishText = "Test"

        val mockRepository = object : TranslationRepository {
            override fun translateEnToRu(text: String): Flow<Result<String>> = flow {
                emit(Result.success("Тест"))
            }
        }
        val useCase = TranslateEnToRuUseCase(mockRepository)

        useCase(englishText).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
