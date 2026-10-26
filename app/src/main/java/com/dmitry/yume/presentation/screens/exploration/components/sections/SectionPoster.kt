package com.dmitry.yume.presentation.screens.exploration.components.sections

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun SectionPoster(
    modifier: Modifier = Modifier,
    url: String,
    width: Dp,
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape((width/10)))
            .clip(RoundedCornerShape((width/10)))
            .size(width = width, height = width * 1.5f)
        ,
    )
}
