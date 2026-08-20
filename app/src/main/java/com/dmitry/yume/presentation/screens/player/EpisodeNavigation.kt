package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.PlaybackCatalog

data class EpisodeNavigation(
    val previousEpisodeId: Int?,
    val nextEpisodeId: Int?
)

fun PlaybackCatalog.navigationFor(
    selectedEpisodeNumber: Int
): EpisodeNavigation {
    val index = episodes.indexOfFirst {
        it.number == selectedEpisodeNumber
    }

    if (index == -1) {
        return EpisodeNavigation(
            previousEpisodeId = null,
            nextEpisodeId = null
        )
    }

    return EpisodeNavigation(
        previousEpisodeId = episodes.getOrNull(index - 1)?.number,
        nextEpisodeId = episodes.getOrNull(index + 1)?.number
    )
}
