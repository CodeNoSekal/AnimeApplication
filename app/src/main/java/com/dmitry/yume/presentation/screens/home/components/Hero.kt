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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
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
) {
    val colors = YumeTheme.colors
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
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .heightIn(max = 560.dp)
            .background(colors.surfaceBg)
    ) {
        AsyncImage(
            model = heroData.posterUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Black,
                                0.32f to Color.Black,
                                0.92f to Color.Transparent
                            )
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Transparent,
                        0.32f to Color.Transparent,
                        0.92f to colors.surfaceBg.copy(alpha = 0.6f),
                        1f to colors.surfaceBg.copy(alpha = 0.6f)
                    )
                )
            )
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("РЕКОМЕНДУЕМ", style = YumeType.overline, color = colors.violet)

                Text(
                    title,
                    style = YumeType.displayLg.copy(hyphens = Hyphens.Auto),
                    color = colors.textPrimary,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    heroData.rating?.let { rating ->
                        RatingBadge(rating, contentPadding = 0.dp)
                    }

                    if (meta.isNotBlank()) {
                        Text(meta, style = YumeType.sm, color = colors.textSecondary)
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
                    contentPadding = PaddingValues(0.dp),
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
                    contentPadding = PaddingValues(0.dp),
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
        )
    }
}
