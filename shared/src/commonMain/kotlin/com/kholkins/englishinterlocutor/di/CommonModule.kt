package com.kholkins.englishinterlocutor.di

import com.kholkins.englishinterlocutor.data.repository.TranslationRepositoryImpl
import com.kholkins.englishinterlocutor.domain.repository.TranslationRepository
import org.koin.dsl.module

val commonModule = module {
    single<TranslationRepository> { TranslationRepositoryImpl(api = get()) }
}