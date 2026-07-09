package com.dmitry.test.animeapplication.presentation.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.dmitry.test.animeapplication.presentation.components.AnimeList
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
import kotlin.math.roundToInt

@Composable
fun CatalogScreen(
    onItemClicked: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    catalogViewModel: CatalogViewModel = hiltViewModel()
) {
    val animeItems = catalogViewModel.anime.collectAsLazyPagingItems()

    val density = LocalDensity.current
    var barHeightPx by remember { mutableStateOf(0) }
    var barOffset by remember { mutableStateOf(0f) }

    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val prev = barOffset
                barOffset = (barOffset + available.y).coerceIn((-barHeightPx).toFloat(), 0f)
                return Offset(0f, barOffset - prev)
            }
        }
    }


    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize().nestedScroll(connection)
    ) {
        AnimeList(
            animeItems = animeItems,
            onItemClicked = onItemClicked,
            contentPadding = PaddingValues(top = with(density) { (barHeightPx + barOffset).toDp() })
        )
        CatalogTopBar(
            onSearchClicked,
            modifier = Modifier
                .onSizeChanged{ barHeightPx = it.height }
                .offset { IntOffset(0, barOffset.roundToInt()) }
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(MaterialTheme.colorScheme.background)
        )
    }
}
