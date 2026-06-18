package com.dmitry.test.animeapplication.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.dmitry.test.animeapplication.presentation.screens.CatalogScreen
import com.dmitry.test.animeapplication.presentation.screens.HomeScreen
import com.dmitry.test.animeapplication.presentation.screens.ProfileScreen
import com.dmitry.test.animeapplication.presentation.ui.theme.AnimeApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimeApplicationTheme {
                AnimeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeApp(){
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
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

            navigation(route = Destinations.PROFILE_GRAPH, startDestination = Destinations.PROFILE) {
                composable(route = Destinations.PROFILE) { ProfileScreen() }
            }
        }
    }
}


@Composable
fun BottomBar(
    navController: NavController
) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = { navController.navigateToTab(Destinations.CATALOG_GRAPH) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Catalog",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = { navController.navigateToTab(Destinations.HOME_GRAPH) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = { navController.navigateToTab(Destinations.PROFILE_GRAPH) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}