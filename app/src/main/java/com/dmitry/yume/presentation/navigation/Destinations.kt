package com.dmitry.yume.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dmitry.yume.R

object Destinations {
    const val ROOT = "root"
    const val AUTH = "auth"
    const val VERIFICATION = "verification"
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val PROFILE = "profile"
    const val COLLECTIONS = "collections"
    const val EXPLORATION = "exploration"

    const val FILTERS = "filters"
    const val GENRES = "genres"


    const val AUTH_GRAPH = "auth_graph"
    const val VERIFICATION_GRAPH = "verification_graph"
    const val HOME_GRAPH = "home_graph"
    const val CATALOG_GRAPH = "catalog_graph"
    const val PROFILE_GRAPH = "profile_graph"
    const val COLLECTIONS_GRAPH = "collections_graph"
    const val EXPLORATION_GRAPH = "exploration_graph"
}

object Details {
    const val DETAILS = "details"
    const val ANIME_ID = "animeId"

    fun routePattern(parent: String) = "$parent/$DETAILS/{$ANIME_ID}"

    fun build(parent: String, animeId: Int) = "$parent/$DETAILS/$animeId"
}

object PlaybackDestination {
    const val GRAPH = "player_graph"
    const val SCREEN = "player"
    const val EPISODE_PICKER = "episodes"
    const val ANIME_ID = "animeId"

    fun routePattern() = "$GRAPH/{$ANIME_ID}"

    fun build(animeId: Int) = "$GRAPH/$animeId"
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
        R.drawable.heart_24,
        R.string.nav_lists
    ),
    CATALOG(
        Destinations.CATALOG_GRAPH,
        Destinations.CATALOG,
        R.drawable.apps_24,
        R.string.nav_catalog
    ),
    HOME(
        Destinations.HOME_GRAPH,
        Destinations.HOME,
        R.drawable.home_24,
        R.string.nav_home
    ),
    SEARCH(
        Destinations.EXPLORATION_GRAPH,
        Destinations.EXPLORATION,
        R.drawable.search_24,
        R.string.nav_search
    ),
    PROFILE(
        Destinations.PROFILE_GRAPH,
        Destinations.PROFILE,
        R.drawable.user_24,
        R.string.nav_profile
    )
}
