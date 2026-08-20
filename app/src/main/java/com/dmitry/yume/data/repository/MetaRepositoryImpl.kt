package com.dmitry.yume.data.repository

import com.dmitry.yume.data.api.MetaApi
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.domain.repository.GenresResult
import com.dmitry.yume.domain.repository.HomeResult
import com.dmitry.yume.domain.repository.MetaRepository
import com.dmitry.yume.domain.repository.ProgressResult
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import javax.inject.Inject

class MetaRepositoryImpl @Inject constructor(
    private val api: MetaApi
) : MetaRepository {

    override suspend fun getHome(): HomeResult {
        try {
            val result = api.getHome()

            return HomeResult.Success(result.toDomain())

        } catch (e: HttpException) {
            return HomeResult.Error(e.safeMessage("Не удалось загрузить домашний экран"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return HomeResult.Error(e.safeMessage("Не удалось загрузить домашний экран"))
        }
    }

    override suspend fun getGenres(): GenresResult {
        try {
            val result = api.getGenres()

            return GenresResult.Success(result.toDomain())

        } catch (e: HttpException) {
            return GenresResult.Error(e.safeMessage("Не удалось загрузить жанры"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return GenresResult.Error(e.safeMessage("Не удалось загрузить жанры"))
        }
    }

    private fun Exception.safeMessage(fallback: String): String =
        when (this) {
            is HttpException -> "$fallback (HTTP ${code()})"
            else -> message?.takeIf { it.isNotBlank() } ?: fallback
        }
}