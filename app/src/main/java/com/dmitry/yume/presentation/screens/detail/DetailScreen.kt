package com.dmitry.yume.presentation.screens.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.presentation.screens.detail.components.Background
import com.dmitry.yume.presentation.screens.detail.components.ScorePickerContent
import com.dmitry.yume.presentation.screens.detail.components.StatusPickerContent
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    onPlayClick: (Int) -> Unit,
    animeData: AnimeDetailed,
    statusState: StatusViewState,
    setStatus: (String?) -> Unit,
    setFavorite: () -> Unit,
    onRelationClick: (Int) -> Unit,
    actionState: DetailActionState,
    scoreEditor: ScoreEditorState,
    onScoreClick: () -> Unit,
    onScoreDismiss: () -> Unit,
    onScoreSave: (Int?) -> Unit,
    onErrorDismiss: () -> Unit,
    onRetryPersonal: () -> Unit
) {
    var showStatusSheet by rememberSaveable(animeData.id) { mutableStateOf(false) }
    var showAllRelations by rememberSaveable(animeData.id) { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val startFadePx = with(LocalDensity.current) { 300.dp.toPx() }
    val fadeDistancePx = with(LocalDensity.current) { 30.dp.toPx() }
    val topBarAlpha by remember(startFadePx, fadeDistancePx) {
        derivedStateOf { ((scrollState.value - startFadePx) / fadeDistancePx).coerceIn(0f, 1f) }
    }
    LaunchedEffect(actionState.error, scoreEditor.isOpen) {
        if (!scoreEditor.isOpen) actionState.error?.let {
            snackbar.showSnackbar(it)
            onErrorDismiss()
        }
    }
    BackHandler(enabled = showAllRelations) { showAllRelations = false }
    if (showAllRelations) {
        RelationsScreen(
            items = animeData.relations,
            currentTitleId = animeData.id,
            onBackClick = { showAllRelations = false },
            onItemClick = onRelationClick
        )
        return
    }
    Scaffold(
        topBar = { DetailsTopBar(onBackClick, topBarAlpha) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(scrollState)
        ) {
            Box(Modifier.fillMaxWidth()) {
                Background(animeData.posterUrl)
                DetailContent(
                    animeData = animeData,
                    statusState = statusState,
                    setFavorite = setFavorite,
                    onStatusClick = { showStatusSheet = true },
                    onScoreClick = onScoreClick,
                    onPlayClick = onPlayClick,
                    onRelationClick = onRelationClick,
                    onAllRelationsClick = { showAllRelations = true },
                    onRetryPersonal = onRetryPersonal,
                    isSaving = actionState.isSaving
                )
            }
        }
    }
    if (showStatusSheet && statusState is StatusViewState.Success) {
        ModalBottomSheet(
            onDismissRequest = { showStatusSheet = false },
            containerColor = colors.surfaceCard,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            StatusPickerContent(
                statusState.status,
                setStatus = {
                    showStatusSheet = false
                    setStatus(it)
                },
                enabled = !actionState.isSaving
            )
        }
    }
    if (scoreEditor.isOpen) {
        ModalBottomSheet(
            onDismissRequest = onScoreDismiss,
            containerColor = colors.surfaceCard,
            sheetGesturesEnabled = !actionState.isSaving,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            ScorePickerContent(
                title = animeData.title ?: animeData.titleEn,
                initialScore = scoreEditor.initialScore,
                isSaving = actionState.isSaving,
                error = actionState.error,
                onSave = onScoreSave
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(onBackClick: () -> Unit, topAppBarAlpha: Float, title: String = "") {
    TopAppBar(
        title = { if (title.isNotEmpty()) Text(title) },
        modifier = Modifier.height(85.dp),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.angle_small_left),
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = topAppBarAlpha)
        )
    )
}
