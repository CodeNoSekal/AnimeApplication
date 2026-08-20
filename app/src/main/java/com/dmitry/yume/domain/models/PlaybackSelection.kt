package com.dmitry.yume.domain.models

data class PlaybackSelection(
    val animeId: Int,
    val episodeNumber: Int,
    val sourceProvider: Provider,
    val voiceoverId: Int,
)
