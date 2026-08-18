package com.dmitry.yume.presentation.navigation.screens

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Search
import com.dmitry.yume.presentation.screens.catalog.FiltersScreen

fun NavGraphBuilder.filtersComposable(navController: NavController){
    composable(
        route = Destinations.FILTERS
    ) {
        FiltersScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}