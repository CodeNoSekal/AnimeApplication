package com.dmitry.yume.presentation.screens.home.components.hero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Anime

@Composable
fun HeroContent(
    modifier: Modifier = Modifier,
    pageData: Anime,
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
        ,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeroPoster(
            modifier = Modifier,
            poster = pageData.posterUrl
        )

        HeroInformation(
            data = pageData,
        )
    }
}