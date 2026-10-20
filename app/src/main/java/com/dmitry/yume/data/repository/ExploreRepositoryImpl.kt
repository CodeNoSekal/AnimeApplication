package com.dmitry.yume.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dmitry.yume.data.api.ExploreApi
import com.dmitry.yume.data.request.OptionsRequest
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.data.toAppError
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.repository.ExploreRepository
import com.dmitry.yume.domain.repository.ExploreResult
import com.dmitry.yume.domain.repository.HomeResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExploreRepositoryImpl @Inject constructor(
    private val api: ExploreApi
) : ExploreRepository {

    override suspend fun getExplore(): ExploreResult {
        try {
            val result = api.getExplore()

            return ExploreResult.Success(result.toDomain())

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("ExploreRepository", e.message?: "Всё ещё хз что за ошибка")
            return ExploreResult.Error(e.toAppError())
        }
    }

    override fun getCollection(
        endpoint: String
    ): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource(
                    loadPage = { page ->
                        api.getCollection(
                            endpoint = endpoint,
                            page = page,
                        )
                    }
                )
            }
        ).flow
    }
}