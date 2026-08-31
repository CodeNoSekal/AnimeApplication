package com.dmitry.yume.presentation.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun Avatar(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(shape)
            .background(colors.surfaceCard)
            .border(2.dp, colors.accent, shape)
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Аватар",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}
