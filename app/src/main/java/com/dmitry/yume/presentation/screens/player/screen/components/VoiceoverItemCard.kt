package com.dmitry.yume.presentation.screens.player.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun VoiceoverItemCard(
    voiceoverId: Int,
    selectedVoiceoverId: Int?,
    logoUrl: String,
    voiceoverName: String,
    onClick: (Int) -> Unit
) {
    val cardColor =
        if (voiceoverId == selectedVoiceoverId)
            colors.accent.copy(alpha = 0.3f)
        else
            colors.surfaceCard

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                onClick = {
                    if (voiceoverId != selectedVoiceoverId)
                        onClick(voiceoverId)
                }
            )
            .background(color = cardColor)
            .padding(horizontal = 5.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )

        Text(
            text = voiceoverName,
            style = YumeType.bodyMedium
        )
    }
}
