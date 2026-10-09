package com.kholkins.englishinterlocutor.domain.repository

import kotlinx.coroutines.flow.Flow

interface AiRepository {
    fun sayAi(text: String): Flow<Result<String>>
}