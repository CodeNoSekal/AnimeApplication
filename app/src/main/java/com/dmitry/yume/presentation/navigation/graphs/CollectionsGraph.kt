package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.Search
import com.dmitry.yume.presentation.navigation.screens.detailsComposable
import com.dmitry.yume.presentation.navigation.screens.searchComposable
import com.dmitry.yume.presentation.screens.collections.CollectionsScreen

fun NavGraphBuilder.collectionsGraph(navController: NavController) {
    navigation(route = Destinations.COLLECTIONS_GRAPH, startDestination = Destinations.COLLECTIONS) {
        composable(Destinations.COLLECTIONS) {
            CollectionsScreen(
                onItemClicked = { id ->
                    navController.navigate(Details.build(Destinations.COLLECTIONS, id))
                },
                onSearchClicked = {
                    navController.navigate(Search.route(Destinations.COLLECTIONS))
                }
            )
        }

        detailsComposable(Destinations.COLLECTIONS, navController)
        searchComposable(Destinations.COLLECTIONS, navController)
    }
}