package com.dmitry.yume.data.request

import com.dmitry.yume.domain.models.SearchOptions
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.Int
import kotlin.collections.List

@JsonClass(generateAdapter = true)
data class OptionsRequest(
    @param:Json(name = "genre_ids")
    val genreIds: List<Int>?,
    @param:Json(name = "exclude_genre_ids")
    val excludeGenreIds: List<Int>?,
    @param:Json(name = "year_from")
    val yearFrom: Int?,
    @param:Json(name = "year_to")
    val yearTo: Int?,
    @param:Json(name = "statuses")
    val statuses: List<String>?,
    @param:Json(name = "exclude_statuses")
    val excludeStatuses: List<String>?,
    @param:Json(name = "kinds")
    val kinds: List<String>?,
    @param:Json(name = "exclude_kinds")
    val excludeKinds: List<String>?,
    @param:Json(name = "min_rating")
    val minRating: Double?,
    @param:Json(name = "max_rating")
    val maxRating: Double?,
    @param:Json(name = "my_statuses")
    val myStatuses: List<String>?,
    @param:Json(name = "exclude_my_statuses")
    val excludeMyStatuses: List<String>?,
    @param:Json(name = "sort")
    val sort: String = "rating",
    @param:Json(name = "order")
    val order: String = "desc"
) {
    companion object {
        fun from(options: SearchOptions): OptionsRequest =
            OptionsRequest(
                genreIds = options.genreIds.takeIf { it.isNotEmpty() },
                excludeGenreIds = options.excludeGenreIds.takeIf { it.isNotEmpty() },
                yearFrom = options.yearFrom,
                yearTo = options.yearTo,
                statuses = options.statuses.takeIf { it.isNotEmpty() },
                excludeStatuses = options.excludeStatuses.takeIf { it.isNotEmpty() },
                kinds = options.kinds.takeIf { it.isNotEmpty() },
                excludeKinds = options.excludeKinds.takeIf { it.isNotEmpty() },
                minRating = options.minRating,
                maxRating = options.maxRating,
                myStatuses = options.myStatuses.takeIf { it.isNotEmpty() },
                excludeMyStatuses = options.excludeMyStatuses.takeIf { it.isNotEmpty() },
                sort = options.sort,
                order = options.order,
            )
    }
}

