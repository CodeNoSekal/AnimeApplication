package com.dmitry.test.animeapplication.presentation.screens.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.OrientationEventListener
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.domain.models.PlayerData
import com.dmitry.test.animeapplication.presentation.components.BaseButton

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun PlayerScreen(
    playerData: PlayerData,
    playerState: PlayerUiState,
    onSavePosition: (Long) -> Unit,
    onPrevEpisodeClick: () -> Unit,
    onNextEpisodeClick: () -> Unit,
    onEpisodeClick: () -> Unit
) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val activity = context as Activity
    val window = activity.window
    val lifecycleOwner = LocalLifecycleOwner.current

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(10_000)
            .setSeekForwardIncrementMs(10_000)
            .build()
    }

    LaunchedEffect(playerState.currentUrl) {
        playerState.currentUrl?.let{
            exoPlayer.setMediaItem(MediaItem.fromUri(playerState.currentUrl))
            exoPlayer.prepare()
            exoPlayer.seekTo(playerState.currentPositionMs)
            exoPlayer.playWhenReady = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver {_, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                onSavePosition(exoPlayer.currentPosition)
                exoPlayer.pause()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onSavePosition(exoPlayer.currentPosition)
            exoPlayer.release()
        }
    }

    DisposableEffect(isLandscape) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        if (isLandscape) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    val screenModifier =
        if (isLandscape) Modifier.fillMaxSize()
        else Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)

    Column(
        modifier = screenModifier
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val playerModifier =
            if (isLandscape) Modifier.fillMaxSize()
            else Modifier.fillMaxWidth().aspectRatio(16f / 9f)

        Player(playerState,
            exoPlayer,
            playerModifier,
            isLandscape,
            expand = {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            },
            compress = {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        )


        if (!isLandscape) {

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BaseButton(
                    onClick = onPrevEpisodeClick,
                    modifier = Modifier
                        .weight(0.2f),
                    icon = R.drawable.angle_left_24,
                    disabled = playerState.selectedEpisodeNumber == 1
                )

                BaseButton(
                    onClick = onEpisodeClick,
                    modifier = Modifier
                        .weight(0.6f),
                    text = "Серия ${playerState.selectedEpisodeNumber}"
                )

                BaseButton(
                    onClick = onNextEpisodeClick,
                    modifier = Modifier
                        .weight(0.2f),
                    icon = R.drawable.angle_right_24,
                    disabled = playerState.selectedEpisodeNumber == playerData.episodesAvailable
                )
            }
        }
    }
}
