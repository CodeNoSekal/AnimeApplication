package com.dmitry.yume.presentation.screens.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    onItemClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onPlayClick: (Int) -> Unit,
) {
    val progressData by homeViewModel.progressState.collectAsStateWithLifecycle()
    val homeData by homeViewModel.homeState.collectAsStateWithLifecycle()
    val progressActionState by homeViewModel.progressActionState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        homeViewModel.updateProgress()
    }

    HomeScreenContent(
        homeData, progressData, onItemClick, onSearchClick, onPlayClick,
        progressActionState = progressActionState,
        onRemoveProgress = homeViewModel::clearProgress,
        onClearAllProgress = homeViewModel::clearAllProgress,
        onProgressErrorDismiss = homeViewModel::dismissProgressActionError,
        onRetry = homeViewModel::load
    )
}