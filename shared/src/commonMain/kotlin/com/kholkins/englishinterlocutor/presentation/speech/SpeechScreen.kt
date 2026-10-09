package com.kholkins.englishinterlocutor.presentation.speech

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kholkins.englishinterlocutor.presentation.theme.AppCoral
import com.kholkins.englishinterlocutor.presentation.theme.AppIndigo
import com.kholkins.englishinterlocutor.presentation.theme.EnglishBubbleColor
import com.kholkins.englishinterlocutor.presentation.theme.EnglishBubbleText
import com.kholkins.englishinterlocutor.presentation.theme.RussianBubbleColor
import com.kholkins.englishinterlocutor.presentation.theme.RussianBubbleText
import englishinterlocutor.shared.generated.resources.Res
import englishinterlocutor.shared.generated.resources.blank_text_message
import englishinterlocutor.shared.generated.resources.hold_button
import englishinterlocutor.shared.generated.resources.hold_to_speak
import englishinterlocutor.shared.generated.resources.listening_message
import englishinterlocutor.shared.generated.resources.release_to_finish
import englishinterlocutor.shared.generated.resources.speech_screen_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpeechScreen(
    viewModel: SpeechViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val tailCount = uiState.messages.size +
            if (uiState.isListening && uiState.partialText.isNotBlank()) 1 else 0

    LaunchedEffect(tailCount) {
        if (tailCount > 0) {
            listState.animateScrollToItem(tailCount - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding(),
    ) {
        FeedHeader(isListening = uiState.isListening)

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.messages.isEmpty() && !uiState.isListening) {
                EmptyFeed(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items = uiState.messages, key = { it.id }) { message ->
                        val revealRussianText = remember(message) {
                            { viewModel.revealRussianText(message) }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            MessageBubble(
                                text = message.englishText,
                                label = "EN",
                                onClick = revealRussianText,
                                labelColor = AppIndigo,
                                bubbleColor = EnglishBubbleColor,
                                textColor = EnglishBubbleText,
                                modifier = Modifier.weight(1f),
                            )
                            ArrowColumn()
                            RightBubble(
                                isVisible = !message.isHiddenRightBubble,
                                message = message,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    if (uiState.isListening && uiState.partialText.isNotBlank()) {
                        item(key = "partial") {
                            PartialMessageCard(
                                text = uiState.partialText
                            )
                        }
                    }
                }
            }
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                textAlign = TextAlign.Center,
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
private fun FeedHeader(isListening: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AppIndigo, AppCoral)),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "EN",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.speech_screen_name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (isListening) {
                        stringResource(Res.string.listening_message)
                    } else {
                        stringResource(Res.string.hold_button)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            ListeningDot(active = isListening)
        }
    }
}

@Composable
private fun ListeningDot(active: Boolean) {
    val alpha by animateFloatAsState(targetValue = if (active) 1f else 0.25f)
    Box(
        modifier = Modifier
            .size(10.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(AppCoral),
    )
}

@Composable
private fun MessageBubble(
    text: String,
    label: String,
    onClick: () -> Unit,
    labelColor: Color,
    bubbleColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = bubbleColor,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun RightBubble(isVisible: Boolean, message: SpeechMessage, modifier: Modifier = Modifier) {
    Surface(
        modifier = if (isVisible) modifier else modifier.alpha(0f),
        color = RussianBubbleColor,
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = 62.dp)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Text(
                text = "RU",
                color = AppCoral,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))

            when {
                message.isTranslating -> Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = AppCoral,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "…",
                        color = RussianBubbleText,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                message.translationError != null -> Text(
                    text = message.translationError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )

                else -> Text(
                    text = message.russianText.orEmpty(),
                    color = RussianBubbleText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ArrowColumn() {
    Text(
        text = "→",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 20.dp),
    )
}

@Composable
private fun PartialMessageCard(text: String, hidden: Boolean = false) {
    AnimatedVisibility(
        visible = !hidden,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            MessageBubble(
                text = text,
                label = "EN • распознаётся",
                onClick = {},
                labelColor = AppIndigo,
                bubbleColor = EnglishBubbleColor.copy(alpha = 0.6f),
                textColor = EnglishBubbleText,
                modifier = Modifier.weight(1f),
            )
            ArrowColumn()
            Surface(
                modifier = Modifier.weight(1f),
                color = RussianBubbleColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp),
            ) {
                Row(
                    modifier = Modifier
                        .heightIn(min = 62.dp)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = AppCoral,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "перевод…",
                        color = RussianBubbleText.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFeed(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🎙",
            fontSize = 48.sp,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.blank_text_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.hold_to_speak),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HoldToSpeakButton(
    isListening: Boolean,
    onPressed: () -> Unit,
    onReleased: () -> Unit,
) {
    val scale by animateFloatAsState(targetValue = if (isListening) 0.97f else 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
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
            color = if (isListening) AppCoral else AppIndigo,
            shadowElevation = 6.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (isListening) {
                            stringResource(Res.string.release_to_finish)
                        } else {
                            stringResource(Res.string.hold_to_speak)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
