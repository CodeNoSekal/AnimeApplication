package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.PlaybackRepository
import com.dmitry.yume.domain.repository.PlaybackCatalogResult
import javax.inject.Inject

class GetPlaybackCatalogUseCase @Inject constructor(
    private val repository: PlaybackRepository
){
    suspend operator fun invoke(animeId: Int): PlaybackCatalogResult {
        return repository.getPlaybackCatalog(animeId)
    }
}
