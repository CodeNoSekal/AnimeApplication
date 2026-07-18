package com.dmitry.test.animeapplication.data.repository

import android.util.Log
import com.dmitry.test.animeapplication.data.api.MeApi
import com.dmitry.test.animeapplication.data.request.FavoriteRequest
import com.dmitry.test.animeapplication.data.request.ProgressRequest
import com.dmitry.test.animeapplication.data.request.StatusRequest
import com.dmitry.test.animeapplication.data.response.toDomain
import com.dmitry.test.animeapplication.domain.models.Progress
import com.dmitry.test.animeapplication.domain.models.Status
import com.dmitry.test.animeapplication.domain.repository.CurrentProgressResult
import com.dmitry.test.animeapplication.domain.repository.MeRepository
import com.dmitry.test.animeapplication.domain.repository.ProgressResult
import com.dmitry.test.animeapplication.domain.repository.StatusResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import javax.inject.Inject

class MeRepositoryImpl @Inject constructor(
    private val meApi: MeApi,
) : MeRepository {

    private val _libraryUpdates = MutableStateFlow<Map<Int, Status>>(emptyMap())
    override val libraryUpdates = _libraryUpdates.asStateFlow()

    override suspend fun putProgress(progress: Progress) {
        try {
            meApi.putProgress(ProgressRequest.from(progress))

        } catch (e: HttpException) {
            logHttpError("putProgress", e)
        } catch (e: Exception) {
            Log.e(TAG, "putProgress failed", e)
        }
    }

    override suspend fun getProgress(): ProgressResult {
        try {
            val result = meApi.getProgress()

            return ProgressResult.Success(result.toDomain())

        } catch (e: HttpException) {
            logHttpError("getProgress", e)
            return ProgressResult.Error(e.message)
        } catch (e: Exception) {
            Log.e(TAG, "getProgress failed", e)
            return ProgressResult.Error(e.message)
        }
    }

    override suspend fun getProgressById(id: Int): CurrentProgressResult {
        try {
            val result = meApi.getProgressById(id)

            return CurrentProgressResult.Success(result.toDomain())

        } catch (e: HttpException) {
            logHttpError("getProgressById id=$id", e)
            return CurrentProgressResult.Error(e.message)
        } catch (e: Exception) {
            Log.e(TAG, "getProgressById failed, id=$id", e)
            return CurrentProgressResult.Error(e.message)
        }
    }

    override suspend fun getStatus(id: Int): StatusResult {
        try {
            val result = meApi.getStatus(id)

            val updated = result.toDomain()

            _libraryUpdates.update { it + (updated.animeId to updated) }

            return StatusResult.Success(updated)

        } catch (e: HttpException) {
            logHttpError("getStatus id=$id", e)
            return StatusResult.Error(e.message)
        } catch (e: Exception) {
            Log.e(TAG, "getStatus failed, id=$id", e)
            return StatusResult.Error(e.message)
        }
    }

    override suspend fun putStatus(id: Int, status: String?): StatusResult {
        try {
            val result = meApi.putStatus(id, StatusRequest(status))

            val updated = result.toDomain()

            _libraryUpdates.update { it + (updated.animeId to updated) }

            return StatusResult.Success(updated)

        } catch (e: HttpException) {
            logHttpError("putStatus id=$id status=$status", e)
            return StatusResult.Error(e.message)
        } catch (e: Exception) {
            Log.e(TAG, "putStatus failed, id=$id status=$status", e)
            return StatusResult.Error(e.message)
        }
    }

    override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult {
        try {
            val result = meApi.putFavorite(id, FavoriteRequest(favorite))

            val updated = result.toDomain()

            _libraryUpdates.update { it + (updated.animeId to updated) }

            return StatusResult.Success(updated)

        } catch (e: HttpException) {
            logHttpError("putFavorite id=$id favorite=$favorite", e)
            return StatusResult.Error(e.message)
        } catch (e: Exception) {
            Log.e(TAG, "putFavorite failed, id=$id favorite=$favorite", e)
            return StatusResult.Error(e.message)
        }
    }

    private fun logHttpError(action: String, e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e(TAG, "$action failed: HTTP ${e.code()} ${e.message()} body=$errorBody", e)
    }

    private companion object {
        const val TAG = "MeRepository"
    }
}
