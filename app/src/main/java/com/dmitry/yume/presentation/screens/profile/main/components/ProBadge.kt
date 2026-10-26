package com.dmitry.yume.presentation.screens.profile.main.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ProBadge() {
    Surface(
        color = colors.accent.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke((0.5).dp, colors.accent)
    ) {
        Text(
            text = "PRO",
            style = YumeType.xs.copy(
                fontWeight = FontWeight.ExtraBold,
            ),
            color = colors.accent,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun ProBadgePreview() {
    YumeTheme {
        PreviewImages {
            ProBadge()
        }
    }
}

