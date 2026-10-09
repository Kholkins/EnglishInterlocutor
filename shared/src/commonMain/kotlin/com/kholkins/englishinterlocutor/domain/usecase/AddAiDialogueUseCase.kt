package com.kholkins.englishinterlocutor.domain.usecase

import com.kholkins.englishinterlocutor.core.domain.FlowUseCase
import com.kholkins.englishinterlocutor.domain.model.AiState
import com.kholkins.englishinterlocutor.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddAiDialogueUseCase(
    private val aiRepository: AiRepository,
): FlowUseCase<String, AiState>(){

    override suspend fun execute(params: String): Flow<AiState> {
        return aiRepository.sayAi(
            "Ты - учитель английского. Выдай примерную короткую фразу для поддержки англоязычного диалога после фразы - $params. Используй выдуманне сюжетные данные. Формат ответа: фраза на английском языке|перевод этой фразы на руский"
        ).map { result ->
            result.fold(
                onSuccess = { AiState.Success(it) },
                onFailure = { error -> AiState.Error(error.message ?: "Ошибка") }
            )
        }
    }
}