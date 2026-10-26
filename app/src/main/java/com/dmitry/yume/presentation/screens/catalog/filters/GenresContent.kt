package com.dmitry.yume.presentation.screens.catalog.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.Genre
import com.dmitry.yume.presentation.screens.catalog.CatalogViewModel
import com.dmitry.yume.presentation.screens.catalog.filters.components.GenresTopBar
import com.dmitry.yume.presentation.screens.catalog.filters.components.GenreCard
import com.dmitry.yume.presentation.screens.catalog.filters.components.GenresBottomBar

@Composable
fun GenresContent(
    genres: List<Genre>,
    catalogViewModel: CatalogViewModel,
    onBackClick: () -> Unit,
    navigateToCatalog: () -> Unit,
) {
    val draftFilters by catalogViewModel.draftFilters.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            GenresTopBar(
                onBackClick = onBackClick,
                onClearClick = catalogViewModel::dropGenres
            )
        },
        bottomBar = {
            GenresBottomBar(
                onApplyClick = {
                    catalogViewModel.applyFilters()
                    navigateToCatalog()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = (7.5).dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(
                items = genres
            ){ genre ->
                GenreCard(
                    genre,
                    included = draftFilters.genres,
                    excluded = draftFilters.excludeGenres,
                    onClick = { catalogViewModel.assignGenre(genre.id)}
                )
            }
        }
    }
}
