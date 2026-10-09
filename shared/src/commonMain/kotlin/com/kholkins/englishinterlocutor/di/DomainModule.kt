package com.kholkins.englishinterlocutor.di

import com.kholkins.englishinterlocutor.domain.usecase.AddAiDialogueUseCase
import com.kholkins.englishinterlocutor.domain.usecase.ObserveSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StartAiDialogueUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StartSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StopSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.TranslateEnToRuUseCase
import org.koin.dsl.module

val domainModule = module {
    single { ObserveSpeechRecognitionUseCase(get()) }
    single { StartSpeechRecognitionUseCase(get()) }
    single { StopSpeechRecognitionUseCase(get()) }
    single { TranslateEnToRuUseCase(get()) }
    single { StartAiDialogueUseCase(get()) }
    single { AddAiDialogueUseCase(get()) }
}
