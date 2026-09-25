package com.kholkins.englishinterlocutor.domain.repository

import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    fun translateEnToRu(text: String): Flow<Result<String>>
}