package com.dmitry.yume.presentation.navigation.graphs

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.Search
import com.dmitry.yume.presentation.navigation.screens.detailsComposable
import com.dmitry.yume.presentation.navigation.screens.filtersComposable
import com.dmitry.yume.presentation.navigation.screens.genresComposable
import com.dmitry.yume.presentation.navigation.screens.searchComposable
import com.dmitry.yume.presentation.screens.catalog.CatalogScreen
import com.dmitry.yume.presentation.screens.catalog.CatalogViewModel

fun NavGraphBuilder.catalogGraph(navController: NavController) {
    navigation(route = Destinations.CATALOG_GRAPH, startDestination = Destinations.CATALOG) {
        composable(Destinations.CATALOG) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Destinations.CATALOG_GRAPH)
            }

            val catalogViewModel: CatalogViewModel = hiltViewModel(parentEntry)

            CatalogScreen(
                catalogViewModel = catalogViewModel,
                onItemClicked = { id ->
                    navController.navigate(Details.build(Destinations.CATALOG, id))
                },
                onSearchClicked = {
                    navController.navigate(Search.route(Destinations.CATALOG))
                },
                onFiltersClicked = {
                    navController.navigate(Destinations.FILTERS)
                }
            )
        }
        detailsComposable(Destinations.CATALOG, navController)
        searchComposable(Destinations.CATALOG, navController)
        filtersComposable(navController)
        genresComposable(navController)
    }
}