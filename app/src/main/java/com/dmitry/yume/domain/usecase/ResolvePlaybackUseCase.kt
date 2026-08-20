package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.models.PlaybackSelection
import com.dmitry.yume.domain.repository.PlaybackRepository
import javax.inject.Inject

class ResolvePlaybackUseCase @Inject constructor(
    private val repository: PlaybackRepository
) {
    suspend operator fun invoke(
        selection: PlaybackSelection
    ) = repository.resolvePlayback(selection)
}
