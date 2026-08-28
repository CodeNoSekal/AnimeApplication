package com.dmitry.yume.presentation.screens.catalog

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.models.SearchOptions
import com.dmitry.yume.domain.models.Status as LibraryStatus
import com.dmitry.yume.domain.repository.*
import com.dmitry.yume.domain.usecase.GetAnimeCatalogUseCase
import com.dmitry.yume.domain.usecase.GetMetaUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val viewModels = mutableListOf<CatalogViewModel>()

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }

    @After fun tearDown() {
        viewModels.forEach { it.viewModelScope.cancel() }
        Dispatchers.resetMain()
    }

    @Test fun `re-entry keeps all loaded pages even when network is unavailable`() = runTest(dispatcher) {
        val repository = FakeAnimeRepository()
        val vm = viewModel(repository)
        val session = vm.catalog.value
        val first = presenter()
        val collection = backgroundScope.launch { session.anime.collectLatest(first::collectFrom) }
        runCurrent()
        // Access pages in sequence, like scrolling through the real list.
        for (index in listOf(10, 49, 70, 99, 149, 199, 200)) {
            first[index]
            runCurrent()
        }
        assertEquals(200, first.peek(200)?.id)
        val cachedItems = first.snapshot().items
        val requests = repository.loads.toList()
        collection.cancel()
        runCurrent()
        repository.offline = true

        val returned = presenter()
        backgroundScope.launch { vm.catalog.value.anime.collectLatest(returned::collectFrom) }
        runCurrent()

        assertSame(session, vm.catalog.value)
        assertEquals(cachedItems, returned.snapshot().items)
        assertEquals(requests, repository.loads)
        assertEquals(1, repository.queries.size)
    }

    @Test fun `library updates reuse pages and change personal fields only`() = runTest(dispatcher) {
        val repository = FakeAnimeRepository()
        val library = FakeMeRepository()
        val vm = viewModel(repository, library)
        val items = presenter()
        backgroundScope.launch { vm.catalog.value.anime.collectLatest(items::collectFrom) }
        runCurrent()
        val requests = repository.loads.toList()

        for (status in listOf("watching", "completed", null)) {
            library.libraryUpdates.value = mapOf(10 to LibraryStatus(10, status, true, 9, null))
            runCurrent()
            val updated = items.peek(10)!!
            assertEquals(status, updated.myStatus)
            assertEquals("released", updated.status)
            assertEquals(9, updated.myScore)
            assertTrue(updated.favorite)
        }
        assertEquals(requests, repository.loads)
        assertEquals(0, vm.catalog.value.generation)
    }

    @Test fun `library changes while away are present on return without refetch`() = runTest(dispatcher) {
        val repository = FakeAnimeRepository()
        val library = FakeMeRepository()
        val vm = viewModel(repository, library)
        val first = presenter()
        val collection = backgroundScope.launch { vm.catalog.value.anime.collectLatest(first::collectFrom) }
        runCurrent()
        collection.cancel()
        runCurrent()
        val requests = repository.loads.toList()
        library.libraryUpdates.value = mapOf(10 to LibraryStatus(10, "planned", true, null, null))
        runCurrent()

        val returned = presenter()
        backgroundScope.launch { vm.catalog.value.anime.collectLatest(returned::collectFrom) }
        runCurrent()
        assertEquals("planned", returned.peek(10)?.myStatus)
        assertEquals(requests, repository.loads)
    }

    @Test fun `only changed applied options create a fresh session`() = runTest(dispatcher) {
        val repository = FakeAnimeRepository()
        val vm = viewModel(repository)
        val initial = vm.catalog.value
        vm.applyFilters()
        vm.setSorting(SortingOptions())
        vm.assignGenre(7)
        assertSame(initial, vm.catalog.value)

        vm.applyFilters()
        val filtered = vm.catalog.value
        assertEquals(1, filtered.generation)
        assertEquals(setOf(7), filtered.options.filters.genres)
        vm.applyFilters()
        assertSame(filtered, vm.catalog.value)

        vm.setSorting(SortingOptions(Sort.Title, Order.Asc))
        assertEquals(2, vm.catalog.value.generation)
        assertEquals(setOf(7), vm.catalog.value.options.filters.genres)
        vm.dropFilters()
        assertEquals(3, vm.catalog.value.generation)
        assertEquals(Sort.Title, vm.catalog.value.options.sorting.sort)
        assertTrue(vm.catalog.value.options.filters.genres.isEmpty())
        assertEquals(4, repository.queries.size)
    }

    @Test fun `new query starts at first page and does not reuse previous results`() = runTest(dispatcher) {
        val repository = FakeAnimeRepository()
        val vm = viewModel(repository)
        val first = presenter()
        backgroundScope.launch { vm.catalog.value.anime.collectLatest(first::collectFrom) }
        runCurrent()
        first[49]
        runCurrent()
        assertTrue(first.size > 50)

        vm.setSorting(SortingOptions(Sort.Title, Order.Asc))
        val next = presenter()
        backgroundScope.launch { vm.catalog.value.anime.collectLatest(next::collectFrom) }
        runCurrent()
        assertEquals(50, next.size)
        assertEquals(299, next.peek(0)?.id)
        assertEquals(0, repository.loads.last())
    }

    private fun presenter() = object : PagingDataPresenter<Anime>(dispatcher) {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<Anime>) = Unit
    }

    private fun viewModel(
        repository: FakeAnimeRepository,
        library: FakeMeRepository = FakeMeRepository()
    ) = CatalogViewModel(
        GetAnimeCatalogUseCase(repository),
        GetMetaUseCase(object : MetaRepository {
            override suspend fun getHome(): HomeResult = error("not used")
            override suspend fun getGenres(): GenresResult = GenresResult.Error("not needed")
        }),
        ObserveLibraryUpdatesUseCase(library)
    ).also(viewModels::add)

    private class FakeAnimeRepository : AnimeRepository {
        val queries = mutableListOf<SearchOptions>()
        val loads = mutableListOf<Int>()
        var offline = false
        override fun getAnime(options: SearchOptions): Flow<PagingData<Anime>> {
            queries += options
            return Pager(PagingConfig(pageSize = 50, initialLoadSize = 50, prefetchDistance = 5)) {
                object : PagingSource<Int, Anime>() {
                    override fun getRefreshKey(state: PagingState<Int, Anime>): Int? = null
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
                        val start = params.key ?: 0
                        loads += start
                        if (offline) return LoadResult.Error(IOException("offline"))
                        val end = (start + 50).coerceAtMost(300)
                        return LoadResult.Page(
                            (start until end).map { index ->
                                val id = if (options.sort == "title") 299 - index else index
                                Anime(id, "Anime $id", null, null, 2026, null, "released", false)
                            },
                            if (start == 0) null else start - 50,
                            if (end == 300) null else end
                        )
                    }
                }
            }.flow
        }
        override fun searchAnime(q: String, options: SearchOptions): Flow<PagingData<Anime>> = error("not used")
        override suspend fun getAnimeById(id: Int): AnimeDetailResult = error("not used")
    }

    private class FakeMeRepository : MeRepository {
        override val libraryUpdates = MutableStateFlow<Map<Int, LibraryStatus>>(emptyMap())
        override suspend fun putProgress(progress: Progress): OperationResult = error("not used")
        override suspend fun getProgress(): ProgressResult = error("not used")
        override suspend fun getProgressById(id: Int): CurrentProgressResult = error("not used")
        override suspend fun getStatus(id: Int): StatusResult = error("not used")
        override suspend fun putStatus(id: Int, status: String?): StatusResult = error("not used")
        override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult = error("not used")
        override suspend fun putScore(id: Int, score: Int?): StatusResult = error("not used")
        override fun getAnimeListByStatus(status: String, q: String?): Flow<PagingData<Anime>> = error("not used")
        override fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>> = error("not used")
    }
}
