package com.dmitry.yume.presentation.screens.home.components.ranking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.RankedAnime
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun RankedAnimeCard(
    anime: RankedAnime,
    onItemClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onItemClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy((-25).dp)
        ) {
            val iconId = getRankIcon(anime.rank)

            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(100.dp),
                tint = colors.accent
            )

            AsyncImage(
                model = anime.posterUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(135.dp)
                    .aspectRatio(2 / 3f)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
        }


        Text(
            text = anime.title.orEmpty(),
            style = YumeType.sm.copy(
                fontWeight = FontWeight.Bold,
                hyphens = Hyphens.Auto),
            color = colors.textPrimary,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(135.dp)
        )
    }
}

fun getRankIcon(rank: Int): Int {
    return when (rank) {
        1 -> R.drawable.ic_top_1
        2 -> R.drawable.ic_top_2
        3 -> R.drawable.ic_top_3
        4 -> R.drawable.ic_top_4
        5 -> R.drawable.ic_top_5
        6 -> R.drawable.ic_top_6
        7 -> R.drawable.ic_top_7
        8 -> R.drawable.ic_top_8
        9 -> R.drawable.ic_top_9
        10 -> R.drawable.ic_top_10
        else -> R.drawable.ic_top_1
    }
}
