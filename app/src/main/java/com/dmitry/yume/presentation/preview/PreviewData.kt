package com.dmitry.yume.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.ExploreModel
import com.dmitry.yume.domain.models.HomeData
import com.dmitry.yume.domain.models.ProgressData
import com.dmitry.yume.domain.models.ProgressItemData
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.RankedAnime
import com.dmitry.yume.domain.models.SeasonModel
import com.dmitry.yume.domain.models.SectionModel

// Fake addresses: they are not real links, PreviewImages maps them to local drawables.
private const val DANDADAN = "preview://dandadan"
private const val JJK = "preview://jjk"
private const val FRIEREN = "preview://frieren"

object PreviewData {

    val heroes = listOf(
        Anime(
            id = 1, title = "Дандадан 3", titleEn = "Dandadan Season 3",
            posterUrl = DANDADAN, year = 2027, rating = null,
            status = "anons", favorite = false, kind = "tv",
        ),
        Anime(
            id = 2, title = "Магическая битва: Смертельная миграция", titleEn = "Jujutsu Kaisen",
            posterUrl = JJK, year = 2026, rating = 8.7,
            status = "ongoing", favorite = true, kind = "tv", myStatus = "watching",
        ),
        Anime(
            id = 3, title = "Фрирен", titleEn = "Sousou no Frieren",
            posterUrl = FRIEREN, year = 2023, rating = 9.3,
            status = "released", favorite = false, kind = "tv", myStatus = "completed", myScore = 10,
        ),
    )

    val ranking = listOf(
        RankedAnime(10, "Атака титанов", "Shingeki no Kyojin", DANDADAN, 2013, 8.5, "released", false, "tv", rank = 1),
        RankedAnime(11, "Стальной алхимик: Братство", "Fullmetal Alchemist: Brotherhood", JJK, 2009, 9.1, "released", true, "tv", rank = 2),
        RankedAnime(12, "Человек-бензопила", "Chainsaw Man", FRIEREN, 2022, 8.6, "released", false, "tv", rank = 3),
        RankedAnime(13, "Поднятие уровня в одиночку", "Solo Leveling", DANDADAN, 2024, 8.3, "ongoing", false, "tv", myStatus = "planned", rank = 4),
        RankedAnime(14, "Твоё имя", "Kimi no Na wa", JJK, 2016, 8.8, "released", false, "movie", rank = 5),
        // Worst case: almost everything is missing, including the poster.
        RankedAnime(15, "Очень длинное название тайтла, чтобы проверить перенос строк", null, null, null, null, null, false, null, rank = 6),
    )

    val homeData = HomeData(
        updated = "2026-10-09T12:00:00Z",
        hero = heroes,
        ranking = ranking,
    )

    val progress = ProgressData(
        items = listOf(
            ProgressItemData(1, "Дандадан", DANDADAN, 5, positionMs = 12 * 60_000L, durationMs = 24 * 60_000L, isCompleted = false, sourceProvider = Provider.Kodik, voiceoverId = null),
            ProgressItemData(2, "Магическая битва", JJK, 12, positionMs = 2 * 60_000L, durationMs = 24 * 60_000L, isCompleted = false, sourceProvider = Provider.Libria, voiceoverId = null),
            ProgressItemData(3, "Фрирен", FRIEREN, 28, positionMs = 24 * 60_000L, durationMs = 24 * 60_000L, isCompleted = true, sourceProvider = null, voiceoverId = null),
        )
    )

    val seasons = listOf(
        SeasonModel(
            key = "fall_2026", label = "Осень 2026", position = "current", isDefault = true,
            total = 48, posters = listOf(DANDADAN, JJK, FRIEREN, DANDADAN), endpoint = "/anime?season=fall_2026",
        ),
        SeasonModel(
            key = "summer_2026", label = "Лето 2026", position = "previous", isDefault = false,
            total = 61, posters = listOf(JJK, FRIEREN, DANDADAN), endpoint = "/anime?season=summer_2026",
        ),
        // Worst case: next season is announced, but nothing has posters yet.
        SeasonModel(
            key = "winter_2027", label = "Зима 2027", position = "next", isDefault = false,
            total = 3, posters = emptyList(), endpoint = "/anime?season=winter_2027",
        ),
    )

    val sections = listOf(
        SectionModel(
            key = "ongoing", title = "Онгоинги", subtitle = "Выходят прямо сейчас",
            total = 112, posters = listOf(JJK, DANDADAN, FRIEREN), endpoint = "/anime?status=ongoing",
        ),
        SectionModel(
            key = "movies", title = "Полнометражные фильмы", subtitle = "Чтобы посмотреть за один вечер",
            total = 540, posters = listOf(FRIEREN, JJK, DANDADAN), endpoint = "/anime?kind=movie",
        ),
        SectionModel(
            key = "top_rated", title = "Лучшие по оценкам", subtitle = "Рейтинг 8.5 и выше",
            total = 87, posters = listOf(DANDADAN, FRIEREN), endpoint = "/anime?rating_from=8.5",
        ),
        // Worst case: long texts and a single poster.
        SectionModel(
            key = "long", title = "Очень длинное название подборки, чтобы проверить перенос строк",
            subtitle = "И такое же длинное описание под ним, которое тоже может не поместиться в одну строку",
            total = 1, posters = listOf(JJK), endpoint = "/anime?collection=long",
        ),
    )

    val explore = ExploreModel(
        updated = "2026-10-14T12:00:00Z",
        seasons = seasons,
        sections = sections,
    )
}

// Wrap a preview in this so every AsyncImage inside shows a local poster instead of loading from network.
@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewImages(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val handler = AsyncImagePreviewHandler { request ->
        val res = when (request.data) {
            DANDADAN -> R.drawable.preview_dandadan
            JJK -> R.drawable.preview_jjk
            FRIEREN -> R.drawable.preview_frieren
            else -> null
        }
        val drawable = res?.let { ContextCompat.getDrawable(context, it) }
            ?: 0xFF2A2833.toInt().toDrawable() // unknown address: plain gray fill
        drawable.asImage()
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides handler) {
        content()
    }
}
