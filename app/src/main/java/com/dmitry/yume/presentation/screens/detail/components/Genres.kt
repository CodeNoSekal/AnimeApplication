package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun Genres(
    genres: List<String>?
) {
    val sortedGenres = genres
        .orEmpty()
        .sortedByDescending { it.length }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        sortedGenres.forEach { genre ->
            GenreButton(genre)
        }
    }
}

@Composable
fun GenreButton(
    genre: String,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceCard,
        border = BorderStroke(
            width = 1.dp,
            color = colors.lineStrong.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .wrapContentSize()
            .clickable {

            }
    ) {
        Text(
            text = genre,
            style = YumeType.sm,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}