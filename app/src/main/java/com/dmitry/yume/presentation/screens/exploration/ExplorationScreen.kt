package com.dmitry.yume.presentation.screens.exploration

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.Season
import com.dmitry.yume.domain.models.Section
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun ExplorationScreen(
    onSearchClick: () -> Unit,
    viewModel: ExplorationViewModel = hiltViewModel(),
    onSeasonClick: (Season) -> Unit,
    onSectionClick: (Section) -> Unit,
) {
    val exploreState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { ExplorationTopBar(onSearchClick) }
    ) { innerPadding ->
        when (val state = exploreState) {
            is ExplorationViewState.Loading -> {

            }

            is ExplorationViewState.Success -> {
                ExplorationContent(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    exploreData = state.exploreData,
                    onSeasonClick = onSeasonClick,
                    onSectionClick = onSectionClick,
                )
            }

            is ExplorationViewState.Error -> {
                ErrorScreen(
                    title = "Ошибка",
                    onRetry = viewModel::load
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun ExplorationScreenPreview() {
    YumeTheme {
        PreviewImages {
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = { ExplorationTopBar({ }) }
            ) { innerPadding ->
                ExplorationContent(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    exploreData = PreviewData.explore,
                    onSeasonClick = {},
                    onSectionClick = {},
                )
            }
        }
    }
}