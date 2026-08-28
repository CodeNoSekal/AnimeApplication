package com.dmitry.yume.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dmitry.yume.data.api.AnimeApi
import com.dmitry.yume.data.request.OptionsRequest
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.SearchOptions
import com.dmitry.yume.domain.repository.AnimeDetailResult
import com.dmitry.yume.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CancellationException
import retrofit2.http.Query
import javax.inject.Inject

class AnimeRepositoryImpl @Inject constructor(
    private val api: AnimeApi
) : AnimeRepository {
    override fun getAnime(
        options: SearchOptions
    ): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource(
                    loadPage = { page ->
                        api.getAnimeList(
                            page = page,
                            options = OptionsRequest.from(options)
                        )
                    }
                )
            }
        ).flow
    }

    override fun searchAnime(
        q: String,
        options: SearchOptions
    ): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource(loadPage = { page ->
                    api.getAnimeList(
                        page = page,
                        q = q,
                        options = OptionsRequest.from(options)
                    )
                })
            }
        ).flow
    }

    override suspend fun getAnimeById(id: Int): AnimeDetailResult {
        try {
            val response = api.getAnimeById(id)
            return AnimeDetailResult.Success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception){
            return AnimeDetailResult.Error(e.message ?: "Не удалось загрузить данные аниме")
        }
    }
}
