package com.dmitry.yume.domain.repository

import androidx.paging.PagingData
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.ExploreModel
import com.dmitry.yume.domain.models.HomeData
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {
    suspend fun getExplore(): ExploreResult

    fun getCollection(endpoint: String): Flow<PagingData<Anime>>
}

sealed interface ExploreResult{
    data class Success(
        val exploreData: ExploreModel
    ) : ExploreResult
    data class Error(
        val error: AppError
    ) : ExploreResult
}

