package com.dmitry.test.animeapplication.presentation.screens.collections

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dmitry.test.animeapplication.presentation.components.list.AnimeList
import kotlinx.coroutines.launch


@Composable
fun CollectionsScreen(
    onItemClicked: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    collectionsViewModel: CollectionsViewModel = hiltViewModel()
) {
    val tabs = CollectionTab.entries
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current

    val searchHeightPx = with(density) { CollectionSearchHeight.toPx() }
    var searchOffsetPx by rememberSaveable { mutableFloatStateOf(0f) }

    val connection = remember(searchHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y >= 0f) return Offset.Zero

                val previous = searchOffsetPx
                searchOffsetPx =
                    (searchOffsetPx + available.y).coerceIn(-searchHeightPx, 0f)

                return Offset(0f, searchOffsetPx - previous)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y <= 0f) return Offset.Zero

                val previous = searchOffsetPx
                searchOffsetPx =
                    (searchOffsetPx + available.y).coerceIn(-searchHeightPx, 0f)

                return Offset(0f, searchOffsetPx - previous)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(connection)
    ) {
        CollectionsTopBar(
            searchOffset = with(density) { searchOffsetPx.toDp() },
            tabs = tabs,
            selectedIndex = pagerState.currentPage,
            onSearchClicked = {  },
            onTabSelected = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            key = { tabs[it] },
        ) { page ->
            val animeItems = collectionsViewModel.animeByTab
                .getValue(tabs[page])
                .collectAsLazyPagingItems()

            val listState = rememberLazyListState()

            AnimeList(
                state = listState,
                animeItems = animeItems,
                onItemClicked = onItemClicked,
                contentPadding = PaddingValues(0.dp)
            )
        }
    }
}