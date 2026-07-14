package com.dmitry.test.animeapplication.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dmitry.test.animeapplication.data.api.AnimeApi
import com.dmitry.test.animeapplication.data.response.toDomain
import com.dmitry.test.animeapplication.domain.models.Anime
import com.dmitry.test.animeapplication.domain.repository.AnimeDetailResult
import com.dmitry.test.animeapplication.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class AnimeRepositoryImpl @Inject constructor(
    private val api: AnimeApi
) : AnimeRepository {
    override fun getAnime(): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource(api, null)
            }
        ).flow
    }

    override fun searchAnime(q: String): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource(api, q)
            }
        ).flow
    }

    override suspend fun getAnimeById(id: Int): AnimeDetailResult {
        try {
            val response = api.getAnimeById(id)
            return AnimeDetailResult.Success(response.toDomain())
        } catch (e: IOException){
            return AnimeDetailResult.Error(e.message)
        } catch (e: HttpException){
            return AnimeDetailResult.Error(e.message)
        }
    }
}