package com.kholkins.englishinterlocutor

import androidx.compose.ui.window.ComposeUIViewController
import com.kholkins.englishinterlocutor.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}