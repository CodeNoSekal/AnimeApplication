package com.dmitry.yume.presentation.screens.player.screen.componens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.domain.models.PlaybackEpisode
import com.dmitry.yume.presentation.screens.player.PlaybackUiState
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun EpisodePickerContent(
    playbackCatalog: PlaybackCatalog,
    playbackState: PlaybackUiState,
    setEpisode: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {

        Text(
            text = "Серии",
            style = YumeType.h2,
            color = colors.textPrimary
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceCard),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(playbackCatalog.episodes) { item ->
                EpisodeItemCard(
                    item,
                    playbackState,
                    setEpisode,
                )
            }
        }
    }
}

@Composable
fun EpisodeItemCard(
    item: PlaybackEpisode,
    playbackState: PlaybackUiState,
    setEpisode: (Int) -> Unit
) {

    val cardColor =
        if (item.number == playbackState.selectedEpisodeNumber)
            colors.accent.copy(alpha = 0.3f)
        else
            colors.surfaceCard

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                if (item.number != playbackState.selectedEpisodeNumber)
                    setEpisode(item.number) }
            .background( color = cardColor)
            .padding(vertical = 5.dp, horizontal = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Серия ${item.number}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )

        item.title?.let {
            Text(
                text = it,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(200.dp),
                style = YumeType.xs,
                color = colors.textMuted
            )
        }
    }
}