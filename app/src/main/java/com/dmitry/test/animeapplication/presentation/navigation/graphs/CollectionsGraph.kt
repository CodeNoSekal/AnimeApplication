package com.dmitry.test.animeapplication.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.test.animeapplication.presentation.navigation.Destinations
import com.dmitry.test.animeapplication.presentation.navigation.Details
import com.dmitry.test.animeapplication.presentation.navigation.Search
import com.dmitry.test.animeapplication.presentation.navigation.screens.detailsComposable
import com.dmitry.test.animeapplication.presentation.navigation.screens.searchComposable
import com.dmitry.test.animeapplication.presentation.screens.collections.CollectionsScreen

fun NavGraphBuilder.collectionsGraph(navController: NavController) {
    navigation(route = Destinations.COLLECTIONS_GRAPH, startDestination = Destinations.COLLECTIONS) {
        composable(Destinations.COLLECTIONS) {
            CollectionsScreen(
                onItemClicked = { id ->
                    navController.navigate(Details.build(Destinations.CATALOG, id))
                },
                onSearchClicked = {
                    navController.navigate(Search.route(Destinations.CATALOG))
                }
            )
        }

        detailsComposable(Destinations.CATALOG, navController)
        searchComposable(Destinations.CATALOG, navController)
    }
}