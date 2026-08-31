package com.dmitry.yume.presentation.screens.exploration

import com.dmitry.yume.domain.models.SearchOptions

private fun typeOptions(kind: String) = SearchOptions(
    kinds = listOf(kind),
    sort = "updated",
    order = "desc",
)

enum class QuickSearchCategory(
    val route: String,
    val title: String,
    val label: String,
    val description: String,
    val options: SearchOptions,
) {
    Tv(
        route = "tv",
        title = "Сериалы",
        label = "TV",
        description = "Новые и обновлённые сериалы",
        options = typeOptions("tv"),
    ),
    Movie(
        route = "movie",
        title = "Фильмы",
        label = "MOVIE",
        description = "Полнометражное аниме",
        options = typeOptions("movie"),
    ),
    Ova(
        route = "ova",
        title = "OVA",
        label = "OVA",
        description = "Отдельные видео-релизы",
        options = typeOptions("ova"),
    ),
    Ona(
        route = "ona",
        title = "ONA",
        label = "ONA",
        description = "Аниме для онлайн-показа",
        options = typeOptions("ona"),
    ),
    Special(
        route = "special",
        title = "Спешлы",
        label = "SPECIAL",
        description = "Специальные выпуски",
        options = typeOptions("special"),
    ),
    Music(
        route = "music",
        title = "Музыка",
        label = "MUSIC",
        description = "Музыкальные видео",
        options = typeOptions("music"),
    ),
    Announcements(
        route = "announcements",
        title = "Анонсы",
        label = "SOON",
        description = "Объявленные будущие тайтлы",
        options = SearchOptions(
            statuses = listOf("anons"),
            sort = "updated",
            order = "desc",
        ),
    ),
    RecentReleases(
        route = "recent-releases",
        title = "Недавние релизы",
        label = "NEW",
        description = "Вышедшие тайтлы по дате релиза",
        options = SearchOptions(
            statuses = listOf("released"),
            sort = "year",
            order = "desc",
        ),
    );

    companion object {
        fun fromRoute(route: String?): QuickSearchCategory =
            entries.firstOrNull { it.route == route } ?: Tv
    }
}
