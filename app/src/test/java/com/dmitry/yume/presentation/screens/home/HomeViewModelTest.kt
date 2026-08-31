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
    private val home = Home(Anime(1, "Home", null, null, 2026, null, favorite = false), emptyList())
    private val progress = ProgressData(listOf(ProgressItemData(
        1, "Continue", null, 2, 1_000, 10_000, false, null, null
    )))
    private val repository = FakeRepository()
    private val models = mutableListOf<HomeViewModel>()
    private var now = 0L

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() {
        models.forEach { it.viewModelScope.cancel() }
        Dispatchers.resetMain()
    }

    @Test fun `fast return reuses home and quietly refreshes progress`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        val previousHome = vm.homeState.value
        val previousProgress = vm.progressState.value
        now = HomeViewModel.HOME_CACHE_TTL_MS - 1
        val waitProgress = CompletableDeferred<ProgressResult>()
        repository.progressResponse = { waitProgress.await() }
        vm.load()
        runCurrent()
        assertEquals(1, repository.homeCalls)
        assertEquals(2, repository.progressCalls)
        assertSame(previousHome, vm.homeState.value)
        assertSame(previousProgress, vm.progressState.value)
        waitProgress.complete(ProgressResult.Success(progress))
        runCurrent()
        assertSame(previousProgress, vm.progressState.value)
    }

    @Test fun `expired home stays visible while refreshing and unchanged result is not emitted again`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        val previous = vm.homeState.value
        val response = CompletableDeferred<HomeResult>()
        repository.homeResponse = { response.await() }
        now = HomeViewModel.HOME_CACHE_TTL_MS
        vm.load()
        runCurrent()
        assertEquals(2, repository.homeCalls)
        assertSame(previous, vm.homeState.value)
        response.complete(HomeResult.Success(home.copy()))
        runCurrent()
        assertSame(previous, vm.homeState.value)
        vm.load()
        runCurrent()
        assertEquals(2, repository.homeCalls)
    }

    @Test fun `background errors keep both successful sections`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        val previousHome = vm.homeState.value
        val previousProgress = vm.progressState.value
        repository.homeResponse = { HomeResult.Error("offline") }
        repository.progressResponse = { ProgressResult.Error("offline") }
        now = HomeViewModel.HOME_CACHE_TTL_MS
        vm.load()
        runCurrent()
        assertSame(previousHome, vm.homeState.value)
        assertSame(previousProgress, vm.progressState.value)
        // A failed request must not make stale data fresh.
        repository.homeResponse = { HomeResult.Success(home.copy(hero = home.hero.copy(title = "Updated"))) }
        vm.load()
        runCurrent()
        assertEquals("Updated", (vm.homeState.value as HomeViewState.Success).home.hero.title)
    }

    @Test fun `slow progress never blocks home and repeated loads do not restart requests`() = runTest(dispatcher) {
        val response = CompletableDeferred<ProgressResult>()
        repository.progressResponse = { response.await() }
        val vm = viewModel()
        vm.load()
        vm.load()
        runCurrent()
        assertTrue(vm.homeState.value is HomeViewState.Success)
        assertEquals(ProgressViewState.Loading, vm.progressState.value)
        vm.load()
        runCurrent()
        assertEquals(1, repository.homeCalls)
        assertEquals(1, repository.progressCalls)
        response.complete(ProgressResult.Success(progress))
        runCurrent()
        assertTrue(vm.progressState.value is ProgressViewState.Success)
    }

    @Test fun `initial failure can retry and force bypasses freshness`() = runTest(dispatcher) {
        val vm = viewModel()
        repository.homeResponse = { HomeResult.Error("offline") }
        vm.load()
        runCurrent()
        assertTrue(vm.homeState.value is HomeViewState.Error)
        repository.homeResponse = { HomeResult.Success(home) }
        vm.load(force = true)
        runCurrent()
        assertTrue(vm.homeState.value is HomeViewState.Success)
        vm.load(force = true)
        runCurrent()
        assertEquals(3, repository.homeCalls)
    }

    @Test fun `changed progress updates in place without refreshing home`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        val updated = progress.copy(items = progress.items.map { it.copy(positionMs = 9_000) })
        repository.progressResponse = { ProgressResult.Success(updated) }
        vm.load()
        runCurrent()
        assertEquals(updated, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals(1, repository.homeCalls)
        // A successful empty response is a real change, not a refresh error.
        repository.progressResponse = { ProgressResult.Success(ProgressData(emptyList())) }
        vm.load()
        runCurrent()
        assertTrue((vm.progressState.value as ProgressViewState.Success).progress.items.isEmpty())
    }

    @Test fun `clear progress removes card before server responds`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        val response = CompletableDeferred<OperationResult>()
        repository.clearProgressResponse = { response.await() }

        vm.clearProgress(1)

        assertTrue((vm.progressState.value as ProgressViewState.Success).progress.items.isEmpty())
        runCurrent()
        assertEquals(1, repository.clearProgressCalls)
        assertTrue(vm.progressActionState.value.isBusy)

        response.complete(OperationResult.Success)
        runCurrent()
        assertFalse(vm.progressActionState.value.isBusy)
    }

    @Test fun `failed clear progress restores removed card`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        repository.clearProgressResponse = { OperationResult.Error("Нет соединения") }

        vm.clearProgress(1)
        assertTrue((vm.progressState.value as ProgressViewState.Success).progress.items.isEmpty())
        runCurrent()

        assertEquals(progress, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals("Нет соединения", vm.progressActionState.value.error)
        assertFalse(vm.progressActionState.value.isBusy)
    }

    @Test fun `clear all progress hides section before server responds`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        val response = CompletableDeferred<OperationResult>()
        repository.clearAllProgressResponse = { response.await() }

        vm.clearAllProgress()

        assertTrue((vm.progressState.value as ProgressViewState.Success).progress.items.isEmpty())
        runCurrent()
        assertEquals(1, repository.clearAllProgressCalls)
        assertTrue(vm.progressActionState.value.isBusy)

        response.complete(OperationResult.Success)
        runCurrent()
        assertFalse(vm.progressActionState.value.isBusy)
    }

    @Test fun `failed clear all progress restores section`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        repository.clearAllProgressResponse = { OperationResult.Error("Нет соединения") }

        vm.clearAllProgress()
        assertTrue((vm.progressState.value as ProgressViewState.Success).progress.items.isEmpty())
        runCurrent()

        assertEquals(progress, (vm.progressState.value as ProgressViewState.Success).progress)
        assertEquals("Нет соединения", vm.progressActionState.value.error)
        assertFalse(vm.progressActionState.value.isBusy)
    }

    @Test fun `hero follows personal updates even when cached home is refreshed`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        repository.libraryUpdates.value = mapOf(1 to Status(1, "смотрю", true, 8, null))
        runCurrent()
        val hero = (vm.homeState.value as HomeViewState.Success).home.hero
        assertEquals("смотрю", hero.myStatus)
        assertTrue(hero.favorite)
        assertEquals(8, hero.myScore)

        vm.load(force = true)
        runCurrent()
        assertEquals("смотрю", (vm.homeState.value as HomeViewState.Success).home.hero.myStatus)
        repository.libraryUpdates.value = mapOf(1 to Status(1, null, true, 8, null))
        runCurrent()
        assertNull((vm.homeState.value as HomeViewState.Success).home.hero.myStatus)
    }

    @Test fun `save hero status blocks duplicates and closes picker after success`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        vm.openHeroList()
        val response = CompletableDeferred<StatusResult>()
        repository.statusResponse = { _, _ -> response.await() }
        vm.setHeroStatus("в планах")
        vm.setHeroStatus("смотрю")
        vm.dismissHeroList()
        runCurrent()
        assertTrue(vm.heroListState.value.isSaving)
        assertEquals(1, repository.statusCalls)
        response.complete(StatusResult.Success(Status(1, "в планах", false, null, null)))
        runCurrent()
        assertNull(vm.heroListState.value.animeId)
        assertEquals("в планах", (vm.homeState.value as HomeViewState.Success).home.hero.myStatus)
    }

    @Test fun `failed save keeps old status and allows retry including removal`() = runTest(dispatcher) {
        val vm = viewModel()
        repository.libraryUpdates.value = mapOf(1 to Status(1, "в планах", false, null, null))
        vm.load()
        runCurrent()
        vm.openHeroList()
        repository.statusResponse = { _, _ -> StatusResult.Error("Нет соединения") }
        vm.setHeroStatus(null)
        runCurrent()
        assertEquals("Нет соединения", vm.heroListState.value.error)
        assertFalse(vm.heroListState.value.isSaving)
        assertEquals(1, vm.heroListState.value.animeId)
        assertEquals("в планах", (vm.homeState.value as HomeViewState.Success).home.hero.myStatus)
        repository.statusResponse = { id, status -> StatusResult.Success(Status(id, status, false, null, null)) }
        vm.setHeroStatus(null)
        runCurrent()
        assertNull((vm.homeState.value as HomeViewState.Success).home.hero.myStatus)
        assertNull(vm.heroListState.value.animeId)
    }

    @Test fun `favorite toggles independently and blocks overlapping personal mutations`() = runTest(dispatcher) {
        val vm = viewModel()
        repository.libraryUpdates.value = mapOf(1 to Status(1, "в планах", false, 8, null))
        vm.load()
        runCurrent()
        vm.openHeroList()
        val response = CompletableDeferred<StatusResult>()
        repository.favoriteResponse = { _, _ -> response.await() }
        vm.toggleHeroFavorite()
        vm.toggleHeroFavorite()
        vm.setHeroStatus(null)
        runCurrent()
        assertTrue(vm.heroFavoriteState.value.isSaving)
        assertEquals(1, repository.favoriteCalls)
        assertEquals(0, repository.statusCalls)
        response.complete(StatusResult.Success(Status(1, "в планах", true, 8, null)))
        runCurrent()
        val hero = (vm.homeState.value as HomeViewState.Success).home.hero
        assertTrue(hero.favorite)
        assertEquals("в планах", hero.myStatus)
        assertEquals(8, hero.myScore)
        assertFalse(vm.heroFavoriteState.value.isSaving)
        repository.favoriteResponse = { id, favorite ->
            StatusResult.Success(repository.libraryUpdates.value.getValue(id).copy(favorite = favorite))
        }
        vm.toggleHeroFavorite()
        runCurrent()
        assertFalse((vm.homeState.value as HomeViewState.Success).home.hero.favorite)
        assertEquals("в планах", (vm.homeState.value as HomeViewState.Success).home.hero.myStatus)
    }

    @Test fun `favorite error preserves state and can retry after dismissal`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.load()
        runCurrent()
        repository.favoriteResponse = { _, _ -> StatusResult.Error("Нет соединения") }
        vm.toggleHeroFavorite()
        runCurrent()
        assertEquals("Нет соединения", vm.heroFavoriteState.value.error)
        assertFalse(vm.heroFavoriteState.value.isSaving)
        assertFalse((vm.homeState.value as HomeViewState.Success).home.hero.favorite)
        vm.dismissFavoriteError()
        assertNull(vm.heroFavoriteState.value.error)
        repository.favoriteResponse = { id, favorite -> StatusResult.Success(Status(id, null, favorite, null, null)) }
        vm.toggleHeroFavorite()
        runCurrent()
        assertTrue((vm.homeState.value as HomeViewState.Success).home.hero.favorite)
    }

    private fun viewModel() = HomeViewModel(
        GetProgressUseCase(repository), GetHomeUseCase(repository),
        PutStatusUseCase(repository), PutFavoriteUseCase(repository), ObserveLibraryUpdatesUseCase(repository), { now },
        ClearProgressUseCase(repository), ClearAllProgressUseCase(repository)
    ).also(models::add)

    private inner class FakeRepository : MetaRepository, MeRepository {
        var homeCalls = 0
        var progressCalls = 0
        var homeResponse: suspend () -> HomeResult = { HomeResult.Success(home) }
        var progressResponse: suspend () -> ProgressResult = { ProgressResult.Success(progress) }
        override suspend fun getHome(): HomeResult { homeCalls++; return homeResponse() }
        override suspend fun getProgress(): ProgressResult { progressCalls++; return progressResponse() }
        override suspend fun getGenres(): GenresResult = error("not used")
        override val libraryUpdates = MutableStateFlow(emptyMap<Int, Status>())
        var statusCalls = 0
        var favoriteCalls = 0
        var clearProgressCalls = 0
        var clearProgressResponse: suspend (Int) -> OperationResult = { OperationResult.Success }
        var clearAllProgressCalls = 0
        var clearAllProgressResponse: suspend () -> OperationResult = { OperationResult.Success }
        var favoriteResponse: suspend (Int, Boolean) -> StatusResult = { id, favorite ->
            StatusResult.Success(Status(id, null, favorite, null, null))
        }
        var statusResponse: suspend (Int, String?) -> StatusResult = { id, status ->
            StatusResult.Success(Status(id, status, false, null, null))
        }
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
        override suspend fun putStatus(id: Int, status: String?): StatusResult {
            statusCalls++
            return statusResponse(id, status).also { result ->
                if (result is StatusResult.Success) {
                    libraryUpdates.value += id to result.status
                }
            }
        }
        override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult {
            favoriteCalls++
            return favoriteResponse(id, favorite).also { result ->
                if (result is StatusResult.Success) libraryUpdates.value += id to result.status
            }
        }
        override suspend fun putScore(id: Int, score: Int?): StatusResult = error("not used")
        override fun getAnimeListByStatus(status: String, q: String?): Flow<PagingData<Anime>> = error("not used")
        override fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>> = error("not used")
    }
}
