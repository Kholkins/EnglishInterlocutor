package com.kholkins.englishinterlocutor.presentation.speech

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpeechScreen(
    viewModel: SpeechViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val displayedText = if (uiState.isListening) {
        uiState.partialText
    } else {
        uiState.recognizedText
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "English speech",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = if (uiState.isListening) {
                    "Listening…"
                } else {
                    "Hold the button and speak English"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp)
                .weight(1f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp,
        ) {
            Text(
                text = displayedText.ifBlank { "Recognized text will appear here" },
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = if (displayedText.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }

        HoldToSpeakButton(
            isListening = uiState.isListening,
            onPressed = viewModel::onHoldStart,
            onReleased = viewModel::onHoldEnd,
        )
    }
}

@Composable
private fun HoldToSpeakButton(
    isListening: Boolean,
    onPressed: () -> Unit,
    onReleased: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPressed()
                        try {
                            awaitRelease()
                        } finally {
                            onReleased()
                        }
                    },
                )
            },
        shape = RoundedCornerShape(28.dp),
        color = if (isListening) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isListening) "Release to finish" else "Hold to speak",
                style = MaterialTheme.typography.titleMedium,
                color = if (isListening) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
            )
        }
    }
}
