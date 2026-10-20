package com.dmitry.yume.presentation.screens.home.components.hero

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlin.math.abs

@Composable
fun HeroBackground(
    modifier: Modifier = Modifier,
    posters: List<String?>,
    position: () -> Float
) {
    Box(modifier) {
        posters.forEachIndexed {index, poster ->
            AsyncImage(
                model = poster,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        alpha = pageCloseness(position(), index) * 0.2f
                    )
                    .blur(25.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to MaterialTheme.colorScheme.background.copy(0.0f),
                        0.7f to MaterialTheme.colorScheme.background.copy(0.0f),
                        1.0f to MaterialTheme.colorScheme.background.copy(1.0f)
                    )
                )
        )
    }
}
