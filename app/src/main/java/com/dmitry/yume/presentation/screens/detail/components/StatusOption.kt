package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun StatusOption(
    text: String,
    status: String?,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val contentColor =
        when (status) {
            "смотрю" -> colors.statusWatching
            "в планах" -> colors.statusPlanned
            "просмотрено" -> colors.statusCompleted
            "брошено" -> colors.statusDropped
            else -> colors.textMuted
        }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val selectedColor =
                if (selected)
                    contentColor
                else contentColor.copy(alpha = 0.35f)

            CheckCircle(
                selected,
                selectedColor
            )

            Text(
                text = text,
                style = YumeType.bodyMedium,
                color = selectedColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
