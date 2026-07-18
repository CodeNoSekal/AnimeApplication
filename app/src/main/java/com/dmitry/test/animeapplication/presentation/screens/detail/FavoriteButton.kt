package com.dmitry.test.animeapplication.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme.colors
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType

@Composable
fun FavoriteButton(
    onClick: () -> Unit,
    statusState: StatusViewState,
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
        shape = RoundedCornerShape(14.dp),
        color = mainColor,
        border = BorderStroke(1.dp, contentColor),
        modifier = Modifier.width(50.dp).height(50.dp)
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
                    contentDescription = null,
                    Modifier.size(18.dp),
                    tint = contentColor
                )
            }

            if (statusState is StatusViewState.Loading) {
                CircularProgressIndicator(
                    Modifier.size(20.dp), strokeWidth = 2.dp,
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