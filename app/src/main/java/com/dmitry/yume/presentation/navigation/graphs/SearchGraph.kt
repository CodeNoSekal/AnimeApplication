package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.screens.detailsComposable
import com.dmitry.yume.presentation.screens.SearchScreen

fun NavGraphBuilder.searchGraph(navController: NavController) {
    navigation(route = Destinations.SEARCH_GRAPH, startDestination = Destinations.SEARCH) {
        composable(Destinations.SEARCH) {
            SearchScreen(
                onItemClicked = { id ->
                    navController.navigate(Details.build(Destinations.SEARCH, id))
                }
            )
        }
        detailsComposable(Destinations.SEARCH, navController)
    }
}