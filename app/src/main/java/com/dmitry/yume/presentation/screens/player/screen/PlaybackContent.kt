package com.dmitry.yume.presentation.screens.player.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.presentation.components.BaseButton
import com.dmitry.yume.presentation.screens.player.PlaybackUiState
import com.dmitry.yume.presentation.screens.player.screen.componens.VoiceoverButton
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun PlaybackContent(
    player: ExoPlayer,
    playbackState: PlaybackUiState,
    playbackCatalog: PlaybackCatalog,
    animeDetails: AnimeDetailed?,
    isLandscape: Boolean,
    hasPreviousEpisode: Boolean,
    hasNextEpisode: Boolean,
    onPreviousEpisode: () -> Unit,
    onNextEpisode: () -> Unit,
    onEpisodePicker: () -> Unit,
    onExpand: () -> Unit,
    onCompress: () -> Unit,
    onSaveProgress: () -> Unit,
    onRefreshStream: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onVoiceoverPicker: () -> Unit,
    onRelatedTitleClick: (Int) -> Unit,
) {
    val isMovie = animeDetails?.kind.equals("movie", ignoreCase = true)
    val availableRelations = animeDetails?.relations
        .orEmpty()
        .filter { it.isAvailable && it.id != animeDetails?.id }
        .distinctBy { it.id }

    var videoAspectRatio by remember(player) {
        mutableFloatStateOf(player.videoSize.toAspectRatioOrDefault())
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                videoAspectRatio = videoSize.toAspectRatioOrDefault()
            }
        }
        player.addListener(listener)
        videoAspectRatio = player.videoSize.toAspectRatioOrDefault()

        onDispose { player.removeListener(listener) }
    }

    if (isLandscape) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            val controlsAspectRatio = videoAspectRatio.coerceIn(4f / 3f, 16f / 9f)
            val availableAspectRatio = maxWidth.value / maxHeight.value
            val playerModifier = if (availableAspectRatio >= controlsAspectRatio) {
                Modifier
                    .height(maxHeight)
                    .aspectRatio(controlsAspectRatio)
            } else {
                Modifier
                    .width(maxWidth)
                    .aspectRatio(controlsAspectRatio)
            }

            VideoPlayer(
                playbackState = playbackState,
                exoPlayer = player,
                modifier = playerModifier,
                isLandscape = true,
                expand = onExpand,
                compress = onCompress,
                saveProgress = onSaveProgress,
                refreshStream = onRefreshStream,
                onBackClick = onBackClick,
                onPreviousEpisode = onPreviousEpisode,
                onNextEpisode = onNextEpisode,
            )
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VideoPlayer(
                playbackState = playbackState,
                exoPlayer = player,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(videoAspectRatio),
                isLandscape = false,
                expand = onExpand,
                compress = onCompress,
                saveProgress = onSaveProgress,
                refreshStream = onRefreshStream,
                onBackClick = onBackClick,
                onPreviousEpisode = onPreviousEpisode,
                onNextEpisode = onNextEpisode,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    playbackCatalog.title?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .fillMaxWidth(),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    if (!isMovie) {
                        playbackCatalog.episodes.find { it.number == playbackState.selectedEpisodeNumber }?.title?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = YumeType.xs,
                                textAlign = TextAlign.Start,
                                color = YumeTheme.colors.textMuted,
                            )
                        }
                    }
                }

                if (!isMovie) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BaseButton(
                            onClick = onPreviousEpisode,
                            modifier = Modifier.weight(0.2f),
                            icon = R.drawable.angle_left_24,
                            disabled = !hasPreviousEpisode
                        )

                        BaseButton(
                            onClick = onEpisodePicker,
                            modifier = Modifier.weight(0.6f),
                            text = "Серия ${playbackState.selectedEpisodeNumber}"
                        )

                        BaseButton(
                            onClick = onNextEpisode,
                            modifier = Modifier.weight(0.2f),
                            icon = R.drawable.angle_right_24,
                            disabled = !hasNextEpisode
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    VoiceoverButton(
                        onClick = onVoiceoverPicker,
                        playbackState = playbackState
                    )
                }

                if (availableRelations.isNotEmpty()) {
                    Text(
                        text = "Следующие релизы",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    availableRelations.forEach { anime ->
                        BaseButton(
                            onClick = { onRelatedTitleClick(anime.id) },
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = anime.title ?: anime.titleEn ?: "Без названия",
                        )
                    }
                }
            }
            }
        }
}

private fun VideoSize.toAspectRatioOrDefault(): Float {
    if (width <= 0 || height <= 0 || !pixelWidthHeightRatio.isFinite()) return 16f / 9f

    return (width.toFloat() / height * pixelWidthHeightRatio)
        .takeIf { it.isFinite() && it > 0f }
        ?: (16f / 9f)
}
