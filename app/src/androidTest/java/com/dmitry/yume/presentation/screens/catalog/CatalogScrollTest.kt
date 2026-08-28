package com.dmitry.yume.presentation.screens.catalog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.presentation.components.list.AnimeList
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class CatalogScrollTest {
    @get:Rule val compose = createComposeRule()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val idle = LoadState.NotLoading(false)
    private val rows = (0 until 300).map {
        Anime(it, "Anime $it", null, null, 2026, null, "released", false)
    }

    @After fun tearDown() { scope.cancel() }

    @Test fun returnRestoresShallowAndDeepPositionsWithPixelOffset() {
        val visible = mutableStateOf(true)
        val session = session(0)
        compose.setContent {
            val holder = rememberSaveableStateHolder()
            YumeTheme {
                if (visible.value) holder.SaveableStateProvider("catalog") {
                    CatalogContent(session, {}, {}, {}, {})
                }
            }
        }
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Anime 0").fetchSemanticsNodes().isNotEmpty()
        }
        for (index in listOf(10, 70, 200)) {
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(index)
                .performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 37f) }
            compose.waitForIdle()
            val expected = scrollPosition()
            compose.onNodeWithText("Anime $index").assertIsDisplayed()
            compose.runOnIdle { visible.value = false }
            compose.waitForIdle()
            compose.runOnIdle { visible.value = true }
            compose.waitForIdle()
            compose.onNodeWithText("Anime $index").assertIsDisplayed()
            assertEquals(expected, scrollPosition(), 0.001f)
        }
    }

    @Test fun applyingFiltersWhileAwayDoesNotRestoreOldQueryPosition() {
        val visible = mutableStateOf(true)
        val current = mutableStateOf(session(0))
        compose.setContent {
            val holder = rememberSaveableStateHolder()
            YumeTheme {
                if (visible.value) holder.SaveableStateProvider("catalog") {
                    CatalogContent(current.value, {}, {}, {}, {})
                }
            }
        }
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Anime 0").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(200)
        compose.runOnIdle { visible.value = false }
        compose.waitForIdle()
        compose.runOnIdle {
            current.value = session(1, reversed = true)
            visible.value = true
        }
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Anime 299").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Anime 299").assertIsDisplayed()
        assertEquals(0f, scrollPosition(), 0.001f)
        // Changing the query while still visible also resets the list.
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(70)
        compose.runOnIdle { current.value = session(2) }
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Anime 0").fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(0f, scrollPosition(), 0.001f)
    }

    @Test fun skeletonAndEmptyErrorDoNotOverwriteSavedAnchor() {
        val listState = LazyListState(200, 37)
        val data = MutableStateFlow(PagingData.empty<Anime>(
            sourceLoadStates = LoadStates(LoadState.Loading, idle, idle)
        ))
        compose.setContent {
            YumeTheme { AnimeList(listState, data.collectAsLazyPagingItems(), {}, PaddingValues(0.dp)) }
        }
        compose.runOnIdle {
            assertEquals(200, listState.firstVisibleItemIndex)
            data.value = PagingData.empty(sourceLoadStates = LoadStates(LoadState.Error(IOException()), idle, idle))
        }
        compose.onNodeWithText("Не удалось загрузить аниме").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(200, listState.firstVisibleItemIndex)
            data.value = PagingData.from(rows, sourceLoadStates = LoadStates(idle, idle, idle))
        }
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Anime 200").fetchSemanticsNodes().isNotEmpty()
        }
        compose.runOnIdle {
            assertEquals(200, listState.firstVisibleItemIndex)
            assertEquals(37, listState.firstVisibleItemScrollOffset)
        }
    }

    @Test fun refreshFailureKeepsCardsAndShowsRetryAndEmptyHasOwnMessage() {
        val data = MutableStateFlow(PagingData.from(rows,
            sourceLoadStates = LoadStates(idle, idle, idle)))
        val listState = LazyListState()
        compose.setContent {
            YumeTheme { AnimeList(listState, data.collectAsLazyPagingItems(), {}, PaddingValues(0.dp)) }
        }
        compose.runOnIdle {
            data.value = PagingData.from(rows,
                sourceLoadStates = LoadStates(LoadState.Error(IOException()), idle, idle))
        }
        compose.onNodeWithText("Не удалось обновить список").assertIsDisplayed()
        compose.onNodeWithText("Повторить").assertIsDisplayed()
        compose.onNodeWithText("Anime 0").assertExists()
        compose.runOnIdle {
            data.value = PagingData.empty(sourceLoadStates = LoadStates(idle, idle, idle))
        }
        compose.onNodeWithText("Ничего не найдено").assertIsDisplayed()
    }

    private fun session(generation: Int, reversed: Boolean = false) = CatalogSession(
        generation,
        CatalogOptions(),
        flowOf(PagingData.from(if (reversed) rows.reversed() else rows,
            sourceLoadStates = LoadStates(idle, idle, idle))).cachedIn(scope)
    )

    private fun scrollPosition(): Float = compose.onNode(hasScrollToIndexAction())
        .fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value()
}
