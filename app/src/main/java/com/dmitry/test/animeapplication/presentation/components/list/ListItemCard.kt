package com.dmitry.test.animeapplication.presentation.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dmitry.test.animeapplication.domain.models.Anime
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType

@Composable
fun ListItemCard(
    anime: Anime,
    onItemClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = YumeTheme.colors
    val outerShape = RoundedCornerShape(8.dp)
    val innerShape = RoundedCornerShape(6.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))      // чтобы ripple клика был скруглён
            .clickable { onItemClicked(anime.id) }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {

        val statusBorderColor =
            when (anime.status) {
                "смотрю" -> YumeTheme.colors.statusWatching
                "в планах" -> YumeTheme.colors.statusPlanned
                "просмотрено" -> YumeTheme.colors.statusCompleted
                "брошено" -> YumeTheme.colors.statusDropped
                else -> null
            }

        Box(
            modifier = Modifier
                .width(108.dp)
                .aspectRatio(2f / 3f)
        ) {

            statusBorderColor?.let { color ->
                Box(
                    Modifier
                        .matchParentSize()
                        .border(6.dp, color.copy(alpha = 0.14f), outerShape)
                )

                Box(
                    Modifier
                        .matchParentSize()
                        .padding(2.dp)
                        .border(3.dp, color.copy(alpha = 0.24f), outerShape)
                )
            }

            Box(
                Modifier
                    .matchParentSize()
                    .padding(4.dp)
                    .border(
                        width = 2.dp,
                        color = statusBorderColor?.copy(alpha = 0.95f)
                            ?: colors.line,
                        shape = outerShape
                    )
                    .padding(2.dp)
                    .clip(innerShape)
            ) {
                AsyncImage(
                    model = anime.posterUrl,
                    contentDescription = anime.title,
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

                anime.rating?.let { rating ->
                    RatingBadge(rating, Modifier.align(Alignment.TopEnd))
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            anime.title?.let {
                Text(
                    text = it,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = YumeType.bodyMedium,
                    color = colors.textPrimary,
                )
            }
            val meta = listOfNotNull(anime.year?.toString(), anime.titleEn).joinToString(" · ")
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
    }
}