package com.dmitry.yume.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dmitry.yume.data.api.MeApi
import com.dmitry.yume.data.request.FavoriteRequest
import com.dmitry.yume.data.request.ProgressRequest
import com.dmitry.yume.data.request.StatusRequest
import com.dmitry.yume.data.request.ScoreRequest
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.models.Status
import com.dmitry.yume.domain.repository.CurrentProgressResult
import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.OperationResult
import com.dmitry.yume.domain.repository.ProgressResult
import com.dmitry.yume.domain.repository.StatusResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject

class MeRepositoryImpl @Inject constructor(
    private val meApi: MeApi,
) : MeRepository {

    private val _libraryUpdates = MutableStateFlow<Map<Int, Status>>(emptyMap())
    override val libraryUpdates = _libraryUpdates.asStateFlow()
    private val librarySources = CopyOnWriteArraySet<AnimePagingSource>()

    override suspend fun putProgress(progress: Progress): OperationResult =
        try {
            meApi.putProgress(ProgressRequest.from(progress))
            OperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            OperationResult.Error(e.safeMessage("Не удалось сохранить прогресс"))
        }

    override suspend fun getProgress(): ProgressResult {
        try {
            val result = meApi.getProgress()

            return ProgressResult.Success(result.toDomain())

        } catch (e: HttpException) {
            return ProgressResult.Error(e.safeMessage("Не удалось загрузить прогресс"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ProgressResult.Error(e.safeMessage("Не удалось загрузить прогресс"))
        }
    }

    override suspend fun getProgressById(id: Int): CurrentProgressResult {
        try {
            val result = meApi.getProgressById(id)

            return CurrentProgressResult.Success(result.toDomain())

        } catch (e: HttpException) {
            return CurrentProgressResult.Error(e.safeMessage("Не удалось загрузить прогресс"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return CurrentProgressResult.Error(e.safeMessage("Не удалось загрузить прогресс"))
        }
    }

    override suspend fun getStatus(id: Int): StatusResult {
        try {
            val result = meApi.getStatus(id)

            val updated = result.toDomain()

            _libraryUpdates.update { it + (updated.animeId to updated) }

            return StatusResult.Success(updated)

        } catch (e: HttpException) {
            return StatusResult.Error(e.safeMessage("Не удалось загрузить статус"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return StatusResult.Error(e.safeMessage("Не удалось загрузить статус"))
        }
    }

    override suspend fun putStatus(id: Int, status: String?): StatusResult {
        try {
            val result = meApi.putStatus(id, StatusRequest(status))

            val updated = result.toDomain()

            _libraryUpdates.update { it + (updated.animeId to updated) }
            invalidateLibrarySources()

            return StatusResult.Success(updated)

        } catch (e: HttpException) {
            return StatusResult.Error(e.safeMessage("Не удалось изменить статус"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return StatusResult.Error(e.safeMessage("Не удалось изменить статус"))
        }
    }

    override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult {
        try {
            val result = meApi.putFavorite(id, FavoriteRequest(favorite))

            val updated = result.toDomain()

            _libraryUpdates.update { it + (updated.animeId to updated) }
            invalidateLibrarySources()

            return StatusResult.Success(updated)

        } catch (e: HttpException) {
            return StatusResult.Error(e.safeMessage("Не удалось изменить избранное"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return StatusResult.Error(e.safeMessage("Не удалось изменить избранное"))
        }
    }

    override suspend fun putScore(id: Int, score: Int?): StatusResult {
        return try {
            val updated = meApi.putScore(id, ScoreRequest(score)).toDomain()
            _libraryUpdates.update { it + (updated.animeId to updated) }
            invalidateLibrarySources()
            StatusResult.Success(updated)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            StatusResult.Error(e.safeMessage("Не удалось сохранить оценку"))
        }
    }

    override fun getAnimeListByStatus(
        status: String,
        q: String?
    ): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                track(AnimePagingSource(loadPage = { page ->
                    meApi.getAnimeListByStatus(
                        page = page,
                        status = status,
                        q = q
                    )
                }))
            }
        ).flow
    }

    override fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                track(AnimePagingSource(loadPage = { page ->
                    meApi.getAnimeListByFavourite(
                        page = page,
                        q = q
                    )
                }))
            }
        ).flow
    }

    private fun track(source: AnimePagingSource): AnimePagingSource {
        librarySources += source
        source.registerInvalidatedCallback { librarySources -= source }
        return source
    }

    private fun invalidateLibrarySources() {
        librarySources.forEach { it.invalidate() }
    }

    private fun Exception.safeMessage(fallback: String): String =
        when (this) {
            is HttpException -> "$fallback (HTTP ${code()})"
            else -> message?.takeIf { it.isNotBlank() } ?: fallback
        }
}
