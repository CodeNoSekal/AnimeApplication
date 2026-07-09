package com.dmitry.test.animeapplication.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dmitry.test.animeapplication.data.AnimeApi
import com.dmitry.test.animeapplication.data.response.toDomain
import com.dmitry.test.animeapplication.domain.models.Anime
import retrofit2.HttpException
import java.io.IOException

class AnimePagingSource (
    private val api: AnimeApi,
    private val q: String?
) : PagingSource<Int, Anime>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
        return try {
            val currentPage = params.key ?: 1

            val response = api.getAnimeList(
                page = currentPage,
                q = q
            )

            val anime = response.items.map { animeData ->
                animeData.toDomain()
            }

            LoadResult.Page(
                data = anime,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage >= response.meta.totalPages) null else currentPage + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val page = state.closestPageToPosition(anchorPosition)

            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }

}