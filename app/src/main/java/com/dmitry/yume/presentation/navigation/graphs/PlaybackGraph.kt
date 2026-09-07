package com.dmitry.yume.presentation.navigation.graphs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.dmitry.yume.presentation.navigation.PlaybackDestination
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.screens.player.PlaybackCatalogState
import com.dmitry.yume.presentation.screens.player.PlaybackViewModel
import com.dmitry.yume.presentation.screens.player.screen.PlaybackScreen

fun NavGraphBuilder.playbackGraph(navController: NavController) {
    navigation(route = PlaybackDestination.routePattern(), startDestination = PlaybackDestination.SCREEN,
        arguments = listOf(navArgument(PlaybackDestination.ANIME_ID) { type = NavType.IntType })) {

        composable(
            route = PlaybackDestination.SCREEN
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(PlaybackDestination.routePattern())
            }

            val viewModel: PlaybackViewModel = hiltViewModel(parentEntry)
            val catalogState by viewModel.catalogState.collectAsStateWithLifecycle()
            val playbackState by viewModel.playbackUiState.collectAsStateWithLifecycle()
            val animeDetails by viewModel.animeDetails.collectAsStateWithLifecycle()

            when (catalogState) {
                is PlaybackCatalogState.Loading -> {

                }
                is PlaybackCatalogState.Success -> {
                    PlaybackScreen(
                        playbackCatalog = (catalogState as PlaybackCatalogState.Success).playbackCatalog,
                        playbackState = playbackState,
                        animeDetails = animeDetails,
                        saveProgress = viewModel::saveProgress,
                        setEpisode = viewModel::selectEpisode,
                        setVoiceover = viewModel::selectVoiceover,
                        setProvider = viewModel::selectSource,
                        onPrevEpisodeClick = viewModel::prevEpisode,
                        onNextEpisodeClick = viewModel::nextEpisode,
                        onRefreshStream = viewModel::resolveSelectedPlayback,
                        onEpisodeClick = { navController.navigate(PlaybackDestination.EPISODE_PICKER)},
                        onBackClick = { navController.popBackStack() },
                        onRelatedTitleClick = { animeId ->
                            navController.navigate(Details.build(Destinations.HOME, animeId))
                        },
                    )
                }
                is PlaybackCatalogState.Error -> {
                    ErrorScreen(
                        message = (catalogState as PlaybackCatalogState.Error).message
                            ?: "Не удалось загрузить плеер"
                    )
                }
            }
        }
    }
}
