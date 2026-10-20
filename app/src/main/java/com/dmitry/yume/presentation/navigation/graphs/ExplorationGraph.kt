package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.ExplorationList
import com.dmitry.yume.presentation.navigation.Search
import com.dmitry.yume.presentation.navigation.screens.detailsComposable
import com.dmitry.yume.presentation.screens.exploration.ExplorationScreen
import com.dmitry.yume.presentation.navigation.screens.searchComposable
import com.dmitry.yume.presentation.screens.exploration.ExplorationListScreen

fun NavGraphBuilder.explorationGraph(navController: NavController) {
    navigation(route = Destinations.EXPLORATION_GRAPH, startDestination = Destinations.EXPLORATION) {
        composable(Destinations.EXPLORATION) {
            ExplorationScreen(
                onSearchClicked = {
                    navController.navigate(Search.route(Destinations.EXPLORATION))
                },
                onSeasonClicked = { season ->
                    navController.navigate(ExplorationList.build(season.endpoint, season.label))
                },
                onSectionClicked = { section ->
                    navController.navigate(ExplorationList.build(section.endpoint, section.title))
                }
            )
        }
        composable (
            route = ExplorationList.routePattern(),
            arguments = listOf(
                navArgument(ExplorationList.ENDPOINT) { type = NavType.StringType },
                navArgument(ExplorationList.TITLE) { type = NavType.StringType }
            )
        ) {
            ExplorationListScreen(
                onItemClicked = { id ->
                    navController.navigate(Details.build(Destinations.EXPLORATION, id))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        detailsComposable(Destinations.EXPLORATION, navController)
        searchComposable(Destinations.EXPLORATION, navController)
    }
}
