package com.dmitry.test.animeapplication.presentation.screens.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.presentation.components.SearchButton

val CollectionSearchHeight = 64.dp
val CollectionTabHeight = 48.dp

@Composable
fun CollectionsTopBar(
    searchOffset: Dp,
    tabs: List<CollectionTab>,
    selectedIndex: Int,
    onSearchClicked: () -> Unit,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val currentHeight =
        statusBarHeight + CollectionSearchHeight + CollectionTabHeight + searchOffset


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(currentHeight)
            .clipToBounds()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .offset(y = statusBarHeight + searchOffset)
                .fillMaxWidth()
                .height(CollectionSearchHeight)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            SearchButton(onSearchClicked)
        }

        CollectionsTabRow(
            tabs = tabs,
            selectedIndex = selectedIndex,
            onTabSelected = onTabSelected,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(CollectionTabHeight)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(statusBarHeight)
                .background(MaterialTheme.colorScheme.background)
        )

    }
}