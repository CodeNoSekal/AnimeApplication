package com.dmitry.yume.presentation.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.screens.profile.ProfileScreen
import com.dmitry.yume.presentation.screens.profile.EditProfileScreen
import com.dmitry.yume.presentation.screens.profile.ProfileSettingsScreen
import com.dmitry.yume.presentation.screens.profile.ChangeEmailScreen
import com.dmitry.yume.presentation.screens.profile.ChangePasswordScreen

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
                onEditClick = { navController.navigate(Destinations.EDIT_PROFILE) },
                onSettingsClick = { navController.navigate(Destinations.PROFILE_SETTINGS) },
            )
        }
        composable(Destinations.EDIT_PROFILE) {
            EditProfileScreen(onBackClick = navController::popBackStack)
        }
        composable(Destinations.PROFILE_SETTINGS) {
            ProfileSettingsScreen(
                onBackClick = navController::popBackStack,
                onEmailClick = { navController.navigate(Destinations.CHANGE_EMAIL) },
                onPasswordClick = { navController.navigate(Destinations.CHANGE_PASSWORD) },
                onLoggedOut = {
                    navController.popBackStack(Destinations.PROFILE, inclusive = false)
                },
            )
        }
        composable(Destinations.CHANGE_EMAIL) {
            ChangeEmailScreen(
                onBackClick = navController::popBackStack,
                onVerificationRequired = {
                    navController.navigate(Destinations.VERIFICATION_GRAPH) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Destinations.CHANGE_PASSWORD) {
            ChangePasswordScreen(onBackClick = navController::popBackStack)
        }
    }
}
