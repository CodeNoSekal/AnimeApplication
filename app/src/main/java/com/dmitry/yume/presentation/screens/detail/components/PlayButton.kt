package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun PlayButton(
    onPlayClick: (Int) -> Unit,
    animeData: AnimeDetailed
){
    Button(
        onClick = { onPlayClick(animeData.id) },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (animeData.isAvailable) colors.accent else colors.accent.copy(
                alpha = 0.8f
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(45.dp),
        enabled = animeData.isAvailable
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (animeData.isAvailable) {
                Icon(
                    painterResource(
                        id = R.drawable.play_24
                    ),
                    contentDescription = null,
                    Modifier.size(15.dp)
                )
            }
            Text(
                text = if (animeData.isAvailable) "Смотреть" else "Скоро"
            )
        }
    }
}