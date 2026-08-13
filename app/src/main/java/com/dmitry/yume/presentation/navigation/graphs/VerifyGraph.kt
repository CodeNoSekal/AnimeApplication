package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.screens.verification.VerificationScreen

fun NavGraphBuilder.verifyGraph(navController: NavController) {
    navigation(route = Destinations.VERIFICATION_GRAPH, startDestination = Destinations.VERIFICATION) {
        composable(Destinations.VERIFICATION) {
            VerificationScreen(
                onVerified = {
                    if (navController.previousBackStackEntry?.destination?.hierarchy
                        ?.any { destination ->
                            destination.route == Destinations.PROFILE_GRAPH
                        } == true
                    ) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Destinations.HOME_GRAPH) {
                            popUpTo(Destinations.AUTH_GRAPH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}