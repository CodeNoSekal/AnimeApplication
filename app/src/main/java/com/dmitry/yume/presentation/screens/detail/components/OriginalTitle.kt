package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun OriginalTitle(
    originalTitle: String?
) {
    originalTitle?.let {
        Text(
            text = it,
            style = YumeType.bodyMedium,
            color = colors.textMuted,
            maxLines = 2,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
    }
}