package com.dmitry.test.animeapplication.presentation.navigation.graphs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.dmitry.test.animeapplication.presentation.navigation.Player
import com.dmitry.test.animeapplication.presentation.screens.ErrorScreen
import com.dmitry.test.animeapplication.presentation.screens.player.EpisodesPicker
import com.dmitry.test.animeapplication.presentation.screens.player.PlayerScreen
import com.dmitry.test.animeapplication.presentation.screens.player.PlayerViewModel
import com.dmitry.test.animeapplication.presentation.screens.player.PlayerViewState

fun NavGraphBuilder.playerGraph(navController: NavController) {
    navigation(route = Player.routePattern(), startDestination = Player.PLAYER,
        arguments = listOf(navArgument(Player.ANIME_ID) { type = NavType.IntType })) {

        composable(
            route = Player.PLAYER
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Player.routePattern())
            }

            val viewModel: PlayerViewModel = hiltViewModel(parentEntry)
            val state by viewModel.state.collectAsStateWithLifecycle()
            val playerState by viewModel.playerState.collectAsStateWithLifecycle()

            when (state) {
                is PlayerViewState.Loading -> {

                }
                is PlayerViewState.Success -> {
                    PlayerScreen(
                        playerData = (state as PlayerViewState.Success).playerData,
                        playerState = playerState,
                        onSavePosition = viewModel::savePosition,
                        saveProgress = viewModel::saveProgress,
                        onPrevEpisodeClick = viewModel::prevEpisode,
                        onNextEpisodeClick = viewModel::nextEpisode,
                        onEpisodeClick = { navController.navigate(Player.EPISODES)}
                    )
                }
                is PlayerViewState.Error -> {
                    ErrorScreen()
                }
            }
        }

        dialog(
           route = Player.EPISODES,
           dialogProperties = DialogProperties(
               usePlatformDefaultWidth = false
           )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Player.routePattern())
            }

            val viewModel: PlayerViewModel = hiltViewModel(parentEntry)
            val state by viewModel.state.collectAsStateWithLifecycle()
            val playerState by viewModel.playerState.collectAsStateWithLifecycle()


            when (state) {
                is PlayerViewState.Loading -> {

                }
                is PlayerViewState.Success -> {
                    EpisodesPicker(
                        playerData = (state as PlayerViewState.Success).playerData,
                        playerState = playerState,
                        episodeSelected = {
                            viewModel.selectEpisode(it)
                            navController.popBackStack()
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                is PlayerViewState.Error -> {
                    ErrorScreen()
                }
            }
        }
    }
}