package com.dmitry.yume.data.repository

import androidx.paging.PagingSource
import com.dmitry.yume.data.api.AnimeApi
import com.dmitry.yume.data.api.MeApi
import com.dmitry.yume.data.request.FavoriteRequest
import com.dmitry.yume.data.request.ProgressRequest
import com.dmitry.yume.data.request.ReviewRequest
import com.dmitry.yume.data.request.ScoreRequest
import com.dmitry.yume.data.request.OptionsRequest
import com.dmitry.yume.domain.repository.StatusResult
import com.dmitry.yume.data.request.StatusRequest
import com.dmitry.yume.data.response.AnimeDetailResponse
import com.dmitry.yume.data.response.AnimeResponse
import com.dmitry.yume.data.response.ProgressItem
import com.dmitry.yume.data.response.ProgressResponse
import com.dmitry.yume.data.response.StatusResponse
import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.repository.AnimeDetailResult
import com.dmitry.yume.domain.repository.OperationResult
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class RepositoryFailureTest {

    @Test
    fun `paging source creates keys for first page`() = runTest {
        val source = AnimePagingSource {
            AnimeResponse(
                meta = com.dmitry.yume.data.response.Meta(1, 50, 100, 2),
                items = emptyList()
            )
        }

        val result = source.load(refresh()) as PagingSource.LoadResult.Page

        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `paging source stops at last page`() = runTest {
        val source = AnimePagingSource {
            AnimeResponse(
                meta = com.dmitry.yume.data.response.Meta(2, 50, 100, 2),
                items = emptyList()
            )
        }

        val result = source.load(refresh(key = 2)) as PagingSource.LoadResult.Page

        assertEquals(1, result.prevKey)
        assertNull(result.nextKey)
    }

    @Test
    fun `paging source exposes network error`() = runTest {
        val source = AnimePagingSource { throw IOException("offline") }

        assertTrue(source.load(refresh()) is PagingSource.LoadResult.Error)
    }

    @Test(expected = CancellationException::class)
    fun `paging source rethrows cancellation`() = runTest {
        val source = AnimePagingSource { throw CancellationException("cancelled") }

        source.load(refresh())
    }

    @Test
    fun `anime repository converts schema error to result`() = runTest {
        val repository = AnimeRepositoryImpl(object : AnimeApi {
            override suspend fun getAnimeList(
                page: Int,
                perPage: Int,
                q: String?,
                options: OptionsRequest
            ): AnimeResponse = error("not used")

            override suspend fun getAnimeById(id: Int): AnimeDetailResponse {
                throw JsonDataException("missing required field")
            }
        })

        assertTrue(repository.getAnimeById(1) is AnimeDetailResult.Error)
    }

    @Test(expected = CancellationException::class)
    fun `anime repository rethrows cancellation`() = runTest {
        val repository = AnimeRepositoryImpl(object : AnimeApi {
            override suspend fun getAnimeList(
                page: Int,
                perPage: Int,
                q: String?,
                options: OptionsRequest
            ): AnimeResponse = error("not used")

            override suspend fun getAnimeById(id: Int): AnimeDetailResponse {
                throw CancellationException("cancelled")
            }
        })

        repository.getAnimeById(1)
    }

    @Test
    fun `paging source exposes schema error`() = runTest {
        val source = AnimePagingSource { throw JsonDataException("bad json") }

        val result = source.load(refresh())

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `put progress returns error instead of swallowing it`() = runTest {
        val repository = MeRepositoryImpl(FakeMeApi { throw JsonDataException("bad response") })

        val result = repository.putProgress(progress())

        assertTrue(result is OperationResult.Error)
    }

    @Test(expected = CancellationException::class)
    fun `put progress rethrows cancellation`() = runTest {
        val repository = MeRepositoryImpl(FakeMeApi { throw CancellationException("cancelled") })

        repository.putProgress(progress())
    }

    @Test fun `score success publishes complete personal state and deletion clears it`() = runTest {
        val api = FakeMeApi {}
        api.scoreResponse = { id, score -> StatusResponse(id, "в планах", true, score.score, "review") }
        val repository = MeRepositoryImpl(api)
        assertTrue(repository.putScore(1, 9) is StatusResult.Success)
        val personal = repository.libraryUpdates.first().getValue(1)
        assertEquals(9, personal.score)
        assertEquals("в планах", personal.status)
        assertEquals(true, personal.favorite)
        assertEquals("review", personal.review)
        repository.putScore(1, null)
        assertNull(repository.libraryUpdates.first().getValue(1).score)
        api.scoreResponse = { _, _ -> throw IOException("offline") }
        assertTrue(repository.putScore(1, 8) is StatusResult.Error)
        assertNull(repository.libraryUpdates.first().getValue(1).score)
    }

    @Test(expected = CancellationException::class)
    fun `score save rethrows cancellation`() = runTest {
        val api = FakeMeApi {}
        api.scoreResponse = { _, _ -> throw CancellationException("cancelled") }
        MeRepositoryImpl(api).putScore(1, 8)
    }

    private fun progress() = Progress(
        animeId = 1,
        episodeNumber = 1,
        positionMs = 100,
        durationMs = 1_000,
        sourceProvider = null,
        voiceoverId = null
    )

    private fun refresh(key: Int? = null) = PagingSource.LoadParams.Refresh(
        key = key,
        loadSize = 20,
        placeholdersEnabled = false
    )

    private class FakeMeApi(
        private val putProgressBlock: suspend () -> Unit
    ) : MeApi {
        var scoreResponse: suspend (Int, ScoreRequest) -> StatusResponse = { _, _ -> error("not used") }
        override suspend fun putProgress(progressRequest: ProgressRequest) = putProgressBlock()
        override suspend fun getProgress(): ProgressResponse = error("not used")
        override suspend fun getProgressById(id: Int): ProgressItem = error("not used")
        override suspend fun getStatus(id: Int): StatusResponse = error("not used")
        override suspend fun putStatus(id: Int, statusRequest: StatusRequest): StatusResponse = error("not used")
        override suspend fun putFavorite(id: Int, favoriteRequest: FavoriteRequest): StatusResponse = error("not used")
        override suspend fun putScore(id: Int, scoreRequest: ScoreRequest): StatusResponse = scoreResponse(id, scoreRequest)
        override suspend fun putReview(id: Int, reviewRequest: ReviewRequest): StatusResponse = error("not used")
        override suspend fun getAnimeListByStatus(page: Int, perPage: Int, status: String, q: String?): AnimeResponse = error("not used")
        override suspend fun getAnimeListByFavourite(page: Int, perPage: Int, q: String?): AnimeResponse = error("not used")
    }
}
