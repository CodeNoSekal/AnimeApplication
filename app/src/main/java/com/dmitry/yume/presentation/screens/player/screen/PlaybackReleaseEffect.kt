package com.dmitry.yume.presentation.screens.player.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.dmitry.yume.presentation.screens.player.PlaybackController

@Composable
fun PlaybackReleaseEffect(
    controller: PlaybackController
) {
    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }
}
