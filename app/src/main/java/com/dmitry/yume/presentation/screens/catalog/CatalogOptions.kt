package com.dmitry.yume.presentation.screens.catalog

import com.dmitry.yume.domain.models.SearchOptions

data class CatalogOptions(
    val filters: FilterOptions = FilterOptions(),
    val sorting: SortingOptions = SortingOptions()
)

data class FilterOptions(
    var genres: Set<Int> = emptySet(),
    var excludeGenres: Set<Int> = emptySet(),
    val statuses: Set<Status> = emptySet(),
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val kinds: Set<AnimeKind> = emptySet(),
    val collections: Set<Collection> = emptySet(),
    val excludeCollections: Set<Collection> = emptySet(),
)

data class SortingOptions(
    val sort: Sort = Sort.Rating,
    val order: Order = Order.Desc
)


fun CatalogOptions.toDomain() = SearchOptions(
    genreIds = filters.genres.toList(),
    excludeGenreIds = filters.excludeGenres.toList(),
    statuses = filters.statuses.map { it.raw },
    yearFrom = filters.yearFrom,
    yearTo = filters.yearTo,
    kinds = filters.kinds.map { it.raw },
    myStatuses = filters.collections.map { it.raw },
    excludeMyStatuses = filters.excludeCollections.map { it.raw },
    sort = sorting.sort.toRaw(),
    order = sorting.order.toRaw(),
)

enum class Status(
    val raw: String,
    val title: String,
){
    Anons("anons", "Анонс"),
    Ongoing("ongoing", "Онгоинг"),
    Released("released", "Вышел"),
}

enum class Collection(
    val raw: String,
    val title: String,
){
    Watching("watching", "Смотрю"),
    Planned("planned", "В планах"),
    Completed("completed", "Просмотрено"),
    Dropped("dropped", "Брошено"),
}

enum class Sort {
    Rating,
    Year,
    Updated,
    Title;

    fun toRaw(): String = when (this) {
        Rating -> "rating"
        Year -> "year"
        Updated -> "updated"
        Title -> "title"
    }

    fun toUi(): String = when (this) {
        Rating -> "Рейтинг"
        Year -> "Год"
        Updated -> "Обновление"
        Title -> "Название"
    }
}

enum class Order {
    Asc,
    Desc;

    fun toRaw(): String = when (this) {
        Asc -> "asc"
        Desc -> "desc"
    }
}

enum class AnimeKind(
    val raw: String,
    val title: String,
) {
    Tv("tv", "Сериал"),
    Movie("movie", "Фильм"),
    Ova("ova", "OVA"),
    Ona("ona", "ONA"),
    Special("special", "Спешл"),
    Music("music", "Музыка"),
}
