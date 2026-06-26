package com.dmitry.test.animeapplication.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.test.animeapplication.presentation.navigation.Destinations
import com.dmitry.test.animeapplication.presentation.screens.verification.VerificationScreen

fun NavGraphBuilder.verifyGraph(navController: NavController) {
    navigation(route = Destinations.VERIFICATION_GRAPH, startDestination = Destinations.VERIFICATION) {
        composable(Destinations.VERIFICATION) {
            VerificationScreen(
                onVerified = {
                    navController.navigate(Destinations.HOME_GRAPH) {
                        popUpTo(Destinations.VERIFICATION_GRAPH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}