package com.dmitry.yume.presentation.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.ProgressData
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType
import com.dmitry.yume.presentation.screens.home.ProgressActionState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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

@Preview(showBackground = true, backgroundColor = 0xFF14131A, locale = "ru",)
@Composable
private fun ContinueTabPreview() {
    YumeTheme {
        PreviewImages {
            HomeScreenContent(
                onItemClick = {},
                onSearchClick = { },
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
