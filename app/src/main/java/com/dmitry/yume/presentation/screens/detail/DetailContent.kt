package com.dmitry.yume.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.presentation.screens.detail.components.AnimeInformation
import com.dmitry.yume.presentation.screens.detail.components.Description
import com.dmitry.yume.presentation.screens.detail.components.MainTitle
import com.dmitry.yume.presentation.screens.detail.components.OriginalTitle
import com.dmitry.yume.presentation.screens.detail.components.Poster
import com.dmitry.yume.presentation.screens.detail.components.FavoriteButton
import com.dmitry.yume.presentation.screens.detail.components.Genres
import com.dmitry.yume.presentation.screens.detail.components.PlayButton
import com.dmitry.yume.presentation.screens.detail.components.Relations
import com.dmitry.yume.presentation.screens.detail.components.Rating
import com.dmitry.yume.presentation.screens.detail.components.StatusButton

@Composable
fun DetailContent(
    animeData: AnimeDetailed,
    statusState: StatusViewState,
    setFavorite: () -> Unit,
    onStatusClick: () -> Unit,
    onScoreClick: () -> Unit,
    onPlayClick: (Int) -> Unit,
    onRelationClick: (Int) -> Unit,
    onAllRelationsClick: () -> Unit,
    onRetryPersonal: () -> Unit,
    isSaving: Boolean = false
) {
    val personal = (statusState as? StatusViewState.Success)?.status
    val actionsEnabled = personal != null && !isSaving
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Poster(animeData.posterUrl)
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            MainTitle(animeData.title)
            if (!animeData.titleEn.isNullOrBlank() && animeData.titleEn != animeData.title) OriginalTitle(animeData.titleEn)
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FavoriteButton(setFavorite, statusState, enabled = actionsEnabled)
            Box(Modifier.weight(1f)) {
                StatusButton(onStatusClick, statusState, enabled = actionsEnabled)
            }
        }
        if (statusState is StatusViewState.Error) {
            TextButton(onClick = onRetryPersonal) { Text("Повторить загрузку личных данных") }
        }
        PlayButton(onPlayClick, animeData)
        Description(animeData.description)
        AnimeInformation(animeData)
        if (!animeData.genres.isNullOrEmpty()) Genres(animeData.genres)
        Spacer(Modifier.height(4.dp))
        Rating(animeData.rating, personal?.score, actionsEnabled, onScoreClick)
        if (animeData.relations.distinctBy { it.id }.size > 1) {
            Spacer(Modifier.height(4.dp))
            Relations(
                items = animeData.relations,
                modifier = Modifier.padding(horizontal = 14.dp),
                currentTitleId = animeData.id,
                onItemClicked = onRelationClick,
                onShowAllClick = onAllRelationsClick
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}
