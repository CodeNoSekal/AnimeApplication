package com.dmitry.yume.presentation.screens.player.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dmitry.yume.presentation.screens.player.PlaybackController
import kotlinx.coroutines.delay

private const val PROGRESS_SAVE_INTERVAL_MS = 20_000L

@Composable
fun PlaybackPeriodicProgressEffect(
    controller: PlaybackController,
) {
    LaunchedEffect(controller) {
        while (true) {
            delay(PROGRESS_SAVE_INTERVAL_MS)

            if (controller.player.isPlaying) {
                controller.saveCurrentProgress()
            }
        }
    }
}
