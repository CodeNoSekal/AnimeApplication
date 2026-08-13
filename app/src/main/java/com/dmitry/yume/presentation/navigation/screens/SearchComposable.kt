package com.dmitry.yume.presentation.navigation.screens

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.Search
import com.dmitry.yume.presentation.screens.search.SearchScreen

fun NavGraphBuilder.searchComposable(parent: String, navController: NavController){
    composable(
        route = Search.route(parent)
    ) {
        SearchScreen(
            onItemClicked = { id ->
                navController.navigate(Details.build(parent, id))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}
