package com.dmitry.yume.presentation.screens.player.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.presentation.screens.player.PlaybackContext
import com.dmitry.yume.presentation.screens.player.PlaybackStage
import com.dmitry.yume.presentation.screens.player.PlaybackUiState
import com.dmitry.yume.presentation.screens.player.navigationFor
import com.dmitry.yume.presentation.screens.player.rememberPlaybackController
import com.dmitry.yume.presentation.screens.player.screen.componens.EpisodePickerContent
import com.dmitry.yume.presentation.screens.player.screen.componens.VoiceoverPickerContent
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun PlaybackScreen(
    playbackCatalog: PlaybackCatalog,
    playbackState: PlaybackUiState,
    saveProgress: (PlaybackContext, Long, Long) -> Unit,
    setEpisode: (Int) -> Unit,
    setVoiceover: (Int) -> Unit,
    setProvider: (Provider) -> Unit,
    onPrevEpisodeClick: () -> Unit,
    onNextEpisodeClick: () -> Unit,
    onRefreshStream: () -> Unit,
    onEpisodeClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val activity = LocalActivity.current ?: return
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val orientationController = rememberPlaybackOrientationController(activity)

    val ready = playbackState.stage as? PlaybackStage.Ready

    val controller = rememberPlaybackController(
        onSaveProgress = saveProgress
    )

    val playbackContext = remember(
        playbackCatalog.animeId,
        playbackState.selectedEpisodeNumber,
        playbackState.selectedSourceProvider,
        playbackState.selectedVoiceoverId
    ) {
        PlaybackContext(
            animeId = playbackCatalog.animeId,
            episodeNumber = playbackState.selectedEpisodeNumber,
            sourceProvider = playbackState.selectedSourceProvider,
            voiceoverId = playbackState.selectedVoiceoverId
        )
    }

    LaunchedEffect(playbackContext, ready?.selectedStream?.url) {
        val url = ready?.selectedStream?.url ?: return@LaunchedEffect

        controller.replaceMedia(
            url = url,
            context = playbackContext,
            startPositionMs = playbackState.currentPositionMs
        )
    }

    PlaybackLifecycleEffect(controller)
    PlaybackPeriodicProgressEffect(controller)
    PlaybackKeepScreenOnEffect(controller)
    PlaybackSystemUiEffect(
        activity = activity,
        isLandscape = isLandscape
    )
    PlaybackReleaseEffect(
        controller = controller
    )

    val navigation = playbackCatalog.navigationFor(
        selectedEpisodeNumber = playbackState.selectedEpisodeNumber
    )

    val contentModifier = if (isLandscape) {
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    } else {
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    }

    var showEpisodesSheet by remember { mutableStateOf(false) }
    var showVoiceoversSheet by remember { mutableStateOf(false) }

    val episodesSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val voiceoversSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scope = rememberCoroutineScope()

    PlaybackContent(
        player = controller.player,
        playbackState = playbackState,
        playbackCatalog = playbackCatalog,
        isLandscape = isLandscape,
        hasPreviousEpisode = navigation.previousEpisodeId != null,
        hasNextEpisode = navigation.nextEpisodeId != null,
        onPreviousEpisode = onPrevEpisodeClick,
        onNextEpisode = onNextEpisodeClick,
        onEpisodePicker = {
            showEpisodesSheet = true
        },
        onVoiceoverPicker = {
            showVoiceoversSheet = true
        },
        onExpand = orientationController::requestLandscape,
        onCompress = orientationController::requestPortrait,
        onSaveProgress = controller::saveCurrentProgress,
        onRefreshStream = {
            controller.saveCurrentProgress()
            onRefreshStream()
        },
        modifier = contentModifier,
        onBackClick = onBackClick,
    )

    val windowHeightPx = LocalWindowInfo.current.containerSize.height
    val maxSheetHeight = with(LocalDensity.current) {
        windowHeightPx.toDp() * 0.7f
    }

    if (showEpisodesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEpisodesSheet = false},
            containerColor = colors.surfaceCard,
            sheetGesturesEnabled = false,
            sheetState = episodesSheetState,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxSheetHeight)
            ) {
                EpisodePickerContent(
                    playbackCatalog,
                    setEpisode = { episodeId ->
                        controller.saveCurrentProgress()
                        controller.player.pause()
                        setEpisode(episodeId)
                    },
                    playbackState = playbackState,
                )
            }
        }
    }

    val currentProviders = playbackCatalog.episodes
        .firstOrNull {
            it.number == playbackState.selectedEpisodeNumber
        }
        ?.sources
        .orEmpty()

    if (showVoiceoversSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVoiceoversSheet = false },
            containerColor = colors.surfaceCard,
            sheetGesturesEnabled = false,
            sheetState = voiceoversSheetState,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxSheetHeight)
            ) {
                VoiceoverPickerContent(
                    sources = currentProviders,
                    selected = playbackState,
                    setVoiceover = { voiceoverId ->
                        controller.saveCurrentProgress()
                        controller.player.pause()
                        setVoiceover(voiceoverId)
                    },
                    setProvider = { provider ->
                        controller.saveCurrentProgress()
                        controller.player.pause()
                        setProvider(provider)
                    }
                )
            }
        }
    }
}
