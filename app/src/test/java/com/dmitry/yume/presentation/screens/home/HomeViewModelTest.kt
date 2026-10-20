package com.dmitry.yume.presentation.screens.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dmitry.yume.domain.models.*
import com.dmitry.yume.domain.repository.*
import com.dmitry.yume.domain.usecase.GetHomeUseCase
import com.dmitry.yume.domain.usecase.GetProgressUseCase
import com.dmitry.yume.domain.usecase.ClearAllProgressUseCase
import com.dmitry.yume.domain.usecase.ClearProgressUseCase
import com.dmitry.yume.domain.usecase.PutStatusUseCase
import com.dmitry.yume.domain.usecase.PutFavoriteUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val homeData = HomeData(
        updated = "2026-10-20",
        hero = listOf(Anime(1, "Home", null, null, 2026, null, favorite = false)),
        ranking = listOf(RankedAnime(2, "Ranked", null, null, 2026, null, favorite = false, rank = 1)),
    )
    private val progress = ProgressData(listOf(ProgressItemData(
        1, "Continue", null, 2, 1_000, 10_000, false, null, null
    )))
    private val repository = FakeRepository()
    private val models = mutableListOf<HomeViewModel>()

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() {
        models.forEach { it.viewModelScope.cancel() }
        Dispatchers.resetMain()
    }

    @Test fun `init loads home and progress`() = runTest(dispatcher) {
        val vm = viewModel()
        assertEquals(HomeViewState.Loading, vm.homeState.value)
        assertEquals(ProgressViewState.Loading, vm.progressState.value)

        runCurrent()

        assertEquals(homeData, (vm.homeState.value as HomeViewState.Success).homeData)
        assertEquals(progress, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals(1, repository.homeCalls)
        assertEquals(1, repository.progressCalls)
    }

    @Test fun `slow progress never blocks home and repeated loads do not restart requests`() = runTest(dispatcher) {
        val response = CompletableDeferred<ProgressResult>()
        repository.progressResponse = { response.await() }
        val vm = viewModel()
        vm.load()
        runCurrent()
        assertTrue(vm.homeState.value is HomeViewState.Success)
        assertEquals(ProgressViewState.Loading, vm.progressState.value)
        assertEquals(1, repository.progressCalls)

        vm.load()
        runCurrent()
        // Home already finished, so it is requested again; progress is still in flight.
        assertEquals(2, repository.homeCalls)
        assertEquals(1, repository.progressCalls)

        response.complete(ProgressResult.Success(progress))
        runCurrent()
        assertTrue(vm.progressState.value is ProgressViewState.Success)
    }

    @Test fun `refresh keeps loaded content visible and applies new result`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        val previous = vm.homeState.value
        val response = CompletableDeferred<HomeResult>()
        repository.homeResponse = { response.await() }

        vm.load()
        runCurrent()
        assertSame(previous, vm.homeState.value)

        val updated = homeData.copy(updated = "2026-10-21")
        response.complete(HomeResult.Success(updated))
        runCurrent()
        assertEquals(updated, (vm.homeState.value as HomeViewState.Success).homeData)
    }

    @Test fun `background errors keep both successful sections`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        val previousHome = vm.homeState.value
        val previousProgress = vm.progressState.value
        repository.homeResponse = { HomeResult.Error(AppError.NoConnection) }
        repository.progressResponse = { ProgressResult.Error("offline") }

        vm.load()
        runCurrent()

        assertSame(previousHome, vm.homeState.value)
        assertSame(previousProgress, vm.progressState.value)
    }

    @Test fun `initial failure shows error and retry loads home`() = runTest(dispatcher) {
        repository.homeResponse = { HomeResult.Error(AppError.NoConnection) }
        repository.progressResponse = { ProgressResult.Error("offline") }
        val vm = viewModel()
        runCurrent()
        assertEquals(HomeViewState.Error(AppError.NoConnection), vm.homeState.value)
        assertEquals(ProgressViewState.Error("offline"), vm.progressState.value)

        repository.homeResponse = { HomeResult.Success(homeData) }
        repository.progressResponse = { ProgressResult.Success(progress) }
        vm.load()
        runCurrent()
        assertTrue(vm.homeState.value is HomeViewState.Success)
        assertTrue(vm.progressState.value is ProgressViewState.Success)
    }

    @Test fun `update progress refreshes only progress`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        val updated = progress.copy(items = progress.items.map { it.copy(positionMs = 9_000) })
        repository.progressResponse = { ProgressResult.Success(updated) }

        vm.updateProgress()
        runCurrent()

        assertEquals(updated, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals(1, repository.homeCalls)
        assertEquals(2, repository.progressCalls)
    }

    @Test fun `ranking follows personal library updates`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()

        repository.libraryUpdates.value = mapOf(2 to Status(2, "смотрю", true, 8, null))
        runCurrent()
        val ranked = (vm.homeState.value as HomeViewState.Success).homeData.ranking.single()
        assertEquals("смотрю", ranked.myStatus)
        assertTrue(ranked.favorite)
        assertEquals(8, ranked.myScore)

        vm.load()
        runCurrent()
        assertEquals("смотрю", (vm.homeState.value as HomeViewState.Success).homeData.ranking.single().myStatus)

        repository.libraryUpdates.value = mapOf(2 to Status(2, null, true, 8, null))
        runCurrent()
        assertNull((vm.homeState.value as HomeViewState.Success).homeData.ranking.single().myStatus)
    }

    @Test fun `clear progress removes card after server confirms`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        val response = CompletableDeferred<OperationResult>()
        repository.clearProgressResponse = { response.await() }

        vm.clearProgress(1)
        runCurrent()

        assertEquals(progress, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals(1, repository.clearProgressCalls)
        assertEquals(1, vm.progressActionState.value.removingAnimeId)
        assertTrue(vm.progressActionState.value.isBusy)

        response.complete(OperationResult.Success)
        runCurrent()
        assertTrue((vm.progressState.value as ProgressViewState.Success).progress.items.isEmpty())
        assertFalse(vm.progressActionState.value.isBusy)
    }

    @Test fun `failed clear progress keeps card and shows error`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        repository.clearProgressResponse = { OperationResult.Error("Нет соединения") }

        vm.clearProgress(1)
        runCurrent()

        assertEquals(progress, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals("Нет соединения", vm.progressActionState.value.error)
        assertFalse(vm.progressActionState.value.isBusy)

        vm.dismissProgressActionError()
        assertNull(vm.progressActionState.value.error)
    }

    @Test fun `clear progress ignores unknown anime`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()

        vm.clearProgress(42)
        runCurrent()

        assertEquals(0, repository.clearProgressCalls)
    }

    @Test fun `clear all progress hides section after server confirms`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        val response = CompletableDeferred<OperationResult>()
        repository.clearAllProgressResponse = { response.await() }

        vm.clearAllProgress()
        runCurrent()

        assertEquals(progress, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals(1, repository.clearAllProgressCalls)
        assertTrue(vm.progressActionState.value.isClearingAll)

        response.complete(OperationResult.Success)
        runCurrent()
        assertTrue((vm.progressState.value as ProgressViewState.Success).progress.items.isEmpty())
        assertFalse(vm.progressActionState.value.isBusy)
    }

    @Test fun `failed clear all progress keeps section and shows error`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        repository.clearAllProgressResponse = { OperationResult.Error("Нет соединения") }

        vm.clearAllProgress()
        runCurrent()

        assertEquals(progress, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals("Нет соединения", vm.progressActionState.value.error)
        assertFalse(vm.progressActionState.value.isBusy)
    }

    private fun viewModel() = HomeViewModel(
        GetProgressUseCase(repository), GetHomeUseCase(repository),
        PutStatusUseCase(repository), PutFavoriteUseCase(repository), ObserveLibraryUpdatesUseCase(repository),
        ClearProgressUseCase(repository), ClearAllProgressUseCase(repository)
    ).also(models::add)

    private inner class FakeRepository : MetaRepository, MeRepository {
        var homeCalls = 0
        var progressCalls = 0
        var homeResponse: suspend () -> HomeResult = { HomeResult.Success(homeData) }
        var progressResponse: suspend () -> ProgressResult = { ProgressResult.Success(progress) }
        override suspend fun getHome(): HomeResult { homeCalls++; return homeResponse() }
        override suspend fun getProgress(): ProgressResult { progressCalls++; return progressResponse() }
        override suspend fun getGenres(): GenresResult = error("not used")
        override val libraryUpdates = MutableStateFlow(emptyMap<Int, Status>())
        var clearProgressCalls = 0
        var clearProgressResponse: suspend (Int) -> OperationResult = { OperationResult.Success }
        var clearAllProgressCalls = 0
        var clearAllProgressResponse: suspend () -> OperationResult = { OperationResult.Success }
        override suspend fun putProgress(progress: Progress): OperationResult = error("not used")
        override suspend fun clearProgress(id: Int): OperationResult {
            clearProgressCalls++
            return clearProgressResponse(id)
        }
        override suspend fun clearAllProgress(): OperationResult {
            clearAllProgressCalls++
            return clearAllProgressResponse()
        }
        override suspend fun getProgressById(id: Int): CurrentProgressResult = error("not used")
        override suspend fun getStatus(id: Int): StatusResult = error("not used")
        override suspend fun putStatus(id: Int, status: String?): StatusResult = error("not used")
        override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult = error("not used")
        override suspend fun putScore(id: Int, score: Int?): StatusResult = error("not used")
        override fun getAnimeListByStatus(status: String, q: String?): Flow<PagingData<Anime>> = error("not used")
        override fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>> = error("not used")
    }
}
