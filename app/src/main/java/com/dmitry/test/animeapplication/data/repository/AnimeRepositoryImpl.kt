package com.dmitry.test.animeapplication.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dmitry.test.animeapplication.data.TestApi
import com.dmitry.test.animeapplication.domain.Anime
import com.dmitry.test.animeapplication.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnimeRepositoryImpl @Inject constructor(
    private val api: TestApi
) : AnimeRepository {
    override fun getAnime(): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource(api)
            }
        ).flow
    }
}