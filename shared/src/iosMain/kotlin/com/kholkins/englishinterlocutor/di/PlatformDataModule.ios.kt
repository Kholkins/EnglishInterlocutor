package com.kholkins.englishinterlocutor.di

import com.kholkins.englishinterlocutor.data.api.BackendApi
import com.kholkins.englishinterlocutor.data.network.createHttpClient
import com.kholkins.englishinterlocutor.data.repository.IosSpeechRecognitionRepository
import com.kholkins.englishinterlocutor.domain.repository.SpeechRecognitionRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single<SpeechRecognitionRepository> { IosSpeechRecognitionRepository() }
    single { createHttpClient() }

    single {
        BackendApi(
            client = get(),
            baseUrl = NetworkConfig.BASE_URL
        )
    }
}
