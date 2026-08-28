package com.dmitry.yume.presentation.navigation.screens

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.screens.catalog.CatalogViewModel
import com.dmitry.yume.presentation.screens.catalog.filters.GenresScreen

fun NavGraphBuilder.genresComposable(navController: NavController){
    composable(
        route = Destinations.GENRES
    ) { backStackEntry ->

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Destinations.CATALOG_GRAPH)
        }

        val catalogViewModel: CatalogViewModel = hiltViewModel(parentEntry)

        GenresScreen(
            catalogViewModel = catalogViewModel,
            onBackClick = {
                navController.popBackStack()
            },
            navigateToCatalog = {
                navController.popBackStack(
                    route = Destinations.CATALOG,
                    inclusive = false,
                )
            }
        )
    }
}