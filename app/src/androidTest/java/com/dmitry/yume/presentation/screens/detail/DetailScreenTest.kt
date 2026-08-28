package com.dmitry.yume.presentation.screens.detail

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.Status
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {
    @get:Rule val compose = createComposeRule()
    private val anime = AnimeDetailed(
        id = 1, title = "Человек-бензопила", titleEn = "Chainsaw Man", posterUrl = null,
        year = 2027, rating = 8.7, isAvailable = false,
        description = "История героя, который пытается найти своё место в мире. ".repeat(20),
        duration = 24, genres = listOf("Экшен", "Фэнтези"), studios = listOf("MAPPA"),
        relations = emptyList(), kind = "tv", releaseStatus = "anons", ratingSource = "shikimori",
        episodesTotal = 12, episodesAvailable = 0, airedOn = "2027-10-01", airedOnPrecision = "month"
    )
    private val personal = mutableStateOf<StatusViewState>(StatusViewState.Success(Status(1, "в планах", true, null, null)))
    private val editor = mutableStateOf(ScoreEditorState())
    private val action = mutableStateOf(DetailActionState())
    private val saved = mutableListOf<Int?>()
    private var failSave = false
    private val displayedAnime = mutableStateOf(anime)
    private var openedRelation: Int? = null

    private fun showScreen() {
        compose.setContent {
            YumeTheme {
                DetailScreen(
                    onBackClick = {}, onPlayClick = {}, animeData = displayedAnime.value, statusState = personal.value,
                    setStatus = {}, setFavorite = {}, onRelationClick = { openedRelation = it }, actionState = action.value,
                    scoreEditor = editor.value,
                    onScoreClick = { editor.value = ScoreEditorState(true, (personal.value as StatusViewState.Success).status.score) },
                    onScoreDismiss = { editor.value = ScoreEditorState() },
                    onScoreSave = { score ->
                        saved += score
                        if (failSave) action.value = DetailActionState(error = "Нет соединения")
                        else {
                            personal.value = StatusViewState.Success((personal.value as StatusViewState.Success).status.copy(score = score))
                            editor.value = ScoreEditorState()
                        }
                    },
                    onErrorDismiss = { action.value = DetailActionState() }, onRetryPersonal = {}
                )
            }
        }
    }

    @Test fun announcementShowsMonthAndFactsAndDescriptionExpands() {
        showScreen()
        compose.onNodeWithText("Премьера · октябрь 2027").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Скоро").assertDoesNotExist()
        compose.onNodeWithText("Описание").assertDoesNotExist()
        compose.onNodeWithText("О тайтле").assertDoesNotExist()
        compose.onNodeWithText("Shikimori").assertDoesNotExist()
        val description = compose.onNodeWithText(anime.description!!)
        description.performScrollTo().performClick()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Развёрнуто"))
        description.performClick()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Свёрнуто"))
        compose.onNodeWithText("Серии: 0 из 12 · 24 мин").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Студия: MAPPA").performScrollTo().assertIsDisplayed()
    }

    @Test fun originalControlsStayAbovePlaybackAndSummaryPrecedesGenresAndRating() {
        showScreen()
        val favorite = compose.onNodeWithContentDescription("Убрать из избранного").getUnclippedBoundsInRoot()
        val list = compose.onNodeWithText("В планах").getUnclippedBoundsInRoot()
        val play = compose.onNodeWithText("Премьера · октябрь 2027").getUnclippedBoundsInRoot()
        val description = compose.onNodeWithText(anime.description!!).getUnclippedBoundsInRoot()
        val summary = compose.onNodeWithText("Студия: MAPPA").getUnclippedBoundsInRoot()
        val genre = compose.onNodeWithText("Фэнтези").getUnclippedBoundsInRoot()
        val rating = compose.onNodeWithText("Рейтинг").getUnclippedBoundsInRoot()
        assertTrue(favorite.right < list.left)
        assertTrue(list.bottom < play.top)
        assertTrue(play.bottom < description.top)
        assertTrue(description.bottom < summary.top)
        assertTrue(summary.bottom < genre.top)
        assertTrue(genre.bottom < rating.top)
        assertEquals(description.left, summary.left)
        assertEquals(description.left, rating.left)
    }

    @Test fun scoreIsDraftUntilSavedAndCanBeDeletedWithoutChangingList() {
        showScreen()
        compose.onNodeWithText("Ваша оценка").assertDoesNotExist()
        compose.onNodeWithText("Оценить").performScrollTo().performClick()
        compose.onNodeWithText("Сохранить").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Оценка 9 из 10").performClick().assertIsSelected()
        compose.runOnIdle { assertTrue(saved.isEmpty()) }
        compose.onNodeWithText("Сохранить").performClick()
        compose.onNodeWithText("Ваша оценка").assertIsDisplayed()
        compose.onNodeWithText("9/10").assertIsDisplayed().performClick()
        compose.onNodeWithContentDescription("Оценка 9 из 10").assertIsSelected()
        compose.onNodeWithText("Удалить оценку").performClick()
        compose.onNodeWithText("Оценить").assertIsDisplayed()
        compose.onNodeWithText("Ваша оценка").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf(9, null), saved)
            val status = (personal.value as StatusViewState.Success).status
            assertEquals("в планах", status.status)
            assertTrue(status.favorite)
        }
    }

    @Test fun scoreErrorKeepsSelectionAndRetryAvailable() {
        failSave = true
        showScreen()
        compose.onNodeWithText("Оценить").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Оценка 8 из 10").performClick()
        compose.onNodeWithText("Сохранить").performClick()
        compose.onNodeWithText("Нет соединения").assertIsDisplayed()
        compose.onNodeWithContentDescription("Оценка 8 из 10").assertIsSelected()
        compose.onNodeWithText("Сохранить").assertIsEnabled()
        compose.runOnIdle { failSave = false }
        compose.onNodeWithText("Сохранить").performClick()
        compose.onNodeWithText("8/10").assertIsDisplayed()
    }

    @Test fun movieShowsRuntimeAndReleaseWithoutEpisodesOrEndDate() {
        displayedAnime.value = anime.copy(
            kind = "movie", releaseStatus = "released", isAvailable = true,
            lastEpisodeNumber = 1, episodesTotal = 1, episodesAvailable = 1, duration = 110,
            releasedOn = "2027-11-30", releasedOnPrecision = "day"
        )
        showScreen()
        compose.onNodeWithText("Продолжить").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Вышел", substring = true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Длительность: 110 мин").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Серии:", substring = true).assertDoesNotExist()
        compose.onNodeWithText("Завершён", substring = true).assertDoesNotExist()
        compose.onNodeWithText("30 ноября 2027", substring = true).assertDoesNotExist()
    }

    @Test fun relatedSectionHidesZeroOrOneItemAndOpensAllItemsWithCurrentMarker() {
        showScreen()
        compose.onNodeWithText("Связанное").assertDoesNotExist()
        val related = (1..6).map {
            Anime(it, "Тайтл $it", null, null, 2027, 8.0, favorite = false, kind = "tv")
        }
        compose.runOnIdle { displayedAnime.value = anime.copy(relations = listOf(related.last())) }
        compose.onNodeWithText("Связанное").assertDoesNotExist()
        compose.runOnIdle { displayedAnime.value = anime.copy(relations = related) }
        compose.onNodeWithText("Тайтл 1").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Вы тут").assertIsDisplayed()
        compose.onNodeWithText("Тайтл 4").assertDoesNotExist()
        compose.onNodeWithText("Показать все").performScrollTo().performClick()
        compose.onNodeWithText("Вы тут").assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(5)
        compose.onNodeWithText("Тайтл 6").performClick()
        compose.runOnIdle { assertEquals(6, openedRelation) }
        compose.onNodeWithContentDescription("Назад").performClick()
        compose.onNodeWithText("Показать все").assertIsDisplayed()
    }

    @Test fun detailSkeletonKeepsBackAvailableWithoutFakeActions() {
        var backPressed = false
        compose.setContent { YumeTheme { DetailPlaceholder { backPressed = true } } }
        compose.onNodeWithContentDescription("Загрузка тайтла").assertIsDisplayed()
        compose.onNodeWithText("Оценить").assertDoesNotExist()
        compose.onNodeWithContentDescription("Назад").performClick()
        compose.runOnIdle { assertTrue(backPressed) }
    }
}
