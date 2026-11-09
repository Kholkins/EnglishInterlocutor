package com.kholkins.englishinterlocutor.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kholkins.englishinterlocutor.presentation.theme.AppCoral
import com.kholkins.englishinterlocutor.presentation.theme.AppIndigo
import com.kholkins.englishinterlocutor.presentation.theme.AppIndigoLight
import com.kholkins.englishinterlocutor.presentation.theme.AppMuted
import englishinterlocutor.shared.generated.resources.Res
import englishinterlocutor.shared.generated.resources.again
import englishinterlocutor.shared.generated.resources.confirm
import englishinterlocutor.shared.generated.resources.confirm_or_try
import englishinterlocutor.shared.generated.resources.recognition_completed
import englishinterlocutor.shared.generated.resources.yo_said
import org.jetbrains.compose.resources.stringResource

@Immutable
data class SpeechRecognitionConfirmationState(
    val recognizedText: String = "",
    val isVisible: Boolean = false
)

@Composable
fun SpeechRecognitionConfirmationDialog(
    state: SpeechRecognitionConfirmationState,
    onRetry: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onRetry
) {
    BaseDialog(
        visible = state.isVisible,
        title = stringResource(Res.string.recognition_completed),
        onDismiss = onDismiss,
        content = {
            SpeechRecognitionConfirmationContent(
                recognizedText = state.recognizedText,
                onRetry = onRetry,
                onConfirm = onConfirm
            )
        }
    )
}

@Composable
fun SpeechRecognitionConfirmationContent(
    recognizedText: String,
    onRetry: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 380.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(modifier = Modifier.weight(2f))
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(AppIndigo, AppCoral)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎤",
                fontSize = 36.sp
            )
        }

        Spacer(modifier = Modifier.weight(3f))

        Text(
            text = stringResource(Res.string.yo_said),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AppMuted,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = AppIndigoLight
            ),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 130.dp)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = recognizedText,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = AppIndigo,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(2f))

        Text(
            text = stringResource(Res.string.confirm_or_try),
            style = MaterialTheme.typography.labelMedium.copy(
                color = AppMuted
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(2f))

        Column(
            modifier = Modifier.height( 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DialogButtonWithIcon(
                icon = "🔄",
                text = stringResource(Res.string.again),
                onClick = onRetry,
                primary = false,
                modifier = Modifier.weight(1f)
            )

            DialogButtonWithIcon(
                icon = "✓",
                text = stringResource(Res.string.confirm),
                onClick = onConfirm,
                primary = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Preview(showBackground = true)
@Composable
private fun SpeechRecognitionConfirmationDialogPreview() {
    MaterialTheme {
        SpeechRecognitionConfirmationContent(
            recognizedText = "Привет, это тест распознаванияПривет, это тест распознаванияПривет, это тест распознаванияПривет, это тест распознаванияПривет, это тест распознавания",
            onRetry = {},
            onConfirm = {}
        )
    }
}
