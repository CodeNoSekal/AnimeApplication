package com.dmitry.yume.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dmitry.yume.domain.models.*
import com.dmitry.yume.domain.repository.*
import com.dmitry.yume.domain.usecase.*
import com.dmitry.yume.presentation.navigation.Details
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val models = mutableListOf<DetailViewModel>()
    private val repository = FakeMeRepository()

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() {
        models.forEach { it.viewModelScope.cancel() }
        Dispatchers.resetMain()
    }

    @Test fun `duplicate favorite taps are ignored while saving and latest state is used afterwards`() = runTest(dispatcher) {
        repository.personal = repository.personal.copy(favorite = false)
        val vm = viewModel()
        runCurrent()
        val response = CompletableDeferred<StatusResult>()
        val targets = mutableListOf<Boolean>()
        repository.favoriteResponse = { value -> targets += value; response.await() }
        vm.putFavorite()
        vm.putFavorite()
        runCurrent()
        assertEquals(listOf(true), targets)
        response.complete(StatusResult.Success(repository.personal.copy(favorite = true)))
        runCurrent()
        repository.favoriteResponse = { value ->
            targets += value
            StatusResult.Success(repository.personal.copy(favorite = value))
        }
        vm.putFavorite()
        runCurrent()
        assertEquals(listOf(true, false), targets)
        assertFalse((vm.statusState.value as StatusViewState.Success).status.favorite)
    }

    @Test fun `favorite error preserves personal data and exposes retryable error`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        repository.favoriteResponse = { StatusResult.Error("failed") }
        vm.putFavorite()
        runCurrent()
        assertTrue(vm.statusState.value is StatusViewState.Success)
        assertEquals("failed", vm.actionState.value.error)
        assertFalse(vm.actionState.value.isSaving)
    }

    @Test fun `score saves independently and current title plus related copy update`() = runTest(dispatcher) {
        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect {} }
        runCurrent()
        vm.openScoreEditor()
        vm.saveScore(9)
        runCurrent()
        val personal = (vm.statusState.value as StatusViewState.Success).status
        assertEquals(9, personal.score)
        assertEquals("в планах", personal.status)
        assertTrue(personal.favorite)
        assertFalse(vm.scoreEditor.value.isOpen)
        val anime = (vm.state.value as DetailViewState.Success).animeDetailed
        assertEquals(9, anime.score)
        assertEquals(9, anime.relations.single().myScore)
    }

    @Test fun `failed score keeps editor open and allows removal retry`() = runTest(dispatcher) {
        repository.personal = repository.personal.copy(score = 8)
        val vm = viewModel()
        runCurrent()
        vm.openScoreEditor()
        repository.scoreResponse = { StatusResult.Error("offline") }
        vm.saveScore(null)
        runCurrent()
        assertTrue(vm.scoreEditor.value.isOpen)
        assertEquals(8, (vm.statusState.value as StatusViewState.Success).status.score)
        assertEquals("offline", vm.actionState.value.error)
        repository.scoreResponse = { StatusResult.Success(repository.personal.copy(score = it)) }
        vm.saveScore(null)
        runCurrent()
        assertNull((vm.statusState.value as StatusViewState.Success).status.score)
        assertFalse(vm.scoreEditor.value.isOpen)
    }

    @Test fun `pending score blocks other mutations and closing the editor`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        vm.openScoreEditor()
        val response = CompletableDeferred<StatusResult>()
        repository.scoreResponse = { response.await() }
        vm.saveScore(7)
        vm.saveScore(8)
        vm.putFavorite()
        vm.putStatus(null)
        vm.dismissScoreEditor()
        runCurrent()
        assertEquals(1, repository.scoreCalls)
        assertEquals(0, repository.favoriteCalls)
        assertEquals(0, repository.statusCalls)
        assertTrue(vm.scoreEditor.value.isOpen)
        response.complete(StatusResult.Success(repository.personal.copy(score = 7)))
        runCurrent()
    }

    @Test fun `invalid scores never reach repository and cancelling editor does not save`() = runTest(dispatcher) {
        val vm = viewModel()
        runCurrent()
        vm.openScoreEditor()
        for (value in listOf(0, 11)) {
            vm.saveScore(value)
            runCurrent()
            assertNotNull(vm.actionState.value.error)
        }
        assertEquals(0, repository.scoreCalls)
        vm.dismissScoreEditor()
        assertFalse(vm.scoreEditor.value.isOpen)
        assertNull(vm.actionState.value.error)
    }

    @Test fun `external personal update refreshes open details including removal`() = runTest(dispatcher) {
        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect {} }
        runCurrent()
        repository.libraryUpdates.value = mapOf(1 to repository.personal.copy(score = 10, status = null))
        runCurrent()
        assertEquals(10, (vm.statusState.value as StatusViewState.Success).status.score)
        val anime = (vm.state.value as DetailViewState.Success).animeDetailed
        assertNull(anime.status)
        assertEquals(10, anime.score)
    }

    private fun viewModel() = DetailViewModel(
        getAnimeById = GetAnimeByIdUseCase(FakeAnimeRepository()),
        getStatusById = GetStatusByIdUseCase(repository),
        putStatus = PutStatusUseCase(repository),
        putFavorite = PutFavoriteUseCase(repository),
        putScore = PutScoreUseCase(repository),
        observeLibraryUpdates = ObserveLibraryUpdatesUseCase(repository),
        savedStateHandle = SavedStateHandle(mapOf(Details.ANIME_ID to 1))
    ).also(models::add)

    private class FakeMeRepository : MeRepository {
        var personal = Status(1, "в планах", true, null, null)
        var scoreCalls = 0
        var favoriteCalls = 0
        var statusCalls = 0
        var scoreResponse: suspend (Int?) -> StatusResult = { StatusResult.Success(personal.copy(score = it)) }
        var favoriteResponse: suspend (Boolean) -> StatusResult = { StatusResult.Success(personal.copy(favorite = it)) }
        override val libraryUpdates = MutableStateFlow(emptyMap<Int, Status>())
        private fun publish(result: StatusResult): StatusResult {
            if (result is StatusResult.Success) {
                personal = result.status
                libraryUpdates.value = mapOf(1 to personal)
            }
            return result
        }
        override suspend fun getStatus(id: Int) = publish(StatusResult.Success(personal))
        override suspend fun putScore(id: Int, score: Int?): StatusResult {
            scoreCalls++
            return publish(scoreResponse(score))
        }
        override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult {
            favoriteCalls++
            return publish(favoriteResponse(favorite))
        }
        override suspend fun putStatus(id: Int, status: String?): StatusResult {
            statusCalls++
            return publish(StatusResult.Success(personal.copy(status = status)))
        }
        override suspend fun putProgress(progress: Progress): OperationResult = error("not used")
        override suspend fun getProgress(): ProgressResult = error("not used")
        override suspend fun getProgressById(id: Int): CurrentProgressResult = error("not used")
        override fun getAnimeListByStatus(status: String, q: String?): Flow<PagingData<Anime>> = emptyFlow()
        override fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>> = emptyFlow()
    }

    private class FakeAnimeRepository : AnimeRepository {
        override fun getAnime(options: SearchOptions): Flow<PagingData<Anime>> = emptyFlow()
        override fun searchAnime(q: String, options: SearchOptions): Flow<PagingData<Anime>> = emptyFlow()
        override suspend fun getAnimeById(id: Int): AnimeDetailResult = AnimeDetailResult.Success(
            AnimeDetailed(
                id, "Title", null, null, 2026, 8.0,
                status = "в планах", isAvailable = false, description = null, duration = null,
                genres = null, studios = null,
                relations = listOf(Anime(id, "Title", null, null, 2026, 8.0, favorite = true))
            )
        )
    }
}
