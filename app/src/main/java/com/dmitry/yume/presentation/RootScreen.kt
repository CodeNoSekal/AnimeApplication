package com.dmitry.yume.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.presentation.navigation.Destinations
import com.dmitry.yume.presentation.navigation.TopLevelDestination
import com.dmitry.yume.presentation.navigation.graphs.authGraph
import com.dmitry.yume.presentation.navigation.graphs.catalogGraph
import com.dmitry.yume.presentation.navigation.graphs.collectionsGraph
import com.dmitry.yume.presentation.navigation.graphs.homeGraph
import com.dmitry.yume.presentation.navigation.graphs.playbackGraph
import com.dmitry.yume.presentation.navigation.graphs.profileGraph
import com.dmitry.yume.presentation.navigation.graphs.explorationGraph
import com.dmitry.yume.presentation.navigation.graphs.verifyGraph

@Composable
fun RootScreen(
    sessionState: SessionState
) {
    val navController = rememberNavController()

    val isAuthenticated = sessionState is SessionState.Authenticated

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val inTopLevelDestination = backStackEntry?.destination?.hierarchy?.any { dest ->
        TopLevelDestination.entries.any { it.graph == dest.route }
    } == true

    Scaffold(
        bottomBar = {
            if(
                inTopLevelDestination &&
                currentRoute != Destinations.FILTERS &&
                currentRoute != Destinations.GENRES &&
                currentRoute != Destinations.EDIT_PROFILE &&
                currentRoute != Destinations.PROFILE_SETTINGS &&
                currentRoute != Destinations.CHANGE_EMAIL &&
                currentRoute != Destinations.CHANGE_PASSWORD
            ){
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            route = Destinations.ROOT,
            startDestination = Destinations.HOME_GRAPH,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            authGraph(navController)

            verifyGraph(navController)

            homeGraph(navController)

            catalogGraph(navController)

            collectionsGraph(navController)

            explorationGraph(navController)

            profileGraph(navController)

            playbackGraph(navController)
        }
    }
}
