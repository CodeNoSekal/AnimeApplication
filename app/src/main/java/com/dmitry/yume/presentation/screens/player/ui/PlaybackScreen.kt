package com.dmitry.yume.presentation.screens.player.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.presentation.screens.player.PlaybackContext
import com.dmitry.yume.presentation.screens.player.PlaybackUiState
import com.dmitry.yume.presentation.screens.player.navigationFor
import com.dmitry.yume.presentation.screens.player.rememberPlaybackController

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun PlaybackScreen(
    playbackCatalog: PlaybackCatalog,
    playbackState: PlaybackUiState,
    saveProgress: (PlaybackContext, Long, Long) -> Unit,
    onPrevEpisodeClick: () -> Unit,
    onNextEpisodeClick: () -> Unit,
    onEpisodeClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val activity = LocalActivity.current ?: return
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val controller = rememberPlaybackController(
        onSaveProgress = saveProgress
    )

    val playbackContext = remember(
        playbackCatalog.animeId,
        playbackState.selectedEpisodeNumber,
        playbackState.selectedSource,
        playbackState.selectedVoiceoverId
    ) {
        PlaybackContext(
            animeId = playbackCatalog.animeId,
            episodeNumber = playbackState.selectedEpisodeNumber,
            sourceProvider = playbackState.selectedSource,
            voiceoverId = playbackState.selectedVoiceoverId
        )
    }

    LaunchedEffect(playbackContext, playbackState.currentUrl) {
        val url = playbackState.currentUrl ?: return@LaunchedEffect

        controller.replaceMedia(
            url = url,
            context = playbackContext,
            startPositionMs = playbackState.currentPositionMs
        )
    }

    PlaybackLifecycleEffect(controller)
    PlaybackKeepScreenOnEffect(controller)
    PlaybackSystemUiEffect(
        activity = activity,
        isLandscape = isLandscape
    )
    PlaybackReleaseEffect(
        controller = controller,
        activity = activity
    )

    val navigation = playbackCatalog.navigationFor(
        selectedEpisodeNumber = playbackState.selectedEpisodeNumber
    )

    val contentModifier = if (isLandscape) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    }

    PlaybackContent(
        player = controller.player,
        playbackState = playbackState,
        playbackCatalog = playbackCatalog,
        isLandscape = isLandscape,
        hasPreviousEpisode = navigation.previousEpisodeId != null,
        hasNextEpisode = navigation.nextEpisodeId != null,
        onPreviousEpisode = onPrevEpisodeClick,
        onNextEpisode = onNextEpisodeClick,
        onEpisodePicker = onEpisodeClick,
        onExpand = {
            activity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        },
        onCompress = {
            activity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        },
        onSaveProgress = controller::saveCurrentProgress,
        modifier = contentModifier,
        onBackClick = onBackClick,
    )
}
