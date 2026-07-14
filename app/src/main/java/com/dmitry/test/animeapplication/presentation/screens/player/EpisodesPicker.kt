package com.dmitry.test.animeapplication.presentation.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.domain.models.Episode
import com.dmitry.test.animeapplication.domain.models.PlayerData
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme

@Composable
fun EpisodesPicker(
    playerData: PlayerData,
    episodeSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    playerState: PlayerUiState,
) {
    Scaffold(
        topBar = { EpisodesTopBar(onBackClick) },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding)
        ) {
            items(playerData.episodes) { item ->

                EpisodeItemCard(
                    item,
                    playerState,
                    episodeSelected,
                )

            }
        }
    }
}

@Composable
fun EpisodeItemCard(
    item: Episode,
    playerState: PlayerUiState,
    episodeSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { episodeSelected(item.id) }
            .padding(vertical = 5.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .background( color =
                if (playerState.selectedEpisodeNumber == item.id)
                    MaterialTheme.colorScheme.background
                else
                    MaterialTheme.colorScheme.surface
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Серия ${item.id}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (playerState.selectedEpisodeNumber == item.id) YumeTheme.colors.accent else MaterialTheme.colorScheme.onBackground
        )

        item.title?.let {
            Text(
                text = it,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodesTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(painterResource(R.drawable.arrow_small_left_24), "back")
            }
        },
        title = { Text("Список эпизодов") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        )
    )
}