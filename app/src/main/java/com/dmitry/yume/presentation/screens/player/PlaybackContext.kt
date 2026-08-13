package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.models.Provider

data class PlaybackContext(
    val animeId: Int,
    val episodeNumber: Int,
    val sourceProvider: Provider,
    val voiceoverId: Int?
)

fun PlaybackContext.toProgress(
    positionMs: Long,
    durationMs: Long
): Progress =
    Progress(
        animeId = animeId,
        episodeNumber = episodeNumber,
        positionMs = positionMs,
        durationMs = durationMs,
        sourceProvider = sourceProvider.toRaw(),
        voiceoverId = voiceoverId
    )
