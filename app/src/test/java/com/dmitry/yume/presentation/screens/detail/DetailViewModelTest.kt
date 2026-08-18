package com.dmitry.yume.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.models.Status
import com.dmitry.yume.domain.repository.AnimeDetailResult
import com.dmitry.yume.domain.repository.AnimeRepository
import com.dmitry.yume.domain.repository.CurrentProgressResult
import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.OperationResult
import com.dmitry.yume.domain.repository.ProgressResult
import com.dmitry.yume.domain.repository.StatusResult
import com.dmitry.yume.domain.usecase.GetAnimeByIdUseCase
import com.dmitry.yume.domain.usecase.GetStatusByIdUseCase
import com.dmitry.yume.domain.usecase.PutFavoriteUseCase
import com.dmitry.yume.domain.usecase.PutStatusUseCase
import com.dmitry.yume.presentation.navigation.Details
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favorite mutations run sequentially and use latest status`() = runTest(dispatcher) {
        val firstStarted = CompletableDeferred<Unit>()
        val finishFirst = CompletableDeferred<Unit>()
        val targets = mutableListOf<Boolean>()
        val meRepository = FakeMeRepository(
            putFavorite = { favorite ->
                targets += favorite
                if (targets.size == 1) {
                    firstStarted.complete(Unit)
                    finishFirst.await()
                }
                StatusResult.Success(status(favorite))
            }
        )
        val viewModel = viewModel(meRepository)
        advanceUntilIdle()

        viewModel.putFavorite()
        firstStarted.await()
        viewModel.putFavorite()
        runCurrent()

        assertEquals(listOf(true), targets)

        finishFirst.complete(Unit)
        advanceUntilIdle()

        assertEquals(listOf(true, false), targets)
        assertEquals(
            false,
            (viewModel.statusState.value as StatusViewState.Success).status.favorite
        )
    }

    @Test
    fun `favorite error is exposed through existing error state`() = runTest(dispatcher) {
        val viewModel = viewModel(
            FakeMeRepository(
                putFavorite = { StatusResult.Error("failed") }
            )
        )
        advanceUntilIdle()

        viewModel.putFavorite()
        advanceUntilIdle()

        assertTrue(viewModel.statusState.value is StatusViewState.Error)
        assertEquals(
            "failed",
            (viewModel.statusState.value as StatusViewState.Error).message
        )
    }

    private fun viewModel(meRepository: MeRepository) = DetailViewModel(
        getAnimeById = GetAnimeByIdUseCase(FakeAnimeRepository()),
        getStatusById = GetStatusByIdUseCase(meRepository),
        putStatus = PutStatusUseCase(meRepository),
        putFavorite = PutFavoriteUseCase(meRepository),
        savedStateHandle = SavedStateHandle(mapOf(Details.ANIME_ID to 1))
    )

    private fun status(favorite: Boolean) = Status(
        animeId = 1,
        status = null,
        favorite = favorite,
        score = null,
        review = null
    )

    private inner class FakeMeRepository(
        private val putFavorite: suspend (Boolean) -> StatusResult
    ) : MeRepository {
        override val libraryUpdates: Flow<Map<Int, Status>> = emptyFlow()
        override suspend fun putProgress(progress: Progress): OperationResult = error("not used")
        override suspend fun getProgress(): ProgressResult = error("not used")
        override suspend fun getProgressById(id: Int): CurrentProgressResult = error("not used")
        override suspend fun getStatus(id: Int): StatusResult = StatusResult.Success(status(false))
        override suspend fun putStatus(id: Int, status: String?): StatusResult = error("not used")
        override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult =
            putFavorite.invoke(favorite)
        override fun getAnimeListByStatus(status: String, q: String?): Flow<PagingData<Anime>> = emptyFlow()
        override fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>> = emptyFlow()
    }

    private class FakeAnimeRepository : AnimeRepository {
        override fun getAnime(): Flow<PagingData<Anime>> = emptyFlow()
        override fun searchAnime(q: String): Flow<PagingData<Anime>> = emptyFlow()
        override suspend fun getAnimeById(id: Int): AnimeDetailResult =
            AnimeDetailResult.Success(
                AnimeDetailed(id, "Title", null, null, 2026, 8.0,)
            )
    }
}
