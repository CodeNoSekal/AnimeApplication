package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.presentation.screens.catalog.AnimeKind
import com.dmitry.yume.presentation.screens.catalog.Status
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun Relations(
    items: List<Anime>,
    modifier: Modifier = Modifier,
    currentTitleId: Int,
    onItemClicked: (Int) -> Unit,
    onShowAllClick: () -> Unit,
){
    val uniqueItems = items.distinctBy { it.id }
    if (uniqueItems.size <= 1) return

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Связанное",
                style = YumeType.h3
            )
            TextButton(onClick = onShowAllClick) {
                Text("Показать все", style = YumeType.sm, color = colors.textMuted)
            }
        }

        val currentIndex = uniqueItems.indexOfFirst { it.id == currentTitleId }

        val maxStartIndex = (uniqueItems.size - 3).coerceAtLeast(0)

        val startIndex = (currentIndex - 1)
            .coerceIn(0, maxStartIndex)

        val relationsList = uniqueItems
            .drop(startIndex)
            .take(3)

        relationsList.forEach { item ->
            RelationItem(item, currentTitleId, onItemClicked)
        }
    }
}

@Composable
internal fun RelationItem(item: Anime, currentTitleId: Int, onItemClicked: (Int) -> Unit) {
    val cardShape = RoundedCornerShape(12.dp)
    val outerShape = RoundedCornerShape(8.dp)
    val innerShape = RoundedCornerShape(7.dp)
    val statusGlow =
        when (item.myStatus) {
            "watching", "смотрю" -> colors.statusWatching to 0.34f
            "planned", "в планах" -> colors.statusPlanned to 0.44f
            "completed", "просмотрено" -> colors.statusCompleted to 0.46f
            "dropped", "брошено" -> colors.statusDropped to 0.36f
            else -> null
        }

    val statusGlowModifier = statusGlow
        ?.let { (glowColor, peakAlpha) ->
            Modifier.drawWithCache {
                val glow = Brush.radialGradient(
                    colorStops = arrayOf(
                        0f to glowColor.copy(alpha = peakAlpha),
                        0.30f to glowColor.copy(alpha = peakAlpha * 0.52f),
                        0.66f to glowColor.copy(alpha = peakAlpha * 0.16f),
                        1f to Color.Transparent,
                    ),
                    center = Offset(
                        x = 136.dp.toPx(),
                        y = size.height * 0.36f,
                    ),
                    radius = size.width * 0.58f,
                )

                onDrawBehind {
                    drawRect(glow)
                }
            }
        }
        ?: Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(117.dp)
            .clip(cardShape)
            .then(statusGlowModifier)
            .clickable(
                enabled = item.id != currentTitleId,
                onClick = {
                    onItemClicked(item.id)
                }
            )
            .padding(6.dp)
        ,
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .width(70.dp)
                .aspectRatio(2f / 3f)
        ) {
            Box(
                Modifier
                    .matchParentSize()
                    .border(
                        width = 1.dp,
                        color = statusGlow?.first?.copy(alpha = 0.92f) ?: colors.line,
                        shape = outerShape
                    )
                    .padding(1.dp)
                    .clip(innerShape)
            ) {
                AsyncImage(
                    model = item.posterUrl,
                    contentDescription = item.title,
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
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1.0f)
        ) {
            item.title?.let {
                Text(
                    text = it,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = YumeType.bodyMedium,
                    color = colors.textPrimary,
                )
            }

            val meta = listOfNotNull(
                item.year?.toString(),
                AnimeKind.entries
                    .firstOrNull { it.raw == item.kind }
                    ?.title,
                Status.entries
                    .firstOrNull { it.raw == item.status }
                    ?.title
            ).joinToString(" · ")

            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = YumeType.xs,
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (item.id == currentTitleId) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_small_left_24),
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier
                            .size(16.dp)
                    )

                    Text(
                        text = "Вы тут",
                        style = YumeType.sm,
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}
