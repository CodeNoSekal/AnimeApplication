package com.dmitry.yume.presentation.screens.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.presentation.screens.detail.components.Background
import com.dmitry.yume.presentation.screens.detail.components.StatusPickerContent
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    onPlayClick: (Int) -> Unit,
    animeData: AnimeDetailed,
    statusState:  StatusViewState,
    setStatus: (String?) -> Unit,
    setFavorite: () -> Unit,
){
    var showStatusSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    val startFadePx = with(LocalDensity.current) { 300.dp.toPx() }
    val fadeDistancePx = with(LocalDensity.current) { 30.dp.toPx() }

    val topAppBarAlpha by remember {
        derivedStateOf {
            ((scrollState.value - startFadePx) / fadeDistancePx)
                .coerceIn(0f, 1f)
        }
    }

    Scaffold(
        topBar = { DetailsTopBar(onBackClick, topAppBarAlpha) }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Background(animeData.posterUrl)

                DetailContent(
                    animeData = animeData,
                    statusState = statusState,
                    setFavorite = setFavorite,
                    onStatusClick = { showStatusSheet = true },
                    onPlayClick = onPlayClick
                )
            }
        }
    }

    if (showStatusSheet && statusState is StatusViewState.Success) {
        ModalBottomSheet(
            onDismissRequest = { showStatusSheet = false},
            containerColor = colors.surfaceCard,
            sheetGesturesEnabled = false,
            sheetState = sheetState,
        ) {
            StatusPickerContent(
                statusState.status,
                setStatus = { status ->
                    scope.launch {
                        sheetState.hide()
                        showStatusSheet = false
                        setStatus(status)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(
    onBackClick: () -> Unit,
    topAppBarAlpha: Float,
) {

    val backgroundColor = MaterialTheme.colorScheme.background
        .copy(alpha = topAppBarAlpha)


    TopAppBar(
        title = {},
        modifier = Modifier
            .height(85.dp),
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
            containerColor = backgroundColor
        )
    )
}
