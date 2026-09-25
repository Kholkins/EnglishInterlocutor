package com.kholkins.englishinterlocutor.di

import com.kholkins.englishinterlocutor.presentation.speech.SpeechViewModel
import org.koin.dsl.module

val presentationModule = module {
    factory {
        SpeechViewModel(
            get(),
            get(),
            get(),
            get()
        ) }
}
