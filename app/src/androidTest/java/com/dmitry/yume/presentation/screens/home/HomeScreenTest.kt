package com.dmitry.yume.presentation.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.semantics.SemanticsActions
import com.dmitry.yume.presentation.screens.home.components.Hero
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dmitry.yume.domain.models.*
import com.dmitry.yume.domain.repository.*
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
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule val compose = createComposeRule()
    private val home = Home(Anime(1, "Home hero", null, null, 2026, null, favorite = false), emptyList())
    private val progress = ProgressData((0 until 6).map {
        ProgressItemData(it, "Continue $it", null, 2, 1_000, 10_000, false, null, null)
    })
    private val repository = FakeRepository()
    private var now = 0L
    private val vm = HomeViewModel(
        GetProgressUseCase(repository), GetHomeUseCase(repository),
        PutStatusUseCase(repository), PutFavoriteUseCase(repository), ObserveLibraryUpdatesUseCase(repository), { now }
    )

    @After fun tearDown() { vm.viewModelScope.cancel() }

    @Test fun skeletonBecomesHeroWhileProgressKeepsItsOwnPlaceholder() {
        val pendingHome = CompletableDeferred<HomeResult>()
        val pendingProgress = CompletableDeferred<ProgressResult>()
        repository.homeResponse = { pendingHome.await() }
        repository.progressResponse = { pendingProgress.await() }
        compose.setContent { YumeTheme { HomeScreen({}, {}, vm, {}) } }
        compose.onNodeWithContentDescription("Загрузка домашнего экрана").assertIsDisplayed()
        compose.onNodeWithText("Подробнее").assertDoesNotExist()
        compose.runOnIdle { pendingHome.complete(HomeResult.Success(home)) }
        compose.onNodeWithText("Home hero").assertIsDisplayed()
        compose.onNodeWithContentDescription("Загрузка домашнего экрана").assertDoesNotExist()
        compose.onNodeWithContentDescription("Загрузка продолжения просмотра").assertIsDisplayed()
        compose.runOnIdle { pendingProgress.complete(ProgressResult.Success(ProgressData(emptyList()))) }
        compose.onNodeWithContentDescription("Загрузка продолжения просмотра").assertDoesNotExist()
        compose.onNodeWithText("Продолжить просмотр").assertDoesNotExist()
    }

    @Test fun returningAndBackgroundFailureKeepContentAndHorizontalPosition() {
        val visible = mutableStateOf(true)
        compose.setContent {
            val holder = rememberSaveableStateHolder()
            YumeTheme {
                if (visible.value) holder.SaveableStateProvider("home") {
                    HomeScreen({}, {}, vm, {})
                }
            }
        }
        compose.onNodeWithText("Home hero").assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(4)
        compose.onNodeWithText("Continue 4").assertIsDisplayed()

        val pendingProgress = CompletableDeferred<ProgressResult>()
        compose.runOnIdle {
            repository.progressResponse = { pendingProgress.await() }
            visible.value = false
        }
        compose.waitForIdle()
        compose.runOnIdle { visible.value = true }
        compose.onNodeWithText("Home hero").assertIsDisplayed()
        compose.onNodeWithText("Continue 4").assertIsDisplayed()
        compose.onAllNodes(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertCountEquals(0)
        compose.runOnIdle {
            assertEquals(1, repository.homeCalls)
            assertEquals(2, repository.progressCalls)
            pendingProgress.complete(ProgressResult.Error("offline"))
        }
        compose.onNodeWithText("Continue 4").assertIsDisplayed()

        val pendingHome = CompletableDeferred<HomeResult>()
        compose.runOnIdle {
            now = HomeViewModel.HOME_CACHE_TTL_MS
            repository.homeResponse = { pendingHome.await() }
            visible.value = false
        }
        compose.waitForIdle()
        compose.runOnIdle { visible.value = true }
        compose.onNodeWithText("Home hero").assertIsDisplayed()
        compose.onNodeWithText("Continue 4").assertIsDisplayed()
        compose.onAllNodes(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertCountEquals(0)
        compose.runOnIdle {
            assertEquals(2, repository.homeCalls)
            pendingHome.complete(HomeResult.Error("offline"))
        }
        compose.onNodeWithText("Home hero").assertIsDisplayed()
        compose.onNodeWithText("Continue 4").assertIsDisplayed()
        compose.onAllNodes(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertCountEquals(0)
    }

    @Test fun firstLoadFailureOffersRetryInsteadOfBlankScreen() {
        repository.homeResponse = { HomeResult.Error("offline") }
        compose.setContent { YumeTheme { HomeScreen({}, {}, vm, {}) } }
        compose.onNodeWithText("Не удалось загрузить домашний экран").assertIsDisplayed()
        compose.runOnIdle { repository.homeResponse = { HomeResult.Success(home) } }
        compose.onNodeWithText("Повторить").performClick()
        compose.onNodeWithText("Home hero").assertIsDisplayed()
    }

    @Test fun heroListButtonSavesAndRemovesStatusWithoutStatusCaption() {
        compose.setContent { YumeTheme { HomeScreen({}, {}, vm, {}) } }
        compose.onNodeWithContentDescription("Добавить в список").performClick()
        compose.onNodeWithText("В планах").performClick()
        compose.onNodeWithContentDescription("Изменить список").assertIsDisplayed()
        compose.onNodeWithText("В списке: в планах").assertDoesNotExist()
        compose.onNodeWithText("Статус просмотра").assertDoesNotExist()
        compose.onNodeWithContentDescription("Изменить список").performClick()
        compose.onNodeWithText("Не смотрю").performClick()
        compose.onNodeWithContentDescription("Добавить в список").assertIsDisplayed()
    }

    @Test fun chainsawManTitleFitsOnNarrowPhoneAndDetailsStillOpens() {
        var openedId: Int? = null
        compose.setContent {
            YumeTheme {
                Box(Modifier.width(320.dp)) {
                    Hero(
                        onItemClick = { openedId = it },
                        onListClick = {},
                        onFavoriteClick = {},
                        heroData = home.hero.copy(title = "Человек-бензопила"),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
        val results = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("Человек-бензопила").performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(results)
        }
        assertFalse(results.single().hasVisualOverflow)
        compose.onNodeWithText("Подробнее").performClick()
        compose.runOnIdle { assertEquals(1, openedId) }
    }

    @Test fun heroFavoriteButtonTogglesWithoutChangingList() {
        repository.libraryUpdates.value = mapOf(1 to Status(1, "в планах", false, null, null))
        compose.setContent { YumeTheme { HomeScreen({}, {}, vm, {}) } }
        compose.onNodeWithContentDescription("Добавить в избранное").performClick()
        compose.onNodeWithContentDescription("Убрать из избранного").assertIsDisplayed()
        compose.onNodeWithContentDescription("Изменить список").assertIsDisplayed()
        compose.runOnIdle { assertEquals("в планах", repository.libraryUpdates.value[1]?.status) }
        compose.onNodeWithContentDescription("Убрать из избранного").performClick()
        compose.onNodeWithContentDescription("Добавить в избранное").assertIsDisplayed()
        compose.onNodeWithContentDescription("Изменить список").assertIsDisplayed()
    }

    private inner class FakeRepository : MetaRepository, MeRepository {
        var homeCalls = 0
        var progressCalls = 0
        var homeResponse: suspend () -> HomeResult = { HomeResult.Success(home) }
        var progressResponse: suspend () -> ProgressResult = { ProgressResult.Success(progress) }
        override suspend fun getHome(): HomeResult { homeCalls++; return homeResponse() }
        override suspend fun getProgress(): ProgressResult { progressCalls++; return progressResponse() }
        override suspend fun getGenres(): GenresResult = error("not used")
        override val libraryUpdates = MutableStateFlow(emptyMap<Int, Status>())
        override suspend fun putProgress(progress: Progress): OperationResult = error("not used")
        override suspend fun getProgressById(id: Int): CurrentProgressResult = error("not used")
        override suspend fun getStatus(id: Int): StatusResult = error("not used")
        override suspend fun putStatus(id: Int, status: String?): StatusResult {
            val updated = (libraryUpdates.value[id] ?: Status(id, null, false, null, null)).copy(status = status)
            libraryUpdates.value += id to updated
            return StatusResult.Success(updated)
        }
        override suspend fun putFavorite(id: Int, favorite: Boolean): StatusResult {
            val updated = (libraryUpdates.value[id] ?: Status(id, null, false, null, null)).copy(favorite = favorite)
            libraryUpdates.value += id to updated
            return StatusResult.Success(updated)
        }
        override suspend fun putScore(id: Int, score: Int?): StatusResult = error("not used")
        override fun getAnimeListByStatus(status: String, q: String?): Flow<PagingData<Anime>> = error("not used")
        override fun getAnimeListByFavorite(q: String?): Flow<PagingData<Anime>> = error("not used")
    }
}
