package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun Poster(
    posterUrl: String?
){
    AsyncImage(
        model = posterUrl,
        contentDescription = "Аниме постер",
        modifier = Modifier
            .padding(horizontal = 80.dp)
            .padding(top = 50.dp)
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
    )
}