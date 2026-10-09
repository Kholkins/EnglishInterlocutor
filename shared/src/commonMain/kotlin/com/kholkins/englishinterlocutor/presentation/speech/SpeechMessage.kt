package com.kholkins.englishinterlocutor.presentation.speech

import androidx.compose.runtime.Stable

@Stable
data class SpeechMessage(
    val id: Long,
    val englishText: String,
    val russianText: String? = null,
    val hiddenRussianText: String? = null,
    val isTranslating: Boolean = false,
    val isHiddenRightBubble: Boolean = false,
    val translationError: String? = null,
)
