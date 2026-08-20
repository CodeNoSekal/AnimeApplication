package com.dmitry.yume.presentation.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.dmitry.yume.presentation.components.list.AnimeList
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onItemClicked: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    catalogViewModel: CatalogViewModel = hiltViewModel(),
    onFiltersClicked: () -> Unit
) {
    val animeItems = catalogViewModel.anime.collectAsLazyPagingItems()

    val density = LocalDensity.current

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topBarHeight = statusBarHeight + CatalogTopBarContentHeight
    val topBarHeightPx = with(density) { topBarHeight.toPx() }

    var barOffsetPx by rememberSaveable { mutableFloatStateOf(0f) }
    val barOffsetDp = with(density) {barOffsetPx.toDp()}

    val connection = remember(topBarHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val prev = barOffsetPx
                barOffsetPx = (barOffsetPx + available.y).coerceIn(-topBarHeightPx, 0f)
                return Offset(0f, barOffsetPx - prev)
            }
        }
    }

    val listState = rememberLazyListState()

    var showSortSheet by remember { mutableStateOf(false) }

    val options by catalogViewModel.optionsState.collectAsState()
    var draftOptions by remember { mutableStateOf(options) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize().nestedScroll(connection)
    ) {
        AnimeList(
            state = listState,
            animeItems = animeItems,
            onItemClicked = onItemClicked,
            contentPadding = PaddingValues(top = topBarHeight + barOffsetDp)
        )
        CatalogTopBar(
            onSearchClicked,
            onSortClicked = {
                draftOptions = options
                showSortSheet = true
            },
            onFiltersClicked = {
                onFiltersClicked()
            },
            modifier = Modifier
                .offset { IntOffset(0, barOffsetPx.roundToInt()) }
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(MaterialTheme.colorScheme.background)
        )

        if (showSortSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    catalogViewModel.setOptions(draftOptions)
                    showSortSheet = false },
                containerColor = colors.surfaceCard,
                sheetGesturesEnabled = false,
                sheetState = sheetState,
            ) {
                SortPickerContent(
                    optionsState = draftOptions,
                    onSortSelected = { newSort ->
                        draftOptions = newSort
                    }
                )
            }
        }

    }
}
