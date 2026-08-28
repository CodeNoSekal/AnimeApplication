package com.dmitry.yume.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme

/** Matches the quiet, static placeholders used by the catalog. */
@Composable
fun SkeletonBlock(modifier: Modifier = Modifier, cornerRadius: Dp = 6.dp, subdued: Boolean = false) {
    Box(modifier.clip(RoundedCornerShape(cornerRadius))
        .background(YumeTheme.colors.surfaceCard.copy(alpha = if (subdued) 0.7f else 1f)))
}
