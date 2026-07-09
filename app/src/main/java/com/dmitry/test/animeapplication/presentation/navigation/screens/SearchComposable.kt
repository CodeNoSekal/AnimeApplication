package com.dmitry.test.animeapplication.presentation.navigation.screens

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmitry.test.animeapplication.presentation.navigation.Destinations
import com.dmitry.test.animeapplication.presentation.navigation.Details
import com.dmitry.test.animeapplication.presentation.navigation.Search
import com.dmitry.test.animeapplication.presentation.screens.search.SearchScreen

fun NavGraphBuilder.searchComposable(parent: String, navController: NavController){
    composable(
        route = Search.route(parent)
    ) {
        SearchScreen(
            onItemClicked = { id ->
                navController.navigate(Details.build(Destinations.CATALOG, id))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}