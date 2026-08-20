package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.PlaybackDestination
import com.dmitry.yume.presentation.navigation.Search
import com.dmitry.yume.presentation.navigation.screens.detailsComposable
import com.dmitry.yume.presentation.navigation.screens.searchComposable
import com.dmitry.yume.presentation.screens.home.HomeScreen

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation(route = Destinations.HOME_GRAPH, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeScreen(
                onItemClick = { id ->
                    navController.navigate(Details.build(Destinations.HOME, id))
                },
                onSearchClicked = {
                    navController.navigate(Search.route(Destinations.HOME))
                },
                onPlayClick = { navController.navigate(PlaybackDestination.build(it)) },
            )
        }
        detailsComposable(Destinations.HOME, navController)
        searchComposable(Destinations.HOME, navController)
    }
}
