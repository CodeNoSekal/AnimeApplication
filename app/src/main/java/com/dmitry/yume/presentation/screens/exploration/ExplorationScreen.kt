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
import com.dmitry.yume.domain.models.SeasonModel
import com.dmitry.yume.domain.models.SectionModel
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun ExplorationScreen(
    onSearchClicked: () -> Unit,
    viewModel: ExplorationViewModel = hiltViewModel(),
    onSeasonClicked: (SeasonModel) -> Unit,
    onSectionClicked: (SectionModel) -> Unit,
) {
    val exploreState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { ExplorationTopBar(onSearchClicked) }
    ) { innerPadding ->
        when (val state = exploreState) {
            is ExplorationUiState.Loading -> {

            }

            is ExplorationUiState.Success -> {
                ExplorationContent(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    exploreData = state.exploreData,
                    onSeasonClicked = onSeasonClicked,
                    onSectionClicked = onSectionClicked,
                )
            }

            is ExplorationUiState.Error -> {
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
private fun SeasonCardPreview() {
    YumeTheme {
        PreviewImages {
            ExplorationContent(
                exploreData = PreviewData.explore,
                onSeasonClicked = {},
                onSectionClicked = {},
            )
        }
    }
}