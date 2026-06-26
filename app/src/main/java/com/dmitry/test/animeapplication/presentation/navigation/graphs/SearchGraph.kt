package com.dmitry.test.animeapplication.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.test.animeapplication.presentation.navigation.Destinations
import com.dmitry.test.animeapplication.presentation.navigation.Details
import com.dmitry.test.animeapplication.presentation.navigation.detailsComposable
import com.dmitry.test.animeapplication.presentation.screens.HomeScreen
import com.dmitry.test.animeapplication.presentation.screens.SearchScreen

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