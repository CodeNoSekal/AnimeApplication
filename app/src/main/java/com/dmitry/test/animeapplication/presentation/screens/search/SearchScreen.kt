package com.dmitry.test.animeapplication.presentation.screens.search

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.presentation.components.list.AnimeList
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onItemClicked: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.query.collectAsStateWithLifecycle()
    val result = viewModel.searchResult.collectAsLazyPagingItems()
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
                                    painterResource(R.drawable.cross_18),
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

        AnimeList(
            state = listState,
            animeItems = result,
            onItemClicked = { id ->
                restoreKeyboard = isKeyboardVisible
                onItemClicked(id)
            },
            contentPadding = PaddingValues(0.dp)
        )
    }

}