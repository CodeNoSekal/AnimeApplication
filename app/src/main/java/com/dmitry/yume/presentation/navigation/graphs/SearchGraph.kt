package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.Search
import com.dmitry.yume.presentation.navigation.screens.detailsComposable
import com.dmitry.yume.presentation.screens.exploration.ExplorationScreen

fun NavGraphBuilder.explorationGraph(navController: NavController) {
    navigation(route = Destinations.EXPLORATION_GRAPH, startDestination = Destinations.EXPLORATION) {
        composable(Destinations.EXPLORATION) {
            ExplorationScreen(
                onSearchClicked = {
                    navController.navigate(Search.route(Destinations.HOME))
                },
            )
        }
        detailsComposable(Destinations.EXPLORATION, navController)
    }
}