package com.dmitry.yume.presentation.screens.home.components

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
import androidx.compose.material3.Surface
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.domain.models.ProgressData
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType
import com.dmitry.yume.presentation.screens.home.ProgressActionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun ContinueTab(
    data: ProgressData,
    onPlayClick: (Int) -> Unit,
    actionState: ProgressActionState = ProgressActionState(),
    onRemove: (Int) -> Unit = {},
    onClearAll: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Продолжить просмотр", style = YumeType.h2, modifier = Modifier.weight(1f))
            TextButton(
                onClick = { onClearAll() },
                enabled = !actionState.isBusy
            ) {
                Text(
                    text = "Очистить",
                    style = YumeType.body,
                    color = colors.textMuted
                )
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(data.items, key = { it.animeId }) { item ->
                ProgressItemCard(
                    item,
                    onPlayClick,
                    onRemove = { onRemove(item.animeId) },
                )
            }
        }
    }
}

@Composable
fun ProgressItemCard(
    data: ProgressItemData,
    onPlayClick: (Int) -> Unit,
    onRemove: () -> Unit = {},
) {
    val outerShape = RoundedCornerShape(8.dp)

    Surface(
        onClick = { onPlayClick(data.animeId) },
        modifier = Modifier
            .clip(outerShape)
            .height(120.dp)
            .width(270.dp)
            .background(YumeTheme.colors.surfaceCard)
    ) {
        val innerShape = RoundedCornerShape(8.dp)

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(2f / 3f)
                        .clip(innerShape)
                ) {
                    AsyncImage(
                        model = data.posterUrl,
                        contentDescription = data.title,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0f to Color(0xFF06070E).copy(alpha = 0.55f),
                                    0.5f to Color.Transparent
                                )
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxSize()
                        .weight(1.0f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        data.title?.let {
                            Text(
                                text = it,
                                style = YumeType.mono,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${data.episodeNumber} серия",
                        color = colors.textMuted,
                        style = YumeType.xs,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 2.dp)
                    )
                }

                IconButton(
                    onClick = onRemove,
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = "Удалить из продолжения")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(
                    modifier = Modifier
                        .background(color = colors.accent)
                        .fillMaxWidth((data.positionMs.toDouble() / data.durationMs).toFloat())
                        .height(4.dp),
                )
            }
        }
    }
}
