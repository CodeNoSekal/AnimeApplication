package com.dmitry.yume.presentation.navigation.screens

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.screens.catalog.CatalogViewModel
import com.dmitry.yume.presentation.screens.catalog.filters.FiltersScreen

fun NavGraphBuilder.filtersComposable(navController: NavController){
    composable(
        route = Destinations.FILTERS
    ) { backStackEntry ->

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Destinations.CATALOG_GRAPH)
        }

        val catalogViewModel: CatalogViewModel = hiltViewModel(parentEntry)

        FiltersScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onGenresClick = {
                navController.navigate(Destinations.GENRES)
            },
            catalogViewModel = catalogViewModel
        )
    }
}