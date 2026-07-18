package com.dmitry.test.animeapplication.presentation.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.dmitry.test.animeapplication.domain.models.Anime
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType

@Composable
fun AnimeList(
    animeItems: LazyPagingItems<Anime>,
    onItemClicked: (Int) -> Unit,
    contentPadding: PaddingValues
) {

    val isInitialLoading =
        animeItems.loadState.refresh is LoadState.Loading && animeItems.itemCount == 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {
        if (isInitialLoading) {
            items(count = 8) {
                ListItemPlaceholder()
            }
        }
        items(
            count = animeItems.itemCount
        ) { index ->

            val anime = animeItems[index]

            if (anime != null) {
                ListItemCard(
                    anime,
                    onItemClicked
                )
            }
        }
    }
}