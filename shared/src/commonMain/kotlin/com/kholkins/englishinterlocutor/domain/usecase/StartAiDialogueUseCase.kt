package com.kholkins.englishinterlocutor.domain.usecase

import com.kholkins.englishinterlocutor.core.domain.FlowUseCase
import com.kholkins.englishinterlocutor.domain.model.AiState
import com.kholkins.englishinterlocutor.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StartAiDialogueUseCase(
    private val aiRepository: AiRepository,
): FlowUseCase<Unit, AiState>(){

    override suspend fun execute(params: Unit): Flow<AiState> {
        return aiRepository.sayAi(
            "Ты - учитель английского. Выдай фразу для начала англоязычного диалога на распространенную тему. Формат ответа: фраза на английском языке|перевод этой фразы на руский"
        ).map { result ->
            result.fold(
                onSuccess = { AiState.Success(it) },
                onFailure = { error -> AiState.Error(error.message ?: "Ошибка") }
            )
        }
    }
}