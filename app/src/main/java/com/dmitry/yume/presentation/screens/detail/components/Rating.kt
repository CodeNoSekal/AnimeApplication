package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType
import java.util.Locale

@Composable
fun Rating(rating: Double?, score: Int?, enabled: Boolean, onScoreClick: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Рейтинг", style = YumeType.h3, color = colors.textPrimary)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(painterResource(R.drawable.star_24), null, Modifier.size(20.dp), tint = colors.rating)
                    Text(
                        rating?.takeIf { it > 0 }?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
                        style = YumeType.display,
                        color = colors.textPrimary
                    )
                }
                Text("Общая оценка", style = YumeType.xs, color = colors.textMuted)
            }
            Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ScoreButton(score, enabled, onScoreClick)
                if (score != null) Text("Ваша оценка", style = YumeType.xs, color = colors.textMuted)
            }
        }
    }
}
