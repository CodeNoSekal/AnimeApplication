package com.dmitry.yume.presentation.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ListItemCard(
    anime: Anime,
    onItemClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = YumeTheme.colors
    val cardShape = RoundedCornerShape(12.dp)
    val outerShape = RoundedCornerShape(8.dp)
    val innerShape = RoundedCornerShape(7.dp)
    val statusGlow =
        when (anime.status) {
            "смотрю" -> colors.statusWatching to 0.34f
            "в планах" -> colors.statusPlanned to 0.44f
            "просмотрено" -> colors.statusCompleted to 0.46f
            "брошено" -> colors.statusDropped to 0.36f
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
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .then(statusGlowModifier)
            .clickable { onItemClicked(anime.id) }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .width(108.dp)
                .aspectRatio(2f / 3f)
        ) {
            Box(
                Modifier
                    .matchParentSize()
                    .border(
                        width = 1.dp,
                        color = colors.line,
                        shape = outerShape
                    )
                    .padding(1.dp)
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
