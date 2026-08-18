package com.dmitry.yume.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dmitry.yume.domain.models.ProgressData
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun HomeScreen(
    onItemClick: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onPlayClick: (Int) -> Unit,
) {
    val progressData by homeViewModel.progressState.collectAsStateWithLifecycle()
    val homeData by homeViewModel.homeState.collectAsStateWithLifecycle()

    LaunchedEffect(homeViewModel) {
        homeViewModel.load()
    }

    Scaffold(
        topBar = { HomeTopBar(onSearchClicked) }
    ) { innerPadding ->
        if (homeData is HomeViewState.Success){
            HomeContent(
                innerPadding = innerPadding,
                progressData = progressData,
                homeData = (homeData as HomeViewState.Success).home,
                onPlayClick = onPlayClick,
                onItemClick = onItemClick,
            )
        }
    }
}
