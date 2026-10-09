package com.kholkins.englishinterlocutor.data.repository

import com.kholkins.englishinterlocutor.data.api.BackendApi
import com.kholkins.englishinterlocutor.domain.repository.AiRepository
import com.kholkins.englishinterlocutor.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AiRepositoryImpl(
    private val api: BackendApi
) : AiRepository {

    override fun sayAi(text: String): Flow<Result<String>> = flow {
        val result = api.aiChat(text)
        if (result.isSuccess) {
            emit(Result.success(result.getOrNull() ?: ""))
        } else {
            emit(Result.failure(result.exceptionOrNull() ?: Exception("AI недоступен")))
        }
    }
}