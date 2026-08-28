package com.dmitry.yume.domain.models

data class SearchOptions(
    val genreIds: List<Int> = emptyList(),
    val excludeGenreIds: List<Int> = emptyList(),
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val statuses: List<String> = emptyList(),
    val excludeStatuses: List<String> = emptyList(),
    val kinds: List<String> = emptyList(),
    val excludeKinds: List<String> = emptyList(),
    val minRating: Double? = null,
    val maxRating: Double? = null,
    val myStatuses: List<String> = emptyList(),
    val excludeMyStatuses: List<String> = emptyList(),
    val sort: String = "rating",
    val order: String = "desc",
)
