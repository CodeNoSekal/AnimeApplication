package com.dmitry.yume.presentation.screens.exploration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.dmitry.yume.presentation.components.list.AnimeList

@Composable
fun QuickSearchScreen(
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    viewModel: QuickSearchViewModel = hiltViewModel(),
) {
    val animeItems = viewModel.anime.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val isRefreshing =
        animeItems.loadState.refresh is LoadState.Loading && animeItems.itemCount > 0

    Scaffold(
        topBar = {
            ExplorationBackTopBar(viewModel.category.title, onBackClick)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = animeItems::refresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                AnimeList(
                    state = listState,
                    animeItems = animeItems,
                    onItemClicked = onItemClick,
                    contentPadding = innerPadding,
                )
            }
        }
    }
}
