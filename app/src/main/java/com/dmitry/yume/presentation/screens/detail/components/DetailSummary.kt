package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.presentation.screens.catalog.AnimeKind
import com.dmitry.yume.presentation.screens.catalog.Status
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun DetailSummary(anime: AnimeDetailed) {
    val release = Status.entries.firstOrNull { it.raw == anime.releaseStatus }?.title
    val age = anime.ageRating?.takeIf { it.isNotBlank() }?.let {
        when (it.lowercase()) {
            "pg_13" -> "PG-13"
            "r" -> "R-17"
            "r_plus" -> "R+"
            "rx" -> "Rx"
            else -> it.uppercase()
        }
    }
    val meta = listOfNotNull(
        anime.year?.toString(),
        AnimeKind.entries.firstOrNull { it.raw == anime.kind }?.title,
        release,
        age
    ).joinToString(" · ")
    if (meta.isNotBlank()) Text(meta, style = YumeType.sm, color = colors.textSecondary)
}
