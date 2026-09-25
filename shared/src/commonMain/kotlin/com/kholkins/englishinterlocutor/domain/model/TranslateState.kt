package com.kholkins.englishinterlocutor.domain.model

sealed interface TranslateState {
    data object Loading : TranslateState
    data class Success(val text: String) : TranslateState
    data class Error(val message: String) : TranslateState
}