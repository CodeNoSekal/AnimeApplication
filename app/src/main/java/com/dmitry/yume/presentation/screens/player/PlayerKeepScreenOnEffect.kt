package com.dmitry.yume.presentation.screens.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.media3.common.Player

@Composable
fun PlayerKeepScreenOnEffect(
    controller: PlayerController
) {
    val view = LocalView.current

    DisposableEffect(view, controller) {
        val originalKeepScreenOn = view.keepScreenOn

        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                view.keepScreenOn = isPlaying
            }
        }

        view.keepScreenOn = controller.player.isPlaying
        controller.player.addListener(listener)

        onDispose {
            controller.player.removeListener(listener)
            view.keepScreenOn = originalKeepScreenOn
        }
    }
}