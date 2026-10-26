package com.dmitry.yume.presentation.screens.profile.main.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun TopBarIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: Int,
) {
    Surface(
        modifier = modifier
            .size(40.dp)
        ,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceCard.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, colors.line)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .padding(12.dp),
            tint = colors.textPrimary.copy(alpha = 0.8f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun TopBarIconButtonPreview() {
    YumeTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            repeat(5){
                TopBarIconButton(
                    onClick = {},
                    icon = R.drawable.pencil
                )
            }
        }
    }
}