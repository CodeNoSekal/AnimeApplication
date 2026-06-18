package com.dmitry.test.animeapplication.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dmitry.test.animeapplication.presentation.screens.DetailsScreen

object Destinations {
    const val ROOT = "root"
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val PROFILE = "profile"

    const val HOME_GRAPH = "home_graph"
    const val CATALOG_GRAPH = "catalog_graph"
    const val PROFILE_GRAPH = "profile_graph"
}

object Details {
    const val DETAILS = "details"
    const val ANIME_ID = "animeId"

    fun routePattern(parent: String) = "$parent/$DETAILS/{$ANIME_ID}"

    fun build(parent: String, animeId: Int) = "$parent/$DETAILS/$animeId"
}

fun NavGraphBuilder.detailsComposable(parent: String, navController: NavController){
    composable(
        route = Details.routePattern(parent),
        arguments = listOf(navArgument(Details.ANIME_ID) { type = NavType.IntType })
    ) {
        DetailsScreen { navController.popBackStack() }
    }
}

fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}