package com.dmitry.yume.domain.repository

import com.dmitry.yume.domain.models.ResolvedPlayback
import com.dmitry.yume.domain.models.PlaybackCatalog
import com.dmitry.yume.domain.models.PlaybackSelection

interface PlaybackRepository {
    suspend fun getPlaybackCatalog(animeId: Int): PlaybackCatalogResult
    suspend fun resolvePlayback(
        selection: PlaybackSelection
    ): ResolvePlaybackResult
}
sealed interface PlaybackCatalogResult {
    data class Success(
        val playbackCatalog: PlaybackCatalog
    ) : PlaybackCatalogResult
    data class Error(
        val message: String?
    ) : PlaybackCatalogResult
}

sealed interface ResolvePlaybackResult {
    data class Success(
        val resolvedPlayback: ResolvedPlayback
    ) : ResolvePlaybackResult
    data class Error(
        val message: String?
    ) : ResolvePlaybackResult
}
