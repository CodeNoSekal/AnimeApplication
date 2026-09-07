package com.dmitry.yume.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun SearchButton(
    onClick: () -> Unit,
    alpha: Float = 1.0f
) {
    val colors = YumeTheme.colors

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard.copy(alpha),
        border = BorderStroke(1.dp, colors.lineStrong),
        modifier = Modifier.fillMaxWidth().height(46.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.search_24),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text("Поиск по каталогу...", color = colors.textMuted, style = YumeType.bodyMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
