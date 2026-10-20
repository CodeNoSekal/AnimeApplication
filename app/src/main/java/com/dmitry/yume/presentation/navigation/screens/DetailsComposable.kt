package com.dmitry.yume.presentation.navigation.screens

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.presentation.navigation.Details
import com.dmitry.yume.presentation.navigation.PlaybackDestination
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.screens.detail.DetailScreen
import com.dmitry.yume.presentation.screens.detail.DetailPlaceholder
import com.dmitry.yume.presentation.screens.detail.DetailViewModel
import com.dmitry.yume.presentation.screens.detail.DetailViewState

fun NavGraphBuilder.detailsComposable(parent: String, navController: NavController){
    composable(
        route = Details.routePattern(parent),
        arguments = listOf(navArgument(Details.ANIME_ID) { type = NavType.IntType })
    ) {
        val viewModel: DetailViewModel = hiltViewModel()
        val detailState by viewModel.state.collectAsStateWithLifecycle()
        val statusState by viewModel.statusState.collectAsStateWithLifecycle()
        val actionState by viewModel.actionState.collectAsStateWithLifecycle()
        val scoreEditor by viewModel.scoreEditor.collectAsStateWithLifecycle()

        when (val state = detailState) {
            is DetailViewState.Loading -> {
                DetailPlaceholder(onBackClick = { navController.popBackStack() })
            }
            is DetailViewState.Success -> {
                DetailScreen(
                    onBackClick = { navController.popBackStack() },
                    onPlayClick = { navController.navigate(PlaybackDestination.build(it)) },
                    animeData = (detailState as DetailViewState.Success).animeDetailed,
                    statusState = statusState,
                    setStatus = { viewModel.putStatus(it) },
                    setFavorite = viewModel::putFavorite,
                    actionState = actionState,
                    scoreEditor = scoreEditor,
                    onScoreClick = viewModel::openScoreEditor,
                    onScoreDismiss = viewModel::dismissScoreEditor,
                    onScoreSave = viewModel::saveScore,
                    onErrorDismiss = viewModel::dismissActionError,
                    onRetryPersonal = viewModel::load,
                    onRelationClick = { id ->
                        navController.navigate(Details.build(parent, id)) }
                )
            }
            is DetailViewState.Error -> {
                val (title, message) = when (state.error) {
                    AppError.NoConnection -> "Нет подключения" to "Проверьте интернет и попробуйте ещё раз"
                    AppError.NotFound     -> "Тайтл не найден" to null
                    AppError.Server       -> "Сервер недоступен" to "Попробуйте немного позже"
                    AppError.Unknown      -> "Не удалось загрузить страницу" to null
                }

                ErrorScreen(
                    title = title,
                    message = message,
                    onRetry = if (state.error == AppError.NotFound) null else viewModel::load
                )
            }
        }
    }
}
