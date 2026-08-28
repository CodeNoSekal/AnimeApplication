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

@Composable
fun ContinueTab(
    data: ProgressData,
    onPlayClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Продолжить просмотр",
            style = YumeType.h2
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(data.items, key = { it.animeId }) { item ->
                ProgressItemCard(
                    item,
                    onPlayClick
                )
            }
        }
    }

}

@Composable
fun ProgressItemCard(
    data: ProgressItemData,
    onPlayClick: (Int) -> Unit
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
                        .fillMaxSize()
                        .padding(vertical = 4.dp, horizontal = 12.dp)

                ) {
                    data.title?.let {
                        Text(
                            text = it,
                            style = YumeType.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(top = 2.dp)
                        )
                    }

                    Text(
                        text = "${data.episodeNumber} серия",
                        color = YumeTheme.colors.textMuted,
                        style = YumeType.xs,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 2.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(
                    modifier = Modifier
                        .background(color = YumeTheme.colors.accent)
                        .fillMaxWidth((data.positionMs.toDouble() / data.durationMs).toFloat())
                        .height(3.dp),
                )
            }

        }
    }
}
