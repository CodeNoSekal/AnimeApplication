package com.dmitry.yume.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
import com.dmitry.yume.presentation.navigation.navigateToTab
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.screens.SplashScreen
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)

        )
        setContent {
            YumeTheme  {
                AnimeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeApp(rootViewModel: RootViewModel = hiltViewModel()){
    val sessionState by rootViewModel.sessionState.collectAsStateWithLifecycle()

    when (sessionState) {
        SessionState.Loading -> SplashScreen()
        SessionState.Unavailable -> ErrorScreen()
        SessionState.Guest -> RootScreen(sessionState)
        is SessionState.Authenticated -> RootScreen(sessionState)
    }
}

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
                currentRoute != Destinations.GENRES
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

@Composable
fun BottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val colors = YumeTheme.colors

    NavigationBar(
        containerColor = colors.surfaceCard.copy(alpha = 0.92f),
        tonalElevation = 0.dp
    ) {
        TopLevelDestination.entries.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.graph } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected) {
                        navController.popBackStack(item.start, inclusive = false)
                    } else {
                        navController.navigateToTab(item.graph)
                    } },
                icon = { Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = stringResource(item.labelRes),
                    modifier = Modifier.size(20.dp),
                ) },
                label = { Text(stringResource(item.labelRes), style = YumeType.xs)},
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.accent,
                    selectedTextColor = colors.accent,
                    unselectedIconColor = colors.textMuted,
                    unselectedTextColor = colors.textMuted,
                    indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                )
            )
        }
    }
}

