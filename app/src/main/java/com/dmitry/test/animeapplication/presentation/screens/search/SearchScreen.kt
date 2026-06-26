package com.dmitry.test.animeapplication.presentation.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.room.util.TableInfo
import com.dmitry.test.animeapplication.presentation.components.AnimeList

@Composable
fun SearchScreen(
    onItemClicked: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val state by viewModel.query.collectAsStateWithLifecycle()
    val result = viewModel.searchResult.collectAsLazyPagingItems()

    Column(

    ) {
        BasicTextField(
            state = state,
            modifier = TODO(),
            inputTransformation = TODO(),
            keyboardOptions = TODO(),
            onKeyboardAction = TODO(),
            interactionSource = TODO(),
            outputTransformation = TODO(),
            decorator = TODO(),
        )

        AnimeList(
            animeItems = result,
            onItemClicked = onItemClicked,
            contentPadding = PaddingValues(0.dp)
        )
    }

}