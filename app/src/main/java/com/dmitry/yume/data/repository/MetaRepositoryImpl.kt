package com.dmitry.yume.data.repository

import com.dmitry.yume.data.api.MetaApi
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.data.toAppError
import com.dmitry.yume.domain.repository.GenresResult
import com.dmitry.yume.domain.repository.HomeResult
import com.dmitry.yume.domain.repository.MetaRepository
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

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return HomeResult.Error(e.toAppError())
        }
    }

    override suspend fun getGenres(): GenresResult {
        try {
            val result = api.getMeta()

            return GenresResult.Success(result.toDomain())

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return GenresResult.Error(e.toAppError())
        }
    }
}