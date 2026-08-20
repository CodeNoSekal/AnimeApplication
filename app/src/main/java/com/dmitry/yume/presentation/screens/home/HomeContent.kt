package com.dmitry.yume.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Home
import com.dmitry.yume.domain.models.ProgressData
import com.dmitry.yume.presentation.screens.home.components.ContinueTab
import com.dmitry.yume.presentation.screens.home.components.Hero

@Composable
fun HomeContent(
    innerPadding: PaddingValues,
    progressData: ProgressViewState,
    homeData: Home,
    onPlayClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
){
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(
                top = 100.dp,
                ///TODO///
            )
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        if (progressData is ProgressViewState.Success) {
            ContinueTab(
                data = progressData.progress,
                onPlayClick = onPlayClick
            )
        }
    }
}