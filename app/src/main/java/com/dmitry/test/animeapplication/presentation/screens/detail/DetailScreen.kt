package com.dmitry.test.animeapplication.presentation.screens.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.domain.models.AnimeDetailed
import com.dmitry.test.animeapplication.domain.models.Status
import com.dmitry.test.animeapplication.presentation.components.BaseButton
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme.colors
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType
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

    Scaffold(
        topBar = { DetailsTopBar(onBackClick) }
    ) { _ ->
        DetailContent(
            onPlayClick = onPlayClick,
            animeData = animeData,
            statusState = statusState,
            setFavorite = setFavorite,
            onStatusClick = { showStatusSheet = true }
        )
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

@Composable
fun DetailContent(
    onPlayClick: (Int) -> Unit,
    animeData: AnimeDetailed,
    statusState:  StatusViewState,
    setFavorite: () -> Unit,
    onStatusClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = animeData.posterUrl,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .blur(12.dp),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AsyncImage(
                    model = animeData.posterUrl,
                    contentDescription = animeData.title,
                    modifier = Modifier
                        .padding(horizontal = 80.dp)
                        .padding(top = 100.dp)
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    animeData.title?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp),
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    animeData.titleEn?.let {
                        Text(
                            text = it,
                            style = YumeType.bodyMedium,
                            color = colors.textMuted,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .height(50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FavoriteButton(
                        onClick = setFavorite ,
                        statusState = statusState
                    )

                    StatusButton(
                        onClick = onStatusClick,
                        statusState = statusState
                    )
                }

                Button(
                    onClick = { onPlayClick(animeData.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .height(50.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(painterResource(
                            id = R.drawable.play_24),
                            contentDescription = null,
                            Modifier.size(15.dp))
                        Text("Смотреть")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {},
        modifier = Modifier
            .height(85.dp),
        navigationIcon = {
            IconButton(onClick = {onBackClick()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
