package com.kholkins.englishinterlocutor.presentation.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// Палитра приложения
val AppIndigo = Color(0xFF6366F1)
val AppIndigoLight = Color(0xFFE0E7FF)
val AppIndigoDark = Color(0xFF312E81)

val AppCoral = Color(0xFFF97316)
val AppCoralLight = Color(0xFFFFEDD5)
val AppCoralDark = Color(0xFF7C2D12)

val AppBackground = Color(0xFFF7F8FC)
val AppSurface = Color(0xFFFFFFFF)
val AppOnSurface = Color(0xFF1F2937)
val AppMuted = Color(0xFF6B7280)

// Цвета «пузырей» в ленте
val EnglishBubbleColor = Color(0xFFE0E7FF)   // мягкий индиго
val EnglishBubbleText = Color(0xFF1E1B4B)
val RussianBubbleColor = Color(0xFFFFEDD5)   // мягкий коралловый
val RussianBubbleText = Color(0xFF7C2D12)
val DividerColor = Color(0xFFE5E7EB)

private val AppColorScheme = lightColorScheme(
    primary = AppIndigo,
    onPrimary = Color.White,
    primaryContainer = AppIndigoLight,
    onPrimaryContainer = AppIndigoDark,
    secondary = AppCoral,
    onSecondary = Color.White,
    secondaryContainer = AppCoralLight,
    onSecondaryContainer = AppCoralDark,
    background = AppBackground,
    onBackground = AppOnSurface,
    surface = AppSurface,
    onSurface = AppOnSurface,
    surfaceVariant = Color(0xFFF1F3F8),
    onSurfaceVariant = AppMuted,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColorScheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
