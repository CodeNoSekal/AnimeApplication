package com.dmitry.yume.presentation.screens.catalog.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.screens.catalog.Order
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun OrderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    order: Order,
    isActive: Boolean = false
) {

    val mainColor =
        if (isActive) colors.accent
        else colors.surfaceCard


    Surface(
        onClick = onClick,
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
            when (order) {
                Order.Asc -> {
                    Icon(
                        painterResource(R.drawable.sort_amount_up),
                        contentDescription = null,
                        Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Order.Desc -> {
                    Icon(
                        painterResource(R.drawable.sort_amount_down),
                        contentDescription = null,
                        Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

        }
    }
}