package com.dmitry.yume.presentation.screens.catalog.components.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.Genre
import com.dmitry.yume.presentation.screens.catalog.CatalogViewModel
import com.dmitry.yume.presentation.screens.catalog.GenresViewState
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun GenresScreen(
    onBackClick: () -> Unit,
    catalogViewModel: CatalogViewModel = hiltViewModel(),
) {
    val genresState by catalogViewModel.genresState.collectAsStateWithLifecycle()
    var genres: List<Genre>

    if (genresState is GenresViewState.Success){
        genres = (genresState as GenresViewState.Success).genres.genres
    } else {
        return
    }

    val scrollState = rememberScrollState()
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        topBar = {
            GenresTopBar(onBackClick)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            items(
                items = genres
            ){ genre ->
                GenreCard(genre)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenresTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Жанры",
                style = YumeType.h3
            )
        },
        modifier = Modifier
            .height(70.dp),
        navigationIcon = {
            IconButton(onClick = {onBackClick()
            }) {
                Icon(
                    painter = painterResource(R.drawable.angle_small_left),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun GenreCard(
    genre: Genre,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var genreChecked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    genreChecked = !genreChecked
                }
            )
    ) {
        Checkbox(
            checked = genreChecked,
            onCheckedChange = {  },
        )

        Text(
            text = genre.name,
            style = YumeType.bodyMedium
        )
    }
}