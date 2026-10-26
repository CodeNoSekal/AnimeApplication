package com.dmitry.yume.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.presentation.ui.theme.YumeType
import androidx.compose.ui.text.font.FontWeight
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

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
