package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun BoxScope.Background(
    posterUrl: String?
){
    AsyncImage(
        model = posterUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .blur(12.dp),
        contentScale = ContentScale.Crop,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    )
}