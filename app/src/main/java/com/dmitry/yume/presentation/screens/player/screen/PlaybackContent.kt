package com.dmitry.yume.presentation.screens.player.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.dmitry.yume.R
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
    isLandscape: Boolean,
    hasPreviousEpisode: Boolean,
    hasNextEpisode: Boolean,
    onPreviousEpisode: () -> Unit,
    onNextEpisode: () -> Unit,
    onEpisodePicker: () -> Unit,
    onExpand: () -> Unit,
    onCompress: () -> Unit,
    onSaveProgress: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onVoiceoverPicker: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val videoModifier = if (isLandscape) {
            Modifier
                .fillMaxHeight()
                .aspectRatio(16f / 9f)
        } else {
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        }

        VideoPlayer(
            playbackState = playbackState,
            exoPlayer = player,
            modifier = videoModifier,
            isLandscape = isLandscape,
            expand = onExpand,
            compress = onCompress,
            saveProgress = onSaveProgress,
            onBackClick = onBackClick,
            onPreviousEpisode = { onPreviousEpisode() },
            onNextEpisode = { onNextEpisode() }
        )

        if (!isLandscape) {
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
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
            }
        }
    }
}
