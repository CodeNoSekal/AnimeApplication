package com.dmitry.yume.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
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
    Scaffold(topBar = { RelationTopBar(onBackClick, 1f) }) { padding ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelationTopBar(
    onBackClick: () -> Unit,
    topAppBarAlpha: Float,
){
    TopAppBar(
        title = { Text("Связанное") },
        modifier = Modifier.height(85.dp),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.angle_small_left),
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = topAppBarAlpha)
        )
    )
}
