package com.dmitry.yume.presentation.screens.home.components.hero

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun HeroPoster(
    modifier: Modifier = Modifier,
    poster: String?,
){
    AsyncImage(
        model = poster,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(2/3f)
            .width(120.dp)
            .clip(RoundedCornerShape(14.dp))
    )
}