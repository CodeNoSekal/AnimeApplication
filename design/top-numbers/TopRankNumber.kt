package com.dmitry.yume.presentation.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R

/**
 * Контурные цифры топа из макета.
 * Nunito Black 150, трекинг −0.07em, обводка 2.5dp цветом #A9A8FF с альфой 65%.
 * Векторы лежат в res/drawable/ic_top_1.xml … ic_top_10.xml.
 */
@DrawableRes
fun topRankDrawable(rank: Int): Int = when (rank) {
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
    else -> error("Топ поддерживает места 1–10, получено $rank")
}

@Composable
fun TopRankNumber(rank: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(topRankDrawable(rank)),
        contentDescription = null, // место озвучивается в карточке целиком
        modifier = modifier,
    )
}

/**
 * Раскладка как в макете (вариант A): цифра снизу слева, постер 160×240 справа поверх неё.
 * Ширина 226dp для мест 1–9 и 262dp для 10.
 */
@Composable
fun TopRankPosterSlot(rank: Int, poster: @Composable () -> Unit) {
    Box(Modifier.size(width = if (rank == 10) 262.dp else 226.dp, height = 240.dp)) {
        TopRankNumber(rank, Modifier.align(Alignment.BottomStart))
        Box(Modifier.align(Alignment.TopEnd).size(width = 160.dp, height = 240.dp)) { poster() }
    }
}
