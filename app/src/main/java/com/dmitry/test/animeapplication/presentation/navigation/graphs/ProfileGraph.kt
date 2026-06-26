package com.dmitry.test.animeapplication.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.test.animeapplication.presentation.navigation.Destinations
import com.dmitry.test.animeapplication.presentation.navigation.Details
import com.dmitry.test.animeapplication.presentation.navigation.detailsComposable
import com.dmitry.test.animeapplication.presentation.screens.SearchScreen
import com.dmitry.test.animeapplication.presentation.screens.profile.ProfileScreen

fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation(route = Destinations.PROFILE_GRAPH, startDestination = Destinations.PROFILE) {
        composable(route = Destinations.PROFILE) { ProfileScreen() }
    }
}