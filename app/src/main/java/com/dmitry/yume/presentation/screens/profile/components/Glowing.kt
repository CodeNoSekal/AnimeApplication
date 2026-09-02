package com.dmitry.yume.presentation.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.presentation.screens.profile.ProfileViewModel
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun Glowing() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.34f),
                        Color.Transparent,
                    ),
                    center = Offset(x = Float.POSITIVE_INFINITY * 0.7f, y = 0f),
                    radius = 900f,
                )
            )
    )
}
