package com.kholkins.englishinterlocutor.di

import com.kholkins.englishinterlocutor.data.api.BackendApi
import com.kholkins.englishinterlocutor.data.network.createHttpClient
import com.kholkins.englishinterlocutor.data.repository.AndroidSpeechRecognitionRepository
import com.kholkins.englishinterlocutor.domain.repository.SpeechRecognitionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

object NetworkConfig {
    const val BASE_URL = "https://bba52grcblcbn5u16vih.containers.yandexcloud.net"
}

actual val platformDataModule: Module = module {
    single<SpeechRecognitionRepository> {
        AndroidSpeechRecognitionRepository(androidContext())
    }
    single { createHttpClient() }

    single {
        BackendApi(
            client = get(),
            baseUrl = NetworkConfig.BASE_URL
        )
    }
}
