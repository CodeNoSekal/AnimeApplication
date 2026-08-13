package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.PlayerData

data class EpisodeNavigation(
    val previousEpisodeId: Int?,
    val nextEpisodeId: Int?
)

fun PlayerData.navigationFor(
    selectedEpisodeNumber: Int
): EpisodeNavigation {
    val index = episodes.indexOfFirst {
        it.id == selectedEpisodeNumber
    }

    if (index == -1) {
        return EpisodeNavigation(
            previousEpisodeId = null,
            nextEpisodeId = null
        )
    }

    return EpisodeNavigation(
        previousEpisodeId = episodes.getOrNull(index - 1)?.id,
        nextEpisodeId = episodes.getOrNull(index + 1)?.id
    )
}
