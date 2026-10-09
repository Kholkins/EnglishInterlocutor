package com.kholkins.englishinterlocutor.domain.model

sealed interface AiState {
    data object Loading : AiState
    data class Success(val text: String) : AiState
    data class Error(val message: String) : AiState
}