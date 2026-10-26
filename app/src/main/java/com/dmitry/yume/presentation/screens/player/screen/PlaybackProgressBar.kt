package com.dmitry.yume.presentation.screens.player.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun PlaybackProgressBar(
    progress: Float,
    onSeekStart: (Float) -> Unit,
    onSeekPreview: (Float) -> Unit,
    onSeekEnd: (Float) -> Unit,
    onSeekCancel: (Float) -> Unit,
    modifier: Modifier,
    isLandscape: Boolean,
) {
    var widthPx by remember { mutableIntStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }

    var displayedProgress by remember {
        mutableFloatStateOf(progress.coerceIn(0f,1f))
    }

    LaunchedEffect(progress, isDragging) {
        if (!isDragging) {
            displayedProgress = progress.coerceIn(0f, 1f)
        }
    }

    val height = if (isLandscape) 30.dp else 10.dp

    val alignment =
        if (isLandscape) Alignment.CenterStart
        else Alignment.BottomStart

    val thickness = if (isLandscape) 4.dp else 3.dp

    Box(
        contentAlignment = alignment,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .onSizeChanged {
                widthPx = it.width
            }
            .pointerInput(widthPx) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)

                    if (widthPx <= 0){
                        return@awaitEachGesture
                    }

                    fun updateProgress(x: Float) {
                        displayedProgress =
                            (x / widthPx.toFloat()).coerceIn(0f, 1f)
                    }

                    isDragging = true
                    var completed = false

                    try {
                        updateProgress(down.position.x)
                        onSeekStart(displayedProgress)
                        onSeekPreview(displayedProgress)
                        down.consume()

                        var pressed = true

                        while (pressed) {
                            val event = awaitPointerEvent()
                            val change = event.changes
                                .firstOrNull { it.id == down.id }
                                ?: break

                            updateProgress(change.position.x)
                            onSeekPreview(displayedProgress)
                            pressed = change.pressed
                            change.consume()
                        }

                        completed = true
                        onSeekEnd(displayedProgress)
                    } finally {
                        if (!completed) {
                            onSeekCancel(displayedProgress)
                        }
                        isDragging = false
                    }
                }
            }
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(thickness)
                .clip(
                    RoundedCornerShape(
                        if (isLandscape) thickness else 0.dp
                    )
                )
                .background(YumeTheme.colors.line)
        )

        Box(
            Modifier
                .fillMaxWidth(displayedProgress)
                .height(thickness)
                .clip(
                    RoundedCornerShape(
                        if (isLandscape) thickness else 0.dp
                    )
                )
                .background(YumeTheme.colors.accent)
        )
    }
}
