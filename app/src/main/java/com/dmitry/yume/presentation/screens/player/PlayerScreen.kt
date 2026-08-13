package com.dmitry.yume.presentation.screens.player

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
import androidx.compose.ui.text.intl.Locale
import com.dmitry.yume.domain.models.PlayerData

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun PlayerScreen(
    playerData: PlayerData,
    playerState: PlayerUiState,
    saveProgress: (PlaybackContext, Long, Long) -> Unit,
    onPrevEpisodeClick: () -> Unit,
    onNextEpisodeClick: () -> Unit,
    onEpisodeClick: () -> Unit
) {
    val activity = LocalActivity.current ?: return
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val controller = rememberPlayerController(
        onSaveProgress = saveProgress
    )

    val playbackContext = remember(
        playerData.id,
        playerState.selectedEpisodeNumber,
        playerState.selectedSource,
        playerState.selectedVoiceoverId
    ) {
        PlaybackContext(
            animeId = playerData.id,
            episodeNumber = playerState.selectedEpisodeNumber,
            sourceProvider = playerState.selectedSource,
            voiceoverId = playerState.selectedVoiceoverId
        )
    }

    LaunchedEffect(playbackContext, playerState.currentUrl) {
        val url = playerState.currentUrl ?: return@LaunchedEffect

        controller.replaceMedia(
            url = url,
            context = playbackContext,
            startPositionMs = playerState.currentPositionMs
        )
    }

    PlayerLifecycleEffect(controller)
    PlayerKeepScreenOnEffect(controller)
    PlayerSystemUiEffect(
        activity = activity,
        isLandscape = isLandscape
    )
    PlayerReleaseEffect(
        controller = controller,
        activity = activity
    )

    val navigation = playerData.navigationFor(
        selectedEpisodeNumber = playerState.selectedEpisodeNumber
    )

    val contentModifier = if (isLandscape) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    }

    PlayerContent(
        player = controller.player,
        playerState = playerState,
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
        modifier = contentModifier
    )
}
