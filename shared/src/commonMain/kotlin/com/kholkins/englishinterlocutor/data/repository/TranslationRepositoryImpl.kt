package com.kholkins.englishinterlocutor.data.repository

import com.kholkins.englishinterlocutor.data.api.BackendApi
import com.kholkins.englishinterlocutor.domain.exception.NetworkException
import com.kholkins.englishinterlocutor.domain.exception.toNetworkException
import com.kholkins.englishinterlocutor.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TranslationRepositoryImpl(
    private val api: BackendApi
) : TranslationRepository {

    override fun translateEnToRu(text: String): Flow<Result<String>> = flow {
        val result = api.translate(text)
        if (result.isSuccess) {
            emit(Result.success(result.getOrNull() ?: ""))
        } else {
            val exception = result.exceptionOrNull()
            val networkException = exception?.toNetworkException()
                ?: NetworkException.TranslationException(exception)
            emit(Result.failure(networkException))
        }
    }
}
