package com.dmitry.yume.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.presentation.screens.detail.components.RelationItem

@Composable
internal fun RelationsScreen(
    items: List<Anime>,
    currentTitleId: Int,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val uniqueItems = items.distinctBy { it.id }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (uniqueItems.indexOfFirst { it.id == currentTitleId } - 1).coerceAtLeast(0)
    )
    Scaffold(topBar = { DetailsTopBar(onBackClick, 1f, "Связанное") }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            state = listState,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(uniqueItems, key = { it.id }) { item ->
                RelationItem(item, currentTitleId, onItemClick)
            }
        }
    }
}
