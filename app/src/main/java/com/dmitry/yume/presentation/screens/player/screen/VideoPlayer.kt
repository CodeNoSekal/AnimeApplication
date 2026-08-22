package com.dmitry.yume.presentation.screens.player.screen

import android.os.SystemClock
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.dmitry.yume.R
import com.dmitry.yume.domain.format.formatTime
import com.dmitry.yume.presentation.screens.player.PlaybackStage
import com.dmitry.yume.presentation.screens.player.PlaybackUiState
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import androidx.media3.ui.compose.material3.Player as Media3Player
import androidx.core.net.toUri
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors


private enum class SeekSide {
    Left,
    Right
}

private data class SeekFeedback(
    val side: SeekSide,
    val seconds: Int
)

private data class PlayerFailure(
    val message: String,
    val requiresFreshStream: Boolean,
)

private const val PLAYER_LOG_TAG = "YumePlayer"
private const val BUFFERING_TIMEOUT_MS = 20_000L

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
    refreshStream: () -> Unit,
    onBackClick: () -> Unit,
    onPreviousEpisode: () -> Unit,
    onNextEpisode: () -> Unit,
) {
    var controlsVisible by remember { mutableStateOf(true) }
    var interactionVersion by remember { mutableIntStateOf(0) }

    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var playWhenReady by remember { mutableStateOf(exoPlayer.playWhenReady) }
    var exoPlaybackState by remember { mutableIntStateOf(exoPlayer.playbackState) }
    val ready = playbackState.stage as? PlaybackStage.Ready
    val streamUrl = ready?.selectedStream?.url
    var playerFailure by remember(streamUrl) { mutableStateOf<PlayerFailure?>(null) }
    var automaticRetryCount by remember(streamUrl) { mutableIntStateOf(0) }

    val isBuffering = playWhenReady &&
            exoPlaybackState == Player.STATE_BUFFERING

    var isSeeking by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val backInteractionSource = remember { MutableInteractionSource() }
    val previousInteractionSource = remember { MutableInteractionSource() }
    val playbackInteractionSource = remember { MutableInteractionSource() }
    val nextInteractionSource = remember { MutableInteractionSource() }
    val orientationInteractionSource = remember { MutableInteractionSource() }
    val isBackPressed by backInteractionSource.collectIsPressedAsState()
    val isPreviousPressed by previousInteractionSource.collectIsPressedAsState()
    val isPlaybackPressed by playbackInteractionSource.collectIsPressedAsState()
    val isNextPressed by nextInteractionSource.collectIsPressedAsState()
    val isOrientationPressed by orientationInteractionSource.collectIsPressedAsState()
    val isControlPressed = isBackPressed ||
        isPreviousPressed ||
        isPlaybackPressed ||
        isNextPressed ||
        isOrientationPressed

    fun retryCurrentMedia() {
        val retryPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
        playerFailure = null
        exoPlayer.stop()
        exoPlayer.prepare()
        exoPlayer.seekTo(retryPosition)
        exoPlayer.play()
    }

    var armedSeekSide by remember { mutableStateOf<SeekSide?>(null) }
    var lastSeekTapAt by remember { mutableLongStateOf(0L) }
    var seekBasePosition by remember { mutableLongStateOf(0L) }
    var seekSteps by remember { mutableIntStateOf(0) }
    var seekFeedback by remember { mutableStateOf<SeekFeedback?>(null) }
    var displayedSeekFeedback by remember { mutableStateOf<SeekFeedback?>(null) }
    val seekFeedbackAlpha = remember { Animatable(0f) }
    var seekResetJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(seekFeedback) {
        val feedback = seekFeedback

        if (feedback != null) {
            displayedSeekFeedback = feedback
            seekFeedbackAlpha.snapTo(1f)
        } else if (displayedSeekFeedback != null) {
            seekFeedbackAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 450)
            )
            displayedSeekFeedback = null
        }
    }

    fun showControls() {
        controlsVisible = true
        interactionVersion++
    }

    fun hideControls() {
        controlsVisible = false
    }

    fun handlePlayerTap(x: Float, width: Float) {
        val now = SystemClock.uptimeMillis()

        val side = when {
            x < width * 0.4f -> SeekSide.Left
            x > width * 0.6f -> SeekSide.Right
            else -> null
        }

        if (side == null) {
            seekResetJob?.cancel()
            armedSeekSide = null
            seekSteps = 0
            seekFeedback = null
            controlsVisible = !controlsVisible
            interactionVersion++
            return
        }

        val continuesSequence =
            armedSeekSide == side && now - lastSeekTapAt <= 350L

        if (!continuesSequence) {
            armedSeekSide = side
            seekBasePosition = exoPlayer.currentPosition
            seekSteps = 0
            seekFeedback = null
            controlsVisible = !controlsVisible
            interactionVersion++
        } else {
            seekSteps++

            val offsetMs = seekSteps * 10_000L
            val targetPosition = when (side) {
                SeekSide.Left -> seekBasePosition - offsetMs
                SeekSide.Right -> seekBasePosition + offsetMs
            }

            val maxPosition = exoPlayer.duration
                .takeIf { it > 0 }
                ?: Long.MAX_VALUE

            exoPlayer.seekTo(
                targetPosition.coerceIn(0L, maxPosition)
            )

            seekFeedback = SeekFeedback(
                side = side,
                seconds = seekSteps * 10
            )

            showControls()
        }

        lastSeekTapAt = now

        seekResetJob?.cancel()
        seekResetJob = scope.launch {
            delay(350.milliseconds)

            if (seekSteps > 0) {
                saveProgress()
            }

            armedSeekSide = null
            seekSteps = 0
            seekFeedback = null
        }
    }

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

    LaunchedEffect(
        controlsVisible,
        interactionVersion,
        isPlaying,
        isSeeking,
        isControlPressed,
    ) {
        if (controlsVisible && isPlaying && !isSeeking && !isControlPressed) {
            delay(3_000.milliseconds)
            controlsVisible = false
        }
    }

    LaunchedEffect(isBuffering, streamUrl) {
        if (!isBuffering || streamUrl == null) return@LaunchedEffect

        delay(BUFFERING_TIMEOUT_MS.milliseconds)
        Log.w(
            PLAYER_LOG_TAG,
            "Buffering timeout at ${exoPlayer.currentPosition} ms"
        )

        if (automaticRetryCount == 0) {
            automaticRetryCount++
            retryCurrentMedia()
        } else {
            exoPlayer.pause()
            playerFailure = PlayerFailure(
                message = "Видео слишком долго загружается",
                requiresFreshStream = false,
            )
            showControls()
        }
    }

    DisposableEffect(exoPlayer, streamUrl) {
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

            override fun onPlayerError(error: PlaybackException) {
                val httpCode = error.findHttpResponseCode()
                val needsFreshStream = httpCode == 401 || httpCode == 403

                Log.e(
                    PLAYER_LOG_TAG,
                    "Playback failed: code=${error.errorCodeName}, " +
                        "http=$httpCode, state=${exoPlayer.playbackState}, " +
                        "position=${exoPlayer.currentPosition}, " +
                        "host=${streamUrl?.toSafeHost()}",
                    error,
                )

                if (!needsFreshStream && automaticRetryCount == 0) {
                    automaticRetryCount++
                    scope.launch {
                        delay(1_000.milliseconds)
                        retryCurrentMedia()
                    }
                } else {
                    playerFailure = PlayerFailure(
                        message = if (needsFreshStream) {
                            "Ссылка на видео устарела"
                        } else {
                            "Не удалось продолжить воспроизведение"
                        },
                        requiresFreshStream = needsFreshStream,
                    )
                    showControls()
                }
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

        ready?.let{
            Media3Player(
                player = exoPlayer,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                handlePlayerTap(
                                    x = offset.x,
                                    width = size.width.toFloat()
                                )
                            }
                        )
                    },
                topControls = { _, _ ->
                    if (scrimAlpha > 0) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = scrimAlpha))
                        )
                    }

                    displayedSeekFeedback?.let {
                        val isLeft = it.side == SeekSide.Left

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(seekFeedbackAlpha.value),
                            contentAlignment = if (isLeft) {
                                Alignment.CenterStart
                            } else {
                                Alignment.CenterEnd
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.45f)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = if (isLeft) {
                                                listOf(
                                                    YumeTheme.colors.accent.copy(alpha = 0.32f),
                                                    Color.Transparent
                                                )
                                            } else {
                                                listOf(
                                                    Color.Transparent,
                                                    YumeTheme.colors.accent.copy(alpha = 0.32f)
                                                )
                                            }
                                        )
                                    )
                            )

                            Text(
                                text = if (isLeft) {
                                    "−${it.seconds} сек"
                                } else {
                                    "+${it.seconds} сек"
                                },
                                modifier = Modifier.padding(horizontal = 32.dp),
                                style = YumeType.h3,
                                color = Color.White
                            )
                        }
                    }

                    if (controlsAlpha > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth().alpha(controlsAlpha),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            IconButton(
                                interactionSource = backInteractionSource,
                                onClick = {
                                    saveProgress()
                                    if (isLandscape) {
                                        showControls()
                                        compress()
                                    } else {
                                        showControls()
                                        onBackClick()
                                    }
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
                centerControls = { _, _ ->
                    if (controlsAlpha > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth().alpha(controlsAlpha),
                            horizontalArrangement = Arrangement.spacedBy(
                                18.dp,
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLandscape) {
                                IconButton(
                                    interactionSource = previousInteractionSource,
                                    onClick = {
                                        showControls()
                                        onPreviousEpisode()
                                    }
                                ) {
                                    Icon(
                                        painterResource(R.drawable.step_backward),
                                        contentDescription = "seek back 10",
                                        modifier = Modifier.size(sideIconSize)
                                    )
                                }
                            }

                            when {
                                playerFailure != null -> {
                                    val failure = requireNotNull(playerFailure)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.padding(horizontal = 24.dp),
                                    ) {
                                        Text(
                                            text = failure.message,
                                            color = Color.White,
                                            style = YumeType.bodyMedium,
                                            textAlign = TextAlign.Center,
                                        )
                                        Button(
                                            onClick = {
                                                if (failure.requiresFreshStream) {
                                                    playerFailure = null
                                                    saveProgress()
                                                    refreshStream()
                                                } else {
                                                    retryCurrentMedia()
                                                }
                                            }
                                        ) {
                                            Text("Повторить")
                                        }
                                    }
                                }

                                isBuffering -> {
                                    Box(
                                        modifier = Modifier.size(controlTouchSize),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(mainIconSize),
                                            color = Color.White,
                                            strokeWidth = 3.dp
                                        )
                                    }
                                }

                                isPlaying -> {
                                    IconButton(
                                        interactionSource = playbackInteractionSource,
                                        onClick = {
                                            showControls()
                                            exoPlayer.pause()
                                        },
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
                                        interactionSource = playbackInteractionSource,
                                        onClick = {
                                            showControls()
                                            exoPlayer.play()
                                        },
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

                            if (isLandscape) {
                                IconButton(
                                    interactionSource = nextInteractionSource,
                                    onClick = {
                                        showControls()
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
                    }
                },
                bottomControls = { player, _ ->

                    val progressState = rememberProgressStateWithTickInterval(player, 500)
                    val current = progressState.currentPositionMs
                    val duration = progressState.durationMs

                    var progress = (current.toFloat() / duration.toFloat()).coerceIn(0f, 1f)

                    if (duration <= 0)
                        progress = 0f

                    if (controlsAlpha > 0) {

                        Column(
                            modifier = Modifier
                                .alpha(controlsAlpha),
                        ) {
                            if (isLandscape) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 18.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${formatTime(current)} / ${formatTime(duration)}",
                                        style = YumeType.sm,
                                    )

                                    Icon(
                                        painterResource(R.drawable.compress_24),
                                        contentDescription = "compress",
                                        modifier = Modifier
                                            .clickable(
                                                interactionSource = orientationInteractionSource,
                                                indication = null,
                                                onClick = compress
                                            )
                                            .padding(
                                                top = 15.dp,
                                                start = 10.dp,
                                                end = 18.dp,
                                            )
                                            .size(sideIconSize)
                                    )
                                }

                                PlaybackProgressBar(
                                    progress = progress,
                                    onSeek = { newProgress ->
                                        exoPlayer.seekTo(
                                            (duration * newProgress).toLong()
                                        )
                                        saveProgress()
                                        showControls()
                                    },
                                    onSeekingChanged = { seeking ->
                                        isSeeking = seeking

                                        if (seeking) {
                                            showControls()
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    isLandscape = true
                                )
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp)
                                        .alpha(controlsAlpha),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${formatTime(current)} / ${formatTime(duration)}",
                                        style = YumeType.sm,
                                    )

                                    Icon(
                                        painterResource(R.drawable.expand_24),
                                        contentDescription = "expand",
                                        modifier = Modifier
                                            .clickable(
                                                interactionSource = orientationInteractionSource,
                                                indication = null,
                                                onClick = expand
                                            )
                                            .padding(
                                                top = 6.dp,
                                                start = 10.dp,
                                                end = 10.dp,
                                                bottom = 6.dp
                                            )
                                            .size(sideIconSize)
                                    )
                                }

                                PlaybackProgressBar(
                                    progress = progress,
                                    onSeek = { newProgress ->
                                        exoPlayer.seekTo(
                                            (duration * newProgress).toLong()
                                        )
                                        saveProgress()
                                        showControls()
                                    },
                                    onSeekingChanged = { seeking ->
                                        isSeeking = seeking

                                        if (seeking) {
                                            showControls()
                                        }
                                    },
                                    modifier = Modifier,
                                    isLandscape = false
                                )
                            }
                        }
                    }
                }
            )
        }

        if (ready == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                when (val stage = playbackState.stage) {
                    is PlaybackStage.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(horizontal = 24.dp),
                        ) {
                            Text(
                                text = stage.message,
                                color = Color.White,
                                style = YumeType.bodyMedium,
                                textAlign = TextAlign.Center,
                            )
                            Button(onClick = refreshStream) {
                                Text("Повторить")
                            }
                        }
                    }

                    else -> CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                    )
                }
            }
        }
    }
}

private fun PlaybackException.findHttpResponseCode(): Int? {
    var current: Throwable? = this

    while (current != null) {
        if (current is HttpDataSource.InvalidResponseCodeException) {
            return current.responseCode
        }
        current = current.cause
    }

    return null
}

private fun String.toSafeHost(): String? =
    runCatching { this.toUri().host }.getOrNull()

@Composable
fun PlaybackProgressBar(
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekingChanged: (Boolean) -> Unit,
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
                    onSeekingChanged(true)

                    try {
                        updateProgress(down.position.x)
                        down.consume()

                        var pressed = true

                        while (pressed) {
                            val event = awaitPointerEvent()
                            val change = event.changes
                                .firstOrNull { it.id == down.id }
                                ?: break

                            updateProgress(change.position.x)
                            pressed = change.pressed
                            change.consume()
                        }

                        onSeek(displayedProgress)
                    } finally {
                        isDragging = false
                        onSeekingChanged(false)
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
