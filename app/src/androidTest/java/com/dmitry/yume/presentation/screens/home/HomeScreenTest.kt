package com.dmitry.yume.presentation.screens.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dmitry.yume.domain.models.*
import com.dmitry.yume.domain.repository.*
import com.dmitry.yume.domain.usecase.ClearAllProgressUseCase
import com.dmitry.yume.domain.usecase.ClearProgressUseCase
import com.dmitry.yume.domain.usecase.GetHomeUseCase
import com.dmitry.yume.domain.usecase.GetProgressUseCase
import com.dmitry.yume.domain.usecase.PutStatusUseCase
import com.dmitry.yume.domain.usecase.PutFavoriteUseCase
import com.dmitry.yume.domain.usecase.ObserveLibraryUpdatesUseCase
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule val compose = createComposeRule()
    private val homeData = HomeData(
        updated = "2026-10-20",
        hero = listOf(Anime(1, "Home hero", null, null, 2026, null, favorite = false)),
        ranking = emptyList(),
    )
    private val progress = ProgressData((0 until 6).map {
        ProgressItemData(it, "Continue $it", null, 2, 1_000, 10_000, false, null, null)
    })
    private val repository = FakeRepository()
    private var vm: HomeViewModel? = null

    @After fun tearDown() { vm?.viewModelScope?.cancel() }

    // HomeViewModel starts loading in init, so it is created only after the responses are set up.
    private fun setScreen(onItemClick: (Int) -> Unit = {}) {
        val model = HomeViewModel(
            GetProgressUseCase(repository), GetHomeUseCase(repository),
            PutStatusUseCase(repository), PutFavoriteUseCase(repository), ObserveLibraryUpdatesUseCase(repository),
            ClearProgressUseCase(repository), ClearAllProgressUseCase(repository)
        )
        vm = model
        compose.setContent { YumeTheme { HomeScreen(onItemClick, {}, model, {}) } }
    }

    @Test fun skeletonBecomesHeroWhileProgressKeepsItsOwnPlaceholder() {
        val pendingHome = CompletableDeferred<HomeResult>()
        val pendingProgress = CompletableDeferred<ProgressResult>()
        repository.homeResponse = { pendingHome.await() }
        repository.progressResponse = { pendingProgress.await() }
        setScreen()
        compose.onNodeWithContentDescription("Загрузка домашнего экрана").assertIsDisplayed()

        compose.runOnIdle { pendingHome.complete(HomeResult.Success(homeData)) }
        compose.onNodeWithText("Home hero").assertIsDisplayed()
        compose.onNodeWithContentDescription("Загрузка домашнего экрана").assertDoesNotExist()
        compose.onNodeWithContentDescription("Загрузка продолжения просмотра").assertIsDisplayed()

        compose.runOnIdle { pendingProgress.complete(ProgressResult.Success(ProgressData(emptyList()))) }
        compose.onNodeWithContentDescription("Загрузка продолжения просмотра").assertDoesNotExist()
        compose.onNodeWithText("Продолжить просмотр").assertDoesNotExist()
    }

    @Test fun backgroundFailureKeepsLoadedContent() {
        setScreen()
        compose.onNodeWithText("Home hero").assertIsDisplayed()
        compose.onNodeWithText("Continue 0").assertIsDisplayed()

        compose.runOnIdle {
            repository.homeResponse = { HomeResult.Error(AppError.NoConnection) }
            repository.progressResponse = { ProgressResult.Error("offline") }
            vm?.load()
        }

        compose.onNodeWithText("Home hero").assertIsDisplayed()
        compose.onNodeWithText("Continue 0").assertIsDisplayed()
        compose.onNodeWithText("Нет подключения").assertDoesNotExist()
    }

    @Test fun firstLoadFailureOffersRetryInsteadOfBlankScreen() {
        repository.homeResponse = { HomeResult.Error(AppError.Unknown) }
        setScreen()
        compose.onNodeWithText("Не удалось загрузить домашний экран").assertIsDisplayed()

        compose.runOnIdle { repository.homeResponse = { HomeResult.Success(homeData) } }
        compose.onNodeWithText("Попробовать ещё раз").performClick()
        compose.onNodeWithText("Home hero").assertIsDisplayed()
    }

    @Test fun heroClickOpensDetails() {
        var openedId: Int? = null
        setScreen(onItemClick = { openedId = it })

        compose.onNodeWithText("Home hero").performClick()

        compose.runOnIdle { assertEquals(1, openedId) }
    }

    private inner class FakeRepository : MetaRepository, MeRepository {
        var homeResponse: suspend () -> HomeResult = { HomeResult.Success(homeData) }
        var progressResponse: suspend () -> ProgressResult = { ProgressResult.Success(progress) }
        override suspend fun getHome(): HomeResult = homeResponse()
        override suspend fun getProgress(): ProgressResult = progressResponse()
        override suspend fun getGenres(): GenresResult = error("not used")
        override val libraryUpdates = MutableStateFlow(emptyMap<Int, Status>())
        override suspend fun putProgress(progress: Progress): OperationResult = error("not used")
        override suspend fun getProgressById(id: Int): CurrentProgressResult = error("not used")
        override suspend fun getStatus(id: Int): StatusResult = error("not used")
        override suspend fun putStatus(id: Int, status: String?): StatusResult = error("not used")
        override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult = error("not used")
        override suspend fun putScore(id: Int, score: Int?): StatusResult = error("not used")
        override fun getAnimeListByStatus(status: String, q: String?): Flow<PagingData<Anime>> = error("not used")
        override fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>> = error("not used")
    }
}
