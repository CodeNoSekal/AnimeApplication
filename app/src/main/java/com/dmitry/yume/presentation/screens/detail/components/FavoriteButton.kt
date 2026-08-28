package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.screens.detail.StatusViewState
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun FavoriteButton(
    onClick: () -> Unit,
    statusState: StatusViewState,
    enabled: Boolean = true,
) {

    val mainColor =
        Color.Black.copy(alpha = 0.1f)


    val contentColor =
        if ((statusState is StatusViewState.Success)) {
            if (statusState.status.favorite)
                colors.accent
            else
                colors.textMuted
        } else colors.textMuted


    Surface(
        onClick = { onClick() },
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        color = mainColor,
        border = BorderStroke(1.dp, contentColor),
        modifier = Modifier.width(48.dp).height(48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (statusState is StatusViewState.Success) {
                Icon(
                    painterResource(R.drawable.heart_24),
                    contentDescription = if (statusState.status.favorite) "Убрать из избранного" else "Добавить в избранное",
                    Modifier.size(18.dp),
                    tint = contentColor
                )
            }

            if (statusState is StatusViewState.Loading) {
                CircularProgressIndicator(
                    Modifier.size(18.dp), strokeWidth = 2.dp,
                    color = colors.textMuted
                )
            }

            if (statusState is StatusViewState.Error) {
                Icon(
                    painterResource(R.drawable.cross_24),
                    contentDescription = null,
                    Modifier.size(18.dp),
                    tint = colors.textMuted
                )
            }
        }
    }
}
