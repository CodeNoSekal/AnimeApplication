package com.dmitry.test.animeapplication.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.test.animeapplication.presentation.navigation.Destinations
import com.dmitry.test.animeapplication.presentation.screens.auth.AuthScreen

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(route = Destinations.AUTH_GRAPH, startDestination = Destinations.AUTH) {
        composable(Destinations.AUTH) {
            AuthScreen(
                onAuthorized = {
                    navController.navigate(Destinations.HOME_GRAPH) {
                        popUpTo(Destinations.AUTH_GRAPH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCreated = {
                    navController.navigate(Destinations.VERIFICATION_GRAPH) {
                        popUpTo(Destinations.VERIFICATION_GRAPH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}