package com.kholkins.englishinterlocutor.domain.usecase

import com.kholkins.englishinterlocutor.core.domain.FlowUseCase
import com.kholkins.englishinterlocutor.domain.model.TranslateState
import com.kholkins.englishinterlocutor.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TranslateEnToRuUseCase(
    private val translationRepository: TranslationRepository,
): FlowUseCase<String, TranslateState>(){

    override suspend fun execute(params: String): Flow<TranslateState> {
        return translationRepository.translateEnToRu(params).map { result ->
            result.fold(
                onSuccess = { TranslateState.Success(it) },
                onFailure = { error -> TranslateState.Error(error.message ?: "Ошибка") }
            )

        }
    }

}
