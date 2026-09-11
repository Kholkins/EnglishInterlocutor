package com.kholkins.englishinterlocutor.di

import com.kholkins.englishinterlocutor.domain.usecase.ObserveSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StartSpeechRecognitionUseCase
import com.kholkins.englishinterlocutor.domain.usecase.StopSpeechRecognitionUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { ObserveSpeechRecognitionUseCase(get()) }
    factory { StartSpeechRecognitionUseCase(get()) }
    factory { StopSpeechRecognitionUseCase(get()) }
}
