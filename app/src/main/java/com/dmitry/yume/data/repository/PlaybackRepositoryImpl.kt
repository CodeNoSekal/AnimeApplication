package com.dmitry.yume.data.repository

import com.dmitry.yume.data.api.PlaybackApi
import com.dmitry.yume.data.request.ResolvePlaybackRequest
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.domain.models.PlaybackSelection
import com.dmitry.yume.domain.repository.PlaybackRepository
import com.dmitry.yume.domain.repository.PlaybackCatalogResult
import com.dmitry.yume.domain.repository.ResolvePlaybackResult
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class PlaybackRepositoryImpl @Inject constructor(
    private val api: PlaybackApi
) : PlaybackRepository {
    override suspend fun getPlaybackCatalog(animeId: Int): PlaybackCatalogResult {
        try {
            val response = api.getPlaybackCatalog(animeId)
            return PlaybackCatalogResult.Success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception){
            return PlaybackCatalogResult.Error(e.message ?: "Не удалось загрузить данные плеера")
        }
    }

    override suspend fun resolvePlayback(
        selection: PlaybackSelection
    ): ResolvePlaybackResult {
        try {
            val request = ResolvePlaybackRequest(
                animeId = selection.animeId,
                episodeNumber = selection.episodeNumber,
                sourceProvider = selection.sourceProvider.toRaw(),
                voiceoverId = selection.voiceoverId
            )

            val response = api.resolvePlayback(request)
            return ResolvePlaybackResult.Success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception){
            return ResolvePlaybackResult.Error(e.message ?: "Не удалось загрузить плеер")
        }
    }
}
