package com.dmitry.yume.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.presentation.screens.detail.components.Description
import com.dmitry.yume.presentation.screens.detail.components.Divider
import com.dmitry.yume.presentation.screens.detail.components.MainTitle
import com.dmitry.yume.presentation.screens.detail.components.OriginalTitle
import com.dmitry.yume.presentation.screens.detail.components.Poster
import com.dmitry.yume.presentation.screens.detail.components.FavoriteButton
import com.dmitry.yume.presentation.screens.detail.components.Genres
import com.dmitry.yume.presentation.screens.detail.components.PlayButton
import com.dmitry.yume.presentation.screens.detail.components.Rating
import com.dmitry.yume.presentation.screens.detail.components.StatusButton

@Composable
fun DetailContent(
    animeData: AnimeDetailed,
    statusState:  StatusViewState,
    setFavorite: () -> Unit,
    onStatusClick: () -> Unit,
    onPlayClick: (Int) -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Poster(animeData.posterUrl)

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            MainTitle(animeData.title)

            OriginalTitle(animeData.titleEn)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FavoriteButton(
                onClick = setFavorite,
                statusState = statusState
            )

            StatusButton(
                onClick = onStatusClick,
                statusState = statusState
            )
        }

        PlayButton(
            onPlayClick = onPlayClick,
            animeData = animeData
        )

        Description(animeData.description)

        Genres(animeData.genres)

//        Rating(animeData.rating)

        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
    }
}