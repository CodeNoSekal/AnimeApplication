package com.dmitry.yume.domain.repository

import androidx.paging.PagingData
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.models.ProgressData
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.domain.models.Status
import com.dmitry.yume.domain.models.ProfileListCount
import com.dmitry.yume.domain.models.ProfileStatistics
import kotlinx.coroutines.flow.Flow

interface MeRepository {
    val libraryUpdates: Flow<Map<Int, Status>>
    suspend fun putProgress(progress: Progress): OperationResult
    suspend fun getProgress(): ProgressResult
    suspend fun getProgressById(id: Int): CurrentProgressResult
    suspend fun getProfileLists(): ProfileListsResult =
        ProfileListsResult.Error("Статистика профиля не поддерживается")
    suspend fun getProfileStatistics(): ProfileStatisticsResult =
        ProfileStatisticsResult.Error("Статистика профиля не поддерживается")
    suspend fun clearProgress(id: Int): OperationResult =
        OperationResult.Error("Очистка прогресса не поддерживается")
    suspend fun clearAllProgress(): OperationResult =
        OperationResult.Error("Очистка прогресса не поддерживается")
    suspend fun getStatus(id: Int): StatusResult
    suspend fun putStatus(id: Int, status: String?): StatusResult
    suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult
    suspend fun putScore(id: Int, score: Int?): StatusResult

    fun getAnimeListByStatus(status: String, q: String?): Flow<PagingData<Anime>>
    fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>>
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

sealed interface ProfileListsResult {
    data class Success(val lists: List<ProfileListCount>) : ProfileListsResult
    data class Error(val message: String?) : ProfileListsResult
}

sealed interface ProfileStatisticsResult {
    data class Success(val statistics: ProfileStatistics) : ProfileStatisticsResult
    data class Error(val message: String?) : ProfileStatisticsResult
}
