package com.dmitry.yume.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.HomeData
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.home.components.ContinueTab
import com.dmitry.yume.presentation.screens.home.components.hero.Hero
import com.dmitry.yume.presentation.screens.home.components.ranking.HomeRanking
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun HomeContent(
    homeData: HomeData,
    progressData: ProgressViewState,
    onPlayClick: (Int) -> Unit,
    progressActionState: ProgressActionState = ProgressActionState(),
    onRemoveProgress: (Int) -> Unit = {},
    onClearAllProgress: () -> Unit = {},
    contentPadding: PaddingValues,
    onItemClick: (Int) -> Unit,
){
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Hero(
            homeData.hero,
            contentPadding = contentPadding,
            onItemClick = onItemClick,
        )

        if (progressData is ProgressViewState.Success && progressData.progress.items.isNotEmpty()) {
            ContinueTab(
                data = progressData.progress,
                onPlayClick = onPlayClick,
                actionState = progressActionState,
                onRemove = onRemoveProgress,
                onClearAll = onClearAllProgress,
            )
        } else if (progressData is ProgressViewState.Loading) {
            ContinuePlaceholder()
        }

        HomeRanking(
            ranking = homeData.ranking,
            onItemClick = onItemClick,)

        Spacer(Modifier.height(0.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun HomeScreenPreview() {
    YumeTheme {
        PreviewImages {
            HomeScreenContent(
                onItemClick = {},
                onSearchClicked = { },
                onPlayClick = { },
                homeData = HomeViewState.Success(PreviewData.homeData),
                progressData = ProgressViewState.Success(PreviewData.progress),
                progressActionState = ProgressActionState(),
                onRemoveProgress = {},
                onClearAllProgress = {},
                onProgressErrorDismiss = {},
                onRetry = {}
            )
        }
    }
}
