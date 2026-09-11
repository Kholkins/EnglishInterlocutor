package com.kholkins.englishinterlocutor.di

import com.kholkins.englishinterlocutor.data.repository.IosSpeechRecognitionRepository
import com.kholkins.englishinterlocutor.domain.repository.SpeechRecognitionRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single<SpeechRecognitionRepository> { IosSpeechRecognitionRepository() }
}
