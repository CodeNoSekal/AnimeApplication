package com.dmitry.yume.presentation.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.dmitry.yume.R
import com.dmitry.yume.presentation.components.BaseButton

@Composable
fun PlayerContent(
    player: ExoPlayer,
    playerState: PlayerUiState,
    isLandscape: Boolean,
    hasPreviousEpisode: Boolean,
    hasNextEpisode: Boolean,
    onPreviousEpisode: () -> Unit,
    onNextEpisode: () -> Unit,
    onEpisodePicker: () -> Unit,
    onExpand: () -> Unit,
    onCompress: () -> Unit,
    onSaveProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val playerModifier = if (isLandscape) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        }

        Player(
            playerState = playerState,
            exoPlayer = player,
            modifier = playerModifier,
            isLandscape = isLandscape,
            expand = onExpand,
            compress = onCompress,
            saveProgress = onSaveProgress
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
                    onClick = onPreviousEpisode,
                    modifier = Modifier.weight(0.2f),
                    icon = R.drawable.angle_left_24,
                    disabled = !hasPreviousEpisode
                )

                BaseButton(
                    onClick = onEpisodePicker,
                    modifier = Modifier.weight(0.6f),
                    text = "Серия ${playerState.selectedEpisodeNumber}"
                )

                BaseButton(
                    onClick = onNextEpisode,
                    modifier = Modifier.weight(0.2f),
                    icon = R.drawable.angle_right_24,
                    disabled = !hasNextEpisode
                )
            }
        }
    }
}
