package com.dmitry.yume.presentation.navigation.screens

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.screens.catalog.components.filters.FiltersScreen
import com.dmitry.yume.presentation.screens.catalog.components.filters.GenresScreen

fun NavGraphBuilder.genresComposable(navController: NavController){
    composable(
        route = Destinations.GENRES
    ) {
        GenresScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}