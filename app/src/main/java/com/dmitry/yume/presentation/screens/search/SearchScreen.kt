package com.dmitry.yume.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.dmitry.yume.R
import com.dmitry.yume.presentation.components.list.AnimeList
import com.dmitry.yume.presentation.components.list.ListItemCard
import androidx.compose.material3.TextButton
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onItemClicked: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.query.collectAsStateWithLifecycle()
    val result = viewModel.searchResult.collectAsLazyPagingItems()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    var restoreKeyboard by rememberSaveable { mutableStateOf(true) }
    val keyboard = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.isImeVisible

    LaunchedEffect(Unit) {
        if (restoreKeyboard) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    painterResource(R.drawable.arrow_small_left_24),
                    contentDescription = "back"
                )
            }
            BasicTextField(
                value = state,
                onValueChange = viewModel::onQueryChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                textStyle = YumeType.body.copy(
                    color = YumeTheme.colors.textPrimary
                ),
                cursorBrush = SolidColor(YumeTheme.colors.accent),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f),
                            contentAlignment = Alignment.CenterStart,
                        ){
                            innerTextField()
                            if (state.text.isBlank()){
                                Text(
                                    text = "Поиск аниме...",
                                    style = YumeType.body.copy(
                                        color = YumeTheme.colors.textPrimary
                                    )
                                )
                            }
                        }

                        if (state.text.isNotBlank()){
                            IconButton(
                                onClick = viewModel::clearQuery
                            ) {
                                Icon(
                                    painterResource(R.drawable.cross_24),
                                    contentDescription = "clear",
                                    Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        val listState = rememberLazyListState()

        if (state.text.isBlank()) {
            if (history.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (history.items.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("История", style = YumeType.h2, modifier = Modifier.weight(1f))
                    TextButton(onClick = viewModel::clearHistory) { Text(
                        text = "Очистить",
                        style = YumeType.body,
                        color = colors.textMuted
                    ) }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(history.items, key = { it.id }) { anime ->
                        ListItemCard(
                            anime = anime,
                            onItemClicked = onItemClicked,
                            action = {
                                IconButton(onClick = { viewModel.removeFromHistory(anime.id) }) {
                                    Icon(
                                        painterResource(R.drawable.cross_24),
                                        contentDescription = "Удалить из истории",
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            },
                        )
                    }
                }
            }
        } else {
            AnimeList(
                state = listState,
                animeItems = result,
                onItemClicked = { id ->
                    restoreKeyboard = isKeyboardVisible
                    viewModel.addToHistory(id)
                    onItemClicked(id)
                },
                contentPadding = PaddingValues(0.dp)
            )
        }
    }

}
