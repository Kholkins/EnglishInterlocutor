package com.kholkins.englishinterlocutor.di

import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    try {
        startKoin {
            appDeclaration()
            modules(appModules + platformDataModule + commonModule)
        }
    } catch (_: KoinApplicationAlreadyStartedException) {
        // Already initialized (e.g. configuration change on Android)
    }
}
