package com.dmitry.test.animeapplication.domain.repository

import com.dmitry.test.animeapplication.domain.models.Progress
import com.dmitry.test.animeapplication.domain.models.ProgressData
import com.dmitry.test.animeapplication.domain.models.ProgressItemData
import com.dmitry.test.animeapplication.domain.models.Status
import kotlinx.coroutines.flow.Flow

interface MeRepository {
    val libraryUpdates: Flow<Map<Int, Status>>
    suspend fun putProgress(progress: Progress)
    suspend fun getProgress(): ProgressResult
    suspend fun getProgressById(id: Int): CurrentProgressResult
    suspend fun getStatus(id: Int): StatusResult
    suspend fun putStatus(id: Int, status: String?): StatusResult
    suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult
}

sealed interface ProgressResult {
    data class Success(
        val progress: ProgressData
    ) : ProgressResult
    data class Error(
        val message: String?
    ) : ProgressResult
}

sealed interface CurrentProgressResult {
    data class Success(
        val progress: ProgressItemData
    ) : CurrentProgressResult
    data class Error(
        val message: String?
    ) : CurrentProgressResult
}

sealed interface StatusResult {
    data class Success(
        val status: Status
    ) : StatusResult
    data class Error(
        val message: String?
    ) : StatusResult
}