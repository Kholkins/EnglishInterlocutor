package com.kholkins.englishinterlocutor

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kholkins.englishinterlocutor.presentation.navigation.AppNavigation
import com.kholkins.englishinterlocutor.presentation.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        AppNavigation()
    }
}
