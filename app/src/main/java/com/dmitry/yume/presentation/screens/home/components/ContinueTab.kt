package com.dmitry.yume.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.domain.models.ProgressData
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType
import com.dmitry.yume.presentation.screens.home.ProgressActionState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.home.HomeScreenContent
import com.dmitry.yume.presentation.screens.home.HomeViewState
import com.dmitry.yume.presentation.screens.home.ProgressViewState
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
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = "Продолжить просмотр",
                style = YumeType.h2,
                modifier = Modifier
                    .weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(30.dp)
                    .clickable(
                        enabled = !actionState.isBusy,
                        onClick = onClearAll
                    )
            ) {
                Text(
                    text = "Очистить",
                    style = YumeType.body,
                    color = colors.textMuted,
                    )
            }

        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
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
    val outerShape = RoundedCornerShape(18.dp)
    val innerShape = RoundedCornerShape(12.dp)

    Surface(
        onClick = { onPlayClick(data.animeId) },
        modifier = Modifier
            .clip(outerShape)
            .height(150.dp)
            .width(320.dp)
            .background(colors.surfaceCard)
            .border(1.dp, colors.line, outerShape)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            ) {
                AsyncImage(
                    model = data.posterUrl,
                    contentDescription = data.title,
                    modifier = Modifier
                        .height(126.dp)
                        .aspectRatio(2f / 3f)
                        .clip(innerShape),
                    contentScale = ContentScale.Crop,
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .fillMaxHeight()
                        .weight(1.0f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = data.title.orEmpty(),
                        style = YumeType.h3.copy(fontWeight = FontWeight.Bold),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 2.dp, end = 36.dp)
                    )

                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${data.episodeNumber} серия",
                            color = colors.textPrimary.copy(alpha = 0.7f),
                            style = YumeType.mono,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(top = 2.dp)
                        )

                        Box{
                            Box(
                                modifier = Modifier
                                    .height(4.5.dp)
                                    .clip(RoundedCornerShape(4.5.dp))
                                    .background(color = colors.line)
                                    .fillMaxWidth()
                            )

                            Box(
                                modifier = Modifier
                                    .height(4.5.dp)
                                    .clip(RoundedCornerShape(4.5.dp))
                                    .background(color = colors.accent)
                                    .fillMaxWidth((data.positionMs.toDouble() / data.durationMs).toFloat())
                            )
                        }

                        val lastMins = ((data.durationMs - data.positionMs + 59_999) / 60000).toInt().coerceAtLeast(0)

                        Text(
                            text = pluralStringResource(R.plurals.minutes_left, lastMins, lastMins),
                            color = colors.textMuted,
                            style = YumeType.xs,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(top = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Удалить из продолжения",
                    tint = colors.textMuted,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A, locale = "ru",)
@Composable
private fun HomeScreenPreview() {
    YumeTheme {
        PreviewImages {
            HomeScreenContent(
                onItemClick = {},
                onSearchClicked = { },
                onPlayClick = { },
                homeData = HomeViewState.Success(PreviewData.homeData),
                progressData = ProgressViewState.Success(PreviewData.progress),
                progressActionState = ProgressActionState(),
                onRemoveProgress = {},
                onClearAllProgress = {},
                onProgressErrorDismiss = {},
                onRetry = {}
            )
        }
    }
}

