package com.dmitry.test.animeapplication.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.test.animeapplication.presentation.navigation.Destinations
import com.dmitry.test.animeapplication.presentation.navigation.Details
import com.dmitry.test.animeapplication.presentation.navigation.detailsComposable
import com.dmitry.test.animeapplication.presentation.screens.HomeScreen
import com.dmitry.test.animeapplication.presentation.screens.auth.AuthScreen

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation(route = Destinations.HOME_GRAPH, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeScreen(
                onItemClicked = { id ->
                    navController.navigate(Details.build(Destinations.HOME, id))
                }
            )
        }
        detailsComposable(Destinations.HOME, navController)
    }
}