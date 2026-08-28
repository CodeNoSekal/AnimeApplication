package com.dmitry.yume.presentation.screens.catalog.filters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.presentation.screens.catalog.CatalogViewModel

@Composable
fun FiltersScreen(
    onBackClick: () -> Unit,
    onGenresClick: () -> Unit,
    catalogViewModel: CatalogViewModel = hiltViewModel(),
) {
    val draftFilters by catalogViewModel.draftFilters.collectAsStateWithLifecycle()

    FiltersContent(
        filters = draftFilters,
        onBackClick = onBackClick,
        onGenresClick = onGenresClick,
        onKindClick = catalogViewModel::assignType,
        onStatusClick = catalogViewModel::assignStatus,
        onCollectionClick = catalogViewModel::assignCollection,
        onDropClick = catalogViewModel::dropFilters,
        onApplyClick = {
            catalogViewModel.applyFilters()
            onBackClick()
        },
    )
}