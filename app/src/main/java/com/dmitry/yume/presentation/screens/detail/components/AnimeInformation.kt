package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.presentation.screens.detail.detailEpisodeLabel
import com.dmitry.yume.presentation.screens.detail.formatAnimeDate
import com.dmitry.yume.presentation.screens.detail.formatNextEpisode
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun AnimeInformation(anime: AnimeDetailed) {
    val episodes = detailEpisodeLabel(anime)
    val duration = anime.duration?.takeIf { it > 0 }?.let { "$it мин" }
    val studios = anime.studios.orEmpty().filter { it.isNotBlank() }
    val premiere = formatAnimeDate(anime.airedOn, anime.airedOnPrecision)
    val next = if (anime.kind != "movie" && anime.releaseStatus == "ongoing") formatNextEpisode(anime.nextEpisodeAt) else null

    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        DetailSummary(anime)
        listOfNotNull(episodes, duration).joinToString(" · ").takeIf { it.isNotEmpty() }?.let {
            InformationLine(if (episodes != null) "Серии" else "Длительность", it)
        }
        if (studios.isNotEmpty()) InformationLine(if (studios.size == 1) "Студия" else "Студии", studios.joinToString(", "))
        premiere?.let { InformationLine("Премьера", it) }
        next?.let { InformationLine("Следующая серия", it) }
    }
}

@Composable
private fun InformationLine(label: String, value: String) {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(color = colors.textMuted)) { append("$label: ") }
            append(value)
        },
        style = YumeType.sm,
        color = colors.textSecondary
    )
}
