package com.dmitry.yume.presentation.screens.exploration

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dmitry.yume.presentation.components.list.AnimeList

@Composable
fun ExplorationListScreen(
    onItemClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ExplorationListViewModel = hiltViewModel(),
) {
    val animeItems = viewModel.anime.collectAsLazyPagingItems()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ExplorationBackTopBar(viewModel.title, onBackClick = onBackClick)
        }
    ) { innerPadding ->
        AnimeList(
            state = rememberLazyListState(),
            animeItems = animeItems,
            onItemClick = onItemClick,
            contentPadding = innerPadding,
        )
    }
}