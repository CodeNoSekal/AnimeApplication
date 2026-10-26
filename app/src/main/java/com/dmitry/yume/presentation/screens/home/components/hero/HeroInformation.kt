package com.dmitry.yume.presentation.screens.home.components.hero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType
import com.google.android.material.button.MaterialButtonGroup

@Composable
fun HeroInformation(
    data: Anime
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        listOfNotNull(
            ReleaseStatus.entries.firstOrNull{ it.raw == data.status }?.title,
            data.year
        ).joinToString(" · ")
            .takeIf { it.isNotEmpty() }?.let {
                Text(
                    text = it,
                    style = YumeType.bodyMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp),
                    color = colors.accent
                )
        }

        Text(
            text = data.title.orEmpty(),
            style = YumeType.h1,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}