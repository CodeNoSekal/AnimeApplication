package com.dmitry.yume.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 6.dp,
    subdued: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val pulse = transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .graphicsLayer { alpha = pulse.value }
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                YumeTheme.colors.surfaceCard.copy(
                    alpha = if (subdued) 0.7f else 1f
                )
            )
    )
}

@Composable
fun SkeletonText(
    style: TextStyle,
    widthFraction: Float,
    modifier: Modifier = Modifier,
    subdued: Boolean = false
) {
    val lineHeight = with(LocalDensity.current) {
        val height = if (style.lineHeight.isSpecified) style.lineHeight else style.fontSize * 1.4f
        height.toDp()
    }

    Box(
        modifier = modifier
            .height(lineHeight),
        contentAlignment = Alignment.CenterStart
    ) {
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .fillMaxHeight(0.65f),
            subdued = subdued
        )
    }
}
