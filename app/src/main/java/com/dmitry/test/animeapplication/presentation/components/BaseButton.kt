package com.dmitry.test.animeapplication.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme.colors
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType

@Composable
fun BaseButton(
    onClick: () -> Unit,
    modifier: Modifier,
    text: String? = null,
    icon: Int? = null,
    disabled: Boolean = false
) {

    val mainColor =
        if (disabled) colors.surfaceCard.copy(alpha = 0.5f)
        else colors.surfaceCard

    val contentColor =
        if (disabled) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.onBackground

    Surface(
        onClick = onClick,
        enabled = !disabled,
        shape = RoundedCornerShape(14.dp),
        color = mainColor,
        border = BorderStroke(1.dp, colors.lineStrong),
        modifier = modifier.fillMaxWidth().height(46.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (text != null && icon != null) {
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    Modifier.size(18.dp),
                    tint = contentColor
                )
                Spacer(Modifier.width(8.dp))
                Text(text, style = YumeType.bodyMedium, color = contentColor)
            } else if (text != null) {
                Text(text, style = YumeType.bodyMedium, color = contentColor)
            } else if (icon != null) {
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    Modifier.size(18.dp),
                    tint = contentColor
                )
            }
        }
    }
}