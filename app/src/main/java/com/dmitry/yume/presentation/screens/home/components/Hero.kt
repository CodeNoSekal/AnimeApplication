package com.dmitry.yume.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.presentation.components.list.RatingBadge
import com.dmitry.yume.presentation.screens.catalog.AnimeKind
import com.dmitry.yume.presentation.screens.catalog.Status
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun Hero(
    onItemClick: (Int) -> Unit,
    heroData: Anime,
    onListClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    personalActionsEnabled: Boolean = true,
    isFavoriteSaving: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = YumeTheme.colors
    val shape = RoundedCornerShape(20.dp)
    val title = heroData.title?.takeIf { it.isNotBlank() }
        ?: heroData.titleEn?.takeIf { it.isNotBlank() }
        ?: "Открыть тайтл"
    val meta = listOfNotNull(
        heroData.year?.toString(),
        AnimeKind.entries.firstOrNull { it.raw == heroData.kind }?.title,
        Status.entries.firstOrNull { it.raw == heroData.status }?.title,
    ).joinToString(" · ")
    val personalStatus = heroData.myStatus?.takeIf { it.isNotBlank() }
    val statusGlow = when (personalStatus) {
        "watching", "смотрю" -> colors.statusWatching to 0.34f
        "planned", "в планах" -> colors.statusPlanned to 0.44f
        "completed", "просмотрено" -> colors.statusCompleted to 0.46f
        "dropped", "брошено" -> colors.statusDropped to 0.36f
        else -> null
    }
    val statusColor = statusGlow?.first ?: colors.textSecondary
    val favoriteColor = if (heroData.favorite) colors.accent else colors.textSecondary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp)
            .clip(shape)
            .background(colors.surfaceCard)
            .border(1.dp, colors.lineStrong, shape),
    ) {
        AsyncImage(
            model = heroData.posterUrl,
            contentDescription = null,
            modifier = Modifier.matchParentSize().blur(24.dp),
            contentScale = ContentScale.Crop,
        )
        // Keep text readable even on devices without blur support.
        Box(
            Modifier.matchParentSize().background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF0A0B16).copy(alpha = 0.94f), Color(0xFF0A0B16).copy(alpha = 0.55f))
                )
            )
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xFF0A0B16).copy(alpha = 0.85f))
                )
            )
        )
        // Above the dark scrims, below the content: the same falloff as catalog cards.
        statusGlow?.let { (glowColor, peakAlpha) ->
            Box(Modifier.matchParentSize().drawWithCache {
                val glow = Brush.radialGradient(
                    colorStops = arrayOf(
                        0f to glowColor.copy(alpha = peakAlpha),
                        0.30f to glowColor.copy(alpha = peakAlpha * 0.52f),
                        0.66f to glowColor.copy(alpha = peakAlpha * 0.16f),
                        1f to Color.Transparent
                    ),
                    center = Offset(size.width - 148.dp.toPx(), size.height * 0.36f),
                    radius = size.width * 0.72f
                )
                onDrawBehind { drawRect(glow) }
            })
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("В ФОКУСЕ", style = YumeType.overline, color = colors.textSecondary)
                    if (meta.isNotBlank()) {
                        Text(meta, style = YumeType.xs, color = colors.textSecondary)
                    }
                    Text(
                        title,
                        style = YumeType.h2.copy(hyphens = Hyphens.Auto),
                        color = colors.textPrimary,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    Modifier.width(132.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surfaceCard)
                        .border(
                            1.dp,
                            if (personalStatus != null) statusColor else colors.lineStrong,
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    AsyncImage(
                        model = heroData.posterUrl,
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                    heroData.rating?.let { rating ->
                        RatingBadge(rating, Modifier.align(Alignment.TopEnd).padding(5.dp))
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onItemClick(heroData.id) },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White
                    )
                ) {
                    Text("Подробнее", style = YumeType.bodyMedium)
                }
                OutlinedButton(
                    onClick = onListClick,
                    enabled = personalActionsEnabled,
                    modifier = Modifier.size(48.dp).semantics {
                        stateDescription = personalStatus ?: "Не добавлен в список"
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.65f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = statusColor,
                        containerColor = statusColor.copy(alpha = 0.12f)
                    )
                ) {
                    Icon(
                        imageVector = if (personalStatus == null) Icons.Outlined.BookmarkAdd else Icons.Filled.Bookmark,
                        contentDescription = if (personalStatus == null) "Добавить в список" else "Изменить список",
                        modifier = Modifier.size(22.dp)
                    )
                }
                OutlinedButton(
                    onClick = onFavoriteClick,
                    enabled = personalActionsEnabled,
                    modifier = Modifier.size(48.dp).semantics {
                        stateDescription = if (heroData.favorite) "В избранном" else "Не в избранном"
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, favoriteColor.copy(alpha = 0.65f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = favoriteColor,
                        containerColor = favoriteColor.copy(alpha = 0.12f)
                    )
                ) {
                    if (isFavoriteSaving) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = favoriteColor)
                    } else {
                        Icon(
                            imageVector = if (heroData.favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (heroData.favorite) "Убрать из избранного" else "Добавить в избранное",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B16, widthDp = 360)
@Preview(showBackground = true, backgroundColor = 0xFF0A0B16, widthDp = 320, fontScale = 1.3f)
@Composable
private fun HeroPreview() {
    YumeTheme {
        Hero(
            onItemClick = {},
            onListClick = {},
            onFavoriteClick = {},
            heroData = Anime(
                id = 1,
                title = "Человек-бензопила",
                titleEn = null,
                posterUrl = null,
                year = 2023,
                rating = 9.1,
                favorite = false,
                kind = "tv",
                myStatus = "в планах"
            ),
            modifier = Modifier.padding(12.dp)
        )
    }
}
