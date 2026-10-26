package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ScoreButton(score: Int?, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        color = if (score != null) colors.rating.copy(alpha = 0.12f) else colors.surfaceCard,
        border = BorderStroke(1.dp, if (score != null) colors.rating.copy(alpha = 0.5f) else colors.lineStrong),
        modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = "Ваша оценка" }
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (score != null) Icon(painterResource(R.drawable.star_24), contentDescription = null, tint = colors.rating, modifier = Modifier.size(18.dp))
            Text(score?.let { "$it/10" } ?: "Оценить", style = YumeType.sm, color = colors.textPrimary)
        }
    }
}
