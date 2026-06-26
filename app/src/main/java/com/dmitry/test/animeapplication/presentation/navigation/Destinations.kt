package com.dmitry.test.animeapplication.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.presentation.screens.detail.DetailScreen
import com.dmitry.test.animeapplication.presentation.screens.detail.DetailViewModel
import com.dmitry.test.animeapplication.presentation.screens.detail.DetailViewState

object Destinations {
    const val ROOT = "root"
    const val AUTH = "auth"
    const val VERIFICATION = "verification"
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val PROFILE = "profile"
    const val COLLECTIONS = "collections"
    const val SEARCH = "search"


    const val AUTH_GRAPH = "auth_graph"
    const val VERIFICATION_GRAPH = "verification_graph"
    const val HOME_GRAPH = "home_graph"
    const val CATALOG_GRAPH = "catalog_graph"
    const val PROFILE_GRAPH = "profile_graph"
    const val COLLECTIONS_GRAPH = "collections_graph"
    const val SEARCH_GRAPH = "search_graph"
}

object Details {
    const val DETAILS = "details"
    const val ANIME_ID = "animeId"

    fun routePattern(parent: String) = "$parent/$DETAILS/{$ANIME_ID}"

    fun build(parent: String, animeId: Int) = "$parent/$DETAILS/$animeId"
}

object Search {
    const val SEARCH = "search"

    fun route(parent: String) = "$parent/$SEARCH"
}

enum class TopLevelDestination(
    val graph: String,
    val start: String,
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    COLLECTIONS(
        Destinations.COLLECTIONS_GRAPH,
        Destinations.COLLECTIONS,
        R.drawable.heart_20,
        R.string.nav_lists
    ),
    CATALOG(
        Destinations.CATALOG_GRAPH,
        Destinations.CATALOG,
        R.drawable.apps_20,
        R.string.nav_catalog
    ),
    HOME(
        Destinations.HOME_GRAPH,
        Destinations.HOME,
        R.drawable.home_20,
        R.string.nav_home
    ),
    SEARCH(
        Destinations.SEARCH_GRAPH,
        Destinations.SEARCH,
        R.drawable.search_20,
        R.string.nav_search
    ),
    PROFILE(
        Destinations.PROFILE_GRAPH,
        Destinations.PROFILE,
        R.drawable.user_20,
        R.string.nav_profile
    )
}