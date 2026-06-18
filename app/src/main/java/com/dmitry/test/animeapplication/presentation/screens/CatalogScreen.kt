package com.dmitry.test.animeapplication.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.dmitry.test.animeapplication.domain.Anime
import com.dmitry.test.animeapplication.presentation.CatalogViewModel
import com.dmitry.test.animeapplication.presentation.ui.theme.TextPrimary

@Composable
fun CatalogScreen(
    onItemClicked: (Int) -> Unit,
    catalogViewModel: CatalogViewModel = hiltViewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val animeItems = catalogViewModel.anime.collectAsLazyPagingItems()
    val isRefreshing = animeItems.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { animeItems.refresh() }
    ) {
        CatalogContent(animeItems, onItemClicked)
    }
}

@Composable
fun CatalogContent(
    animeItems: LazyPagingItems<Anime>,
    onItemClicked: (Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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


@Composable
fun ListItemCard(
    anime: Anime,
    onItemClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))      // чтобы ripple клика был скруглён
            .clickable { onItemClicked(anime.id) }
    ) {
        AsyncImage(
            model = anime.posterUrl,
            contentDescription = anime.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = anime.title,
            modifier = Modifier.padding(vertical = 6.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
        )
    }
}
