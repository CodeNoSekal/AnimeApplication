package com.dmitry.yume.presentation.screens.catalog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onItemClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    catalogViewModel: CatalogViewModel = hiltViewModel(),
    onFiltersClick: () -> Unit
) {
    val catalog by catalogViewModel.catalog.collectAsStateWithLifecycle()
    CatalogContent(catalog, onItemClick, onSearchClick, onFiltersClick, catalogViewModel::setSorting)
}
