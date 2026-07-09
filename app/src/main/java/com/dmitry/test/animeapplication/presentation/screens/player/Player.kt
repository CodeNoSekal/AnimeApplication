package com.dmitry.test.animeapplication.presentation.screens.player

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.material3.indicator.PositionAndDurationText
import androidx.media3.ui.compose.material3.indicator.ProgressSlider
import com.dmitry.test.animeapplication.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.animation.core.tween
import androidx.media3.ui.compose.material3.Player as Media3Player

@OptIn(UnstableApi::class)
@Composable
fun Player(
    playerState: PlayerUiState,
    exoPlayer: ExoPlayer,
    modifier: Modifier,
    isLandscape: Boolean
) {
    var controlsVisible by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var isSeeking by remember { mutableStateOf(false) }


    val controlsAlpha by animateFloatAsState(
        targetValue = if (controlsVisible) 1f else 0f,
        label = "controlsAlpha",
        animationSpec = tween(durationMillis = 250)
    )

    val scrimAlpha by animateFloatAsState(
        targetValue = if (controlsVisible) 0.35f else 0f,
        label = "scrimAlpha",
        animationSpec = tween(durationMillis = 250)
    )

    LaunchedEffect(controlsVisible, isPlaying, isSeeking) {
        if (controlsVisible && isPlaying && !isSeeking) {
            delay(3000.milliseconds)
            controlsVisible = false
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(value: Boolean) {
                isPlaying = value
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    Surface(
        modifier = modifier,
    ) {
        val secondButtonSize =
            if (isLandscape) 108.dp
            else 54.dp
        val firstButtonSize =
            if (isLandscape) 200.dp
            else 100.dp


        playerState.currentUrl?.let{
            Media3Player(
                player = exoPlayer,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            controlsVisible = !controlsVisible
                        }
                    },
                topControls = { _, showControls ->
                    if (scrimAlpha > 0) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = scrimAlpha))
                        )
                    }
                },
                showControls = controlsVisible,
                centerControls = { player, showControls ->
                    if (controlsAlpha > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth().alpha(controlsAlpha),
                            horizontalArrangement = Arrangement.spacedBy(
                                18.dp,
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { exoPlayer.seekBack() }
                            ) {
                                Icon(painterResource(R.drawable.replay_10_24), "seek back 10")
                            }

                            if (isPlaying){
                                IconButton(
                                    onClick = { exoPlayer.pause() }
                                ) {
                                    Icon(painterResource(R.drawable.pause_24), "pause", modifier = Modifier.size(32.dp))
                                }
                            } else {
                                IconButton(
                                    onClick = { exoPlayer.play() }
                                ) {
                                    Icon(painterResource(R.drawable.play_24), "play", modifier = Modifier.size(32.dp))
                                }
                            }

                            IconButton(
                                onClick = { exoPlayer.seekForward() }
                            ) {
                                Icon(painterResource(R.drawable.time_forward_ten_24), "seek forward 10")
                            }
                        }
                    }
                },
                bottomControls = { player, showControls ->
                    if (controlsAlpha > 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .alpha(controlsAlpha)
                        ) {
                            PositionAndDurationText(player)
                            ProgressSlider(
                                player,
                                modifier = Modifier.fillMaxWidth(),
                                onValueChange = {
                                    isSeeking = true
                                },
                                onValueChangeFinished = {
                                    isSeeking = false
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}