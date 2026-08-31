package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.QuickSearch
import com.dmitry.yume.presentation.navigation.Search
import com.dmitry.yume.presentation.navigation.screens.detailsComposable
import com.dmitry.yume.presentation.screens.exploration.QuickSearchScreen
import com.dmitry.yume.presentation.screens.exploration.ExplorationScreen
import com.dmitry.yume.presentation.navigation.screens.searchComposable

fun NavGraphBuilder.explorationGraph(navController: NavController) {
    navigation(route = Destinations.EXPLORATION_GRAPH, startDestination = Destinations.EXPLORATION) {
        composable(Destinations.EXPLORATION) {
            ExplorationScreen(
                onSearchClicked = {
                    navController.navigate(Search.route(Destinations.EXPLORATION))
                },
                onQuickSearchClick = { category ->
                    navController.navigate(
                        QuickSearch.build(Destinations.EXPLORATION, category.route)
                    )
                },
            )
        }
        composable(
            route = QuickSearch.routePattern(Destinations.EXPLORATION),
            arguments = listOf(
                navArgument(QuickSearch.CATEGORY) { type = NavType.StringType }
            ),
        ) {
            QuickSearchScreen(
                onBackClick = navController::popBackStack,
                onItemClick = { animeId ->
                    navController.navigate(Details.build(Destinations.EXPLORATION, animeId))
                },
            )
        }
        detailsComposable(Destinations.EXPLORATION, navController)
        searchComposable(Destinations.EXPLORATION, navController)
    }
}
