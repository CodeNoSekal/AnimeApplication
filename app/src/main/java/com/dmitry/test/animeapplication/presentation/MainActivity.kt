package com.dmitry.test.animeapplication.presentation

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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.dmitry.test.animeapplication.presentation.auth.AuthScreen
import com.dmitry.test.animeapplication.presentation.verification.VerificationScreen
import com.dmitry.test.animeapplication.presentation.screens.CatalogScreen
import com.dmitry.test.animeapplication.presentation.screens.CollectionsScreen
import com.dmitry.test.animeapplication.presentation.screens.HomeScreen
import com.dmitry.test.animeapplication.presentation.screens.ProfileScreen
import com.dmitry.test.animeapplication.presentation.screens.SearchScreen
import com.dmitry.test.animeapplication.presentation.screens.SplashScreen
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType
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
    val gate by rootViewModel.authState.collectAsStateWithLifecycle()

    when (gate) {
        AuthGate.Loading -> SplashScreen()
        else -> {
            val navController = rememberNavController()
            val start = remember {
                if (gate == AuthGate.Authenticated)
                    Destinations.HOME_GRAPH
                else Destinations.AUTH_GRAPH
            }

            LaunchedEffect(gate) {
                if (gate == AuthGate.Unauthenticated) {
                    navController.navigate(Destinations.AUTH_GRAPH) {
                        popUpTo(0) {inclusive = true}
                        launchSingleTop = true
                    }
                }
            }

            val backStackEntry by navController.currentBackStackEntryAsState()
            val inAuthFlow = backStackEntry?.destination?.hierarchy?.any {
                it.route == Destinations.AUTH_GRAPH ||
                it.route == Destinations.VERIFICATION_GRAPH
            } == true

            Scaffold(
                bottomBar = {
                    if (gate == AuthGate.Authenticated && !inAuthFlow) BottomBar(navController)
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    route = Destinations.ROOT,
                    startDestination = start,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None },
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    authGraph(navController)

                    verifyGraph(navController)

                    navigation(route = Destinations.HOME_GRAPH, startDestination = Destinations.HOME) {
                        composable(Destinations.HOME) {
                            HomeScreen(
                                onItemClicked = { id ->
                                    navController.navigate(Details.build(Destinations.HOME, id))
                                }
                            )
                        }
                        detailsComposable(Destinations.HOME, navController)
                    }

                    navigation(route = Destinations.CATALOG_GRAPH, startDestination = Destinations.CATALOG) {
                        composable(Destinations.CATALOG) {
                            CatalogScreen(
                                onItemClicked = { id ->
                                    navController.navigate(Details.build(Destinations.CATALOG, id))
                                }
                            )
                        }
                        detailsComposable(Destinations.CATALOG, navController)
                    }

                    navigation(route = Destinations.COLLECTIONS_GRAPH, startDestination = Destinations.COLLECTIONS) {
                        composable(Destinations.COLLECTIONS) {
                            CollectionsScreen(
                                onItemClicked = { id ->
                                    navController.navigate(Details.build(Destinations.COLLECTIONS, id))
                                }
                            )
                        }
                        detailsComposable(Destinations.COLLECTIONS, navController)
                    }

                    navigation(route = Destinations.SEARCH_GRAPH, startDestination = Destinations.SEARCH) {
                        composable(Destinations.SEARCH) {
                            SearchScreen(
                                onItemClicked = { id ->
                                    navController.navigate(Details.build(Destinations.SEARCH, id))
                                }
                            )
                        }
                        detailsComposable(Destinations.SEARCH, navController)
                    }

                    navigation(route = Destinations.PROFILE_GRAPH, startDestination = Destinations.PROFILE) {
                        composable(route = Destinations.PROFILE) { ProfileScreen() }
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.authGraph(navController: NavController) {
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

private fun NavGraphBuilder.verifyGraph(navController: NavController) {
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
                onClick = { navController.navigateToTab(item.graph) },
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