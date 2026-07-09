package com.dmitry.test.animeapplication.presentation.navigation.screens

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dmitry.test.animeapplication.presentation.navigation.Details
import com.dmitry.test.animeapplication.presentation.navigation.Player
import com.dmitry.test.animeapplication.presentation.screens.ErrorScreen
import com.dmitry.test.animeapplication.presentation.screens.detail.DetailScreen
import com.dmitry.test.animeapplication.presentation.screens.detail.DetailViewModel
import com.dmitry.test.animeapplication.presentation.screens.detail.DetailViewState

fun NavGraphBuilder.detailsComposable(parent: String, navController: NavController){
    composable(
        route = Details.routePattern(parent),
        arguments = listOf(navArgument(Details.ANIME_ID) { type = NavType.IntType })
    ) {
        val viewModel: DetailViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        when (state) {
            is DetailViewState.Loading -> {

            }
            is DetailViewState.Success -> {
                DetailScreen(
                    onBackClick = { navController.popBackStack() },
                    onPlayClick = { navController.navigate(Player.build(it)) },
                    animeData = (state as DetailViewState.Success).animeDetailed
                )
            }
            is DetailViewState.Error -> {
                ErrorScreen()
            }
        }
    }
}