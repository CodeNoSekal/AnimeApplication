package com.dmitry.yume.presentation.screens.player.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.PlaybackEpisode
import com.dmitry.yume.presentation.screens.player.PlaybackViewState
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun EpisodeItemCard(
    item: PlaybackEpisode,
    playbackState: PlaybackViewState,
    onEpisodeSelected: (Int) -> Unit
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
                    onEpisodeSelected(item.number) }
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
