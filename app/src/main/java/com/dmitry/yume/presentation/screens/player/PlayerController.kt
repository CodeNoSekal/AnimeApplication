package com.dmitry.yume.presentation.screens.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.util.UUID

@Stable
class PlayerController(
    val player: ExoPlayer,
    private val onSaveProgress:
        (PlaybackContext, Long, Long) -> Unit
) {
    private val contexts =
        mutableMapOf<String, PlaybackContext>()

    fun replaceMedia(
        url: String,
        context: PlaybackContext,
        startPositionMs: Long
    ) {
        saveCurrentProgress()

        val mediaId = UUID.randomUUID().toString()
        contexts.clear()
        contexts[mediaId] = context

        val mediaItem = MediaItem.Builder()
            .setMediaId(mediaId)
            .setUri(url)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.seekTo(startPositionMs)
        player.playWhenReady = true
    }

    fun saveCurrentProgress() {
        val mediaId = player.currentMediaItem?.mediaId ?: return
        val context = contexts[mediaId] ?: return

        onSaveProgress(
            context,
            player.currentPosition,
            player.duration
        )
    }

    fun release() {
        saveCurrentProgress()
        player.release()
        contexts.clear()
    }
}

@Composable
fun rememberPlayerController(
    onSaveProgress:
        (PlaybackContext, Long, Long) -> Unit
): PlayerController {
    val context = LocalContext.current
    val latestSaveProgress by rememberUpdatedState(onSaveProgress)

    return remember {
        PlayerController(
            player = ExoPlayer.Builder(context)
                .setSeekBackIncrementMs(10_000)
                .setSeekForwardIncrementMs(10_000)
                .build(),
            onSaveProgress = { playback, position, duration ->
                latestSaveProgress(playback, position, duration)
            }
        )
    }
}