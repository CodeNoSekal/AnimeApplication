package com.dmitry.yume.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.domain.models.Status
import com.dmitry.yume.presentation.screens.detail.components.StatusPickerContent
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun HomeScreen(
    onItemClick: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onPlayClick: (Int) -> Unit,
) {
    val progressData by homeViewModel.progressState.collectAsStateWithLifecycle()
    val homeData by homeViewModel.homeState.collectAsStateWithLifecycle()
    val heroListState by homeViewModel.heroListState.collectAsStateWithLifecycle()
    val heroFavoriteState by homeViewModel.heroFavoriteState.collectAsStateWithLifecycle()
    val progressActionState by homeViewModel.progressActionState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        homeViewModel.load()
    }

    HomeScreenContent(
        homeData, progressData, onItemClick, onSearchClicked, onPlayClick,
        heroListState = heroListState,
        heroFavoriteState = heroFavoriteState,
        progressActionState = progressActionState,
        onHeroFavoriteClick = homeViewModel::toggleHeroFavorite,
        onFavoriteErrorDismiss = homeViewModel::dismissFavoriteError,
        onHeroListClick = homeViewModel::openHeroList,
        onHeroListDismiss = homeViewModel::dismissHeroList,
        onHeroStatusChange = homeViewModel::setHeroStatus,
        onRemoveProgress = homeViewModel::clearProgress,
        onClearAllProgress = homeViewModel::clearAllProgress,
        onProgressErrorDismiss = homeViewModel::dismissProgressActionError,
    ) {
        homeViewModel.load(force = true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenContent(
    homeData: HomeViewState,
    progressData: ProgressViewState,
    onItemClick: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    onPlayClick: (Int) -> Unit,
    heroListState: HeroListState = HeroListState(),
    heroFavoriteState: HeroFavoriteState = HeroFavoriteState(),
    progressActionState: ProgressActionState = ProgressActionState(),
    onHeroFavoriteClick: () -> Unit = {},
    onFavoriteErrorDismiss: () -> Unit = {},
    onHeroListClick: () -> Unit = {},
    onHeroListDismiss: () -> Unit = {},
    onHeroStatusChange: (String?) -> Unit = {},
    onRemoveProgress: (Int) -> Unit = {},
    onClearAllProgress: () -> Unit = {},
    onProgressErrorDismiss: () -> Unit = {},
    onRetry: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(heroFavoriteState.error) {
        heroFavoriteState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            onFavoriteErrorDismiss()
        }
    }
    LaunchedEffect(progressActionState.error) {
        progressActionState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            onProgressErrorDismiss()
        }
    }
    Scaffold(
        topBar = { HomeTopBar(onSearchClicked) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when (homeData) {
            is HomeViewState.Success -> HomeContent(
                innerPadding = innerPadding,
                progressData = progressData,
                homeData = homeData.home,
                onPlayClick = onPlayClick,
                onItemClick = onItemClick,
                onHeroListClick = onHeroListClick,
                onHeroFavoriteClick = onHeroFavoriteClick,
                personalActionsEnabled = !heroListState.isSaving && !heroFavoriteState.isSaving,
                isFavoriteSaving = heroFavoriteState.isSaving,
                progressActionState = progressActionState,
                onRemoveProgress = onRemoveProgress,
                onClearAllProgress = onClearAllProgress,
            )
            HomeViewState.Loading -> HomePlaceholder(innerPadding)
            is HomeViewState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                Text("Не удалось загрузить домашний экран", textAlign = TextAlign.Center)
                TextButton(onClick = onRetry) { Text("Повторить") }
            }
        }
    }
    if (heroListState.animeId != null && homeData is HomeViewState.Success) {
        val hero = homeData.home.hero
        ModalBottomSheet(
            onDismissRequest = onHeroListDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            sheetGesturesEnabled = !heroListState.isSaving,
            containerColor = YumeTheme.colors.surfaceCard
        ) {
            StatusPickerContent(
                status = Status(
                    animeId = heroListState.animeId,
                    status = hero.myStatus,
                    favorite = hero.favorite,
                    score = hero.myScore,
                    review = null
                ),
                setStatus = onHeroStatusChange,
                enabled = !heroListState.isSaving && !heroFavoriteState.isSaving
            )
            if (heroListState.isSaving) {
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
            }
            heroListState.error?.let { message ->
                Text(message, color = YumeTheme.colors.statusDropped, modifier = Modifier.padding(20.dp))
            }
        }
    }
}
