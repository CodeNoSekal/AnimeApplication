package com.dmitry.yume.presentation.screens.player.ui

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.dmitry.yume.R
import com.dmitry.yume.domain.format.formatTime
import com.dmitry.yume.presentation.screens.player.PlaybackUiState
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import androidx.media3.ui.compose.material3.Player as Media3Player


@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayer(
    playbackState: PlaybackUiState,
    exoPlayer: ExoPlayer,
    modifier: Modifier,
    isLandscape: Boolean,
    expand: () -> Unit,
    compress: () -> Unit,
    saveProgress: () -> Unit,
    onBackClick: () -> Unit,
    onPreviousEpisode: () -> Unit,
    onNextEpisode: () -> Unit,
) {
    var controlsVisible by remember { mutableStateOf(true) }

    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var playWhenReady by remember { mutableStateOf(exoPlayer.playWhenReady) }
    var exoPlaybackState by remember { mutableIntStateOf(exoPlayer.playbackState) }

    val isBuffering = playWhenReady &&
            exoPlaybackState == Player.STATE_BUFFERING

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
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(value: Boolean) {
                if (!value) {
                    saveProgress()
                }
                isPlaying = value
            }

            override fun onPlayWhenReadyChanged(value: Boolean, reason: Int) {
                playWhenReady = value
            }

            override fun onPlaybackStateChanged(state: Int) {
                exoPlaybackState = state
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
        val mainIconSize = if (isLandscape) 40.dp else 32.dp
        val sideIconSize = if (isLandscape) 26.dp else 20.dp
        val controlTouchSize = if (isLandscape) 64.dp else 48.dp
        val controlsSpacing = if (isLandscape) 28.dp else 18.dp
        val bottomPadding = if (isLandscape) 16.dp else 6.dp

        playbackState.currentUrl?.let{
            Media3Player(
                player = exoPlayer,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                controlsVisible = !controlsVisible
                            },
                            onDoubleTap = { offset ->
                                val isLeftSide = offset.x < size.width / 2f

                                if (isLeftSide) {
                                    exoPlayer.seekBack()
                                } else {
                                    exoPlayer.seekForward()
                                }

                                saveProgress()
                            }
                        )
                    },
                topControls = { _, showControls ->
                    if (scrimAlpha > 0) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = scrimAlpha))
                        )
                    }

                    if (controlsAlpha > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth().alpha(controlsAlpha),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            IconButton(
                                onClick = {
                                    saveProgress()
                                    onBackClick()
                                }
                            ) {
                                Icon(
                                    painterResource(R.drawable.angle_small_left),
                                    contentDescription = "Назад",
                                    modifier = Modifier.size(sideIconSize)
                                )
                            }
                        }
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
                                onClick = {
                                    onPreviousEpisode()
                                }
                            ) {
                                Icon(
                                    painterResource(R.drawable.step_backward),
                                    contentDescription = "seek back 10",
                                    modifier = Modifier.size(sideIconSize))
                            }

                            when {
                                isBuffering -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(mainIconSize),
                                        color = Color.White,
                                        strokeWidth = 3.dp
                                    )
                                }

                                isPlaying -> {
                                    IconButton(
                                        onClick = exoPlayer::pause,
                                        modifier = Modifier.size(controlTouchSize)
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.pause_24),
                                            contentDescription = "pause",
                                            modifier = Modifier.size(mainIconSize)
                                        )
                                    }
                                }

                                else -> {
                                    IconButton(
                                        onClick = exoPlayer::play,
                                        modifier = Modifier.size(controlTouchSize)
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.play_24),
                                            contentDescription = "play",
                                            modifier = Modifier.size(mainIconSize)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    onNextEpisode()
                                }
                            ) {
                                Icon(
                                    painterResource(R.drawable.step_forward),
                                    contentDescription = "seek forward 10",
                                    modifier = Modifier.size(sideIconSize)
                                )
                            }
                        }
                    }
                },
                bottomControls = { player, showControls ->

                    val progressState = rememberProgressStateWithTickInterval(player, 500)
                    val current = progressState.currentPositionMs
                    val duration = progressState.durationMs

                    var progress = (current.toFloat() / duration.toFloat()).coerceIn(0f, 1f)

                    if (duration <= 0)
                        progress = 0f

                    if (controlsAlpha > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .alpha(controlsAlpha),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(current),
                                style = YumeType.sm
                            )

                            PlaybackProgressBar(
                                progress,
                                onSeek = { newProgress ->
                                    exoPlayer.seekTo((duration * newProgress).toLong())
                                    saveProgress()
                                },
                                Modifier.weight(1f)
                            )

                            Text(
                                text = formatTime(duration),
                                style = YumeType.sm
                            )

                            if (isLandscape){
                                IconButton(
                                    onClick = compress
                                ) {
                                    Icon(
                                        painterResource(R.drawable.compress_24),
                                        contentDescription = "compress",
                                        modifier = Modifier.size(sideIconSize))
                                }
                            }
                            else {
                                IconButton(
                                    onClick = expand
                                ) {
                                    Icon(
                                        painterResource(R.drawable.expand_24),
                                        contentDescription = "expand",
                                        modifier = Modifier.size(sideIconSize))
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PlaybackProgressBar(
    progress: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier
) {
    var widthPx by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current
    val thumbSize = 9.dp
    val thumbSizePx = with(LocalDensity.current) {
        thumbSize.toPx()
    }
    val thumbRadiusPx = with(density) { thumbSize.toPx() / 2f }

    val trackWidthPx = (widthPx - thumbRadiusPx * 2).coerceAtLeast(0f)
    val thumbCenterPx = thumbRadiusPx + trackWidthPx * progress
    val thumbOffsetPx = (widthPx * progress - thumbSizePx / 2f).roundToInt()

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.height(28.dp)
            .onSizeChanged {widthPx = it.width}
            .pointerInput(widthPx) {
                detectTapGestures { offset ->
                    val newProgress = ((offset.x - thumbRadiusPx) / trackWidthPx).coerceIn(0f, 1f)
                    onSeek(newProgress)
                }
            }
    ) {
        Box(
            Modifier.fillMaxWidth().height(2.dp)
                .background(YumeTheme.colors.line)
        )

        Box(
            Modifier.fillMaxWidth(progress).height(2.dp)
                .background(YumeTheme.colors.accent)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(thumbOffsetPx, 0) }
                .size(thumbSize)
                .background(YumeTheme.colors.accent, CircleShape)
        )
    }
}
