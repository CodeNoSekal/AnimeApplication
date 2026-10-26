package com.dmitry.yume.presentation.screens.catalog.filters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.presentation.screens.catalog.CatalogViewModel
import com.dmitry.yume.presentation.screens.catalog.GenresViewState

@Composable
fun GenresScreen(
    onBackClick: () -> Unit,
    navigateToCatalog: () -> Unit,
    catalogViewModel: CatalogViewModel = hiltViewModel(),
) {
    val genresState by catalogViewModel.genresState.collectAsStateWithLifecycle()

    when (genresState) {
        is GenresViewState.Loading -> {}
        is GenresViewState.Error -> {}
        is GenresViewState.Success -> {
            GenresContent(
                genres = (genresState as GenresViewState.Success).meta.genres,
                catalogViewModel = catalogViewModel,
                onBackClick = onBackClick,
                navigateToCatalog = navigateToCatalog,
            )
        }
    }
}