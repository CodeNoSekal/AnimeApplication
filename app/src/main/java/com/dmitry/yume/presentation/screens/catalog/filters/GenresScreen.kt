package com.dmitry.yume.presentation.screens.catalog.filters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.Genre
import com.dmitry.yume.presentation.components.BaseButton
import com.dmitry.yume.presentation.screens.catalog.CatalogViewModel
import com.dmitry.yume.presentation.screens.catalog.GenresViewState
import com.dmitry.yume.presentation.screens.catalog.filters.components.TripleCheckState
import com.dmitry.yume.presentation.screens.catalog.filters.components.TripleCheckbox
import com.dmitry.yume.presentation.screens.catalog.filters.components.GenresTopBar
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun GenresScreen(
    onBackClick: () -> Unit,
    navigateToCatalog: () -> Unit,
    catalogViewModel: CatalogViewModel = hiltViewModel(),
) {
    val metaState by catalogViewModel.metaState.collectAsStateWithLifecycle()

    when (metaState) {
        is GenresViewState.Loading -> {}
        is GenresViewState.Error -> {}
        is GenresViewState.Success -> {
            GenresContent(
                genres = (metaState as GenresViewState.Success).meta.genres,
                catalogViewModel = catalogViewModel,
                onBackClick = onBackClick,
                navigateToCatalog = navigateToCatalog,
            )
        }
    }
}


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


@Composable
fun GenreCard(
    genre: Genre,
    included: Set<Int>,
    excluded: Set<Int>,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = (4.5).dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        TripleCheckbox(
            state =
                when (genre.id) {
                    in included -> TripleCheckState.Checked
                    in excluded -> TripleCheckState.Rejected
                    else -> TripleCheckState.Unchecked
                },
        )

        Text(
            text = genre.name,
            style = YumeType.bodyMedium
        )
    }
}

@Composable
fun GenresBottomBar(
    onApplyClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .height(65.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseButton(
                onClick = onApplyClick,
                modifier = Modifier
                    .weight(1.0f),
                text = "Применить",
            )
        }
    }
}