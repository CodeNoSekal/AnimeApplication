package com.dmitry.yume.presentation.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.dmitry.yume.domain.models.Anime

@Composable
fun AnimeList(
    state: LazyListState,
    animeItems: LazyPagingItems<Anime>,
    onItemClicked: (Int) -> Unit,
    contentPadding: PaddingValues
) {

    val refresh = animeItems.loadState.refresh
    val padding = contentPadding + PaddingValues(horizontal = 8.dp, vertical = 8.dp)

    if (animeItems.itemCount == 0) {
        when (refresh) {
            is LoadState.Loading -> {
                // Never measure skeleton rows with the real list's saved scroll position.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = padding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(count = 8) { ListItemPlaceholder() }
                }
            }
            is LoadState.Error -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                ListMessage("Не удалось загрузить аниме", "Повторить", animeItems::retry)
            }
            is LoadState.NotLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                ListMessage("Ничего не найдено", "Обновить", animeItems::refresh)
            }
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = animeItems.itemCount,
                key = animeItems.itemKey { it.id },
                contentType = { "anime" }
            ) { index ->

                val anime = animeItems[index]

                if (anime != null) ListItemCard(anime, onItemClicked)
                else ListItemPlaceholder()
            }
            when (animeItems.loadState.append) {
                is LoadState.Loading -> item(key = "append_loading") {
                    Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is LoadState.Error -> item(key = "append_error") {
                    ListMessage("Не удалось загрузить следующую страницу", "Повторить", animeItems::retry)
                }
                is LoadState.NotLoading -> Unit
            }
        }

        // Keep existing cards and don't insert rows ahead of the saved anchor on errors.
        val message = when {
            refresh is LoadState.Error -> "Не удалось обновить список"
            animeItems.loadState.prepend is LoadState.Error -> "Не удалось загрузить предыдущую страницу"
            else -> null
        }
        if (message != null) {
            Surface(Modifier.align(Alignment.TopCenter).padding(padding), tonalElevation = 4.dp) {
                ListMessage(message, "Повторить", animeItems::retry)
            }
        }
    }
}

@Composable
private fun ListMessage(message: String, action: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(message, textAlign = TextAlign.Center)
        TextButton(onClick = onAction) { Text(action) }
    }
}
