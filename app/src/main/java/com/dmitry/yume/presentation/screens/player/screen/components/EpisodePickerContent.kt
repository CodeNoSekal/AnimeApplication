package com.dmitry.yume.presentation.screens.player.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.presentation.screens.player.PlaybackViewState
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun EpisodePickerContent(
    playbackCatalog: PlaybackCatalog,
    playbackState: PlaybackViewState,
    onEpisodeSelected: (Int) -> Unit
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
                    onEpisodeSelected,
                )
            }
        }
    }
}