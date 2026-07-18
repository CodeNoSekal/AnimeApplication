package com.dmitry.test.animeapplication.presentation.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme.colors
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType

@Composable
fun RatingBadge(
    rating: Double,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box{
            Icon(painterResource(
                id = R.drawable.star_24),
                contentDescription = null,
                modifier = Modifier
                    .offset(y = 1.dp)
                    .size(12.dp),
                tint = Color.Black.copy(alpha = 0.75f),
            )
            Icon(painterResource(
                id = R.drawable.star_24),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = colors.rating
            )
        }
        Text(
            "%.1f".format(rating),
            style = YumeType.xs.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.85f),
                    offset = Offset(0f, 1f),
                    blurRadius = 4f
                )
            ),
            color = colors.textPrimary
        )
    }
}