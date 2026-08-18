package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.screens.profile.ProfileScreen

fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation(route = Destinations.PROFILE_GRAPH, startDestination = Destinations.PROFILE) {
        composable(route = Destinations.PROFILE) {
            ProfileScreen(
                onVerificationClick = {
                    navController.navigate(Destinations.VERIFICATION_GRAPH) {
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(Destinations.AUTH_GRAPH) {
                        launchSingleTop = true
                    }
                },
                onRegistrationClick = {
                    navController.navigate(Destinations.AUTH_GRAPH) {
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}