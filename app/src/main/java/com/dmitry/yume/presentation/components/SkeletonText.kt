package com.dmitry.yume.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.isSpecified

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
