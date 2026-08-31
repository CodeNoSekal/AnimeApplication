package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.ProfileActualStatistics
import com.dmitry.yume.domain.models.ProfileLibraryStatistics
import com.dmitry.yume.domain.models.ProfileStatistics
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileStatisticsResponse(
    val library: ProfileLibraryStatisticsResponse,
    val actual: ProfileActualStatisticsResponse,
    val favorites: ProfileTotalResponse,
    val ratings: ProfileRatingStatisticsResponse,
    val reviews: ProfileReviewStatisticsResponse,
)

@JsonClass(generateAdapter = true)
data class ProfileLibraryStatisticsResponse(
    val total: Int = 0,
    @param:Json(name = "by_status") val byStatus: ProfileStatusCountsResponse,
)

@JsonClass(generateAdapter = true)
data class ProfileStatusCountsResponse(
    val watching: Int = 0,
    val planned: Int = 0,
    val completed: Int = 0,
    val dropped: Int = 0,
)

@JsonClass(generateAdapter = true)
data class ProfileActualStatisticsResponse(
    @param:Json(name = "titles_started") val titlesStarted: Int = 0,
    @param:Json(name = "titles_completed") val titlesCompleted: Int = 0,
    @param:Json(name = "titles_in_progress") val titlesInProgress: Int = 0,
    @param:Json(name = "episodes_started") val episodesStarted: Int = 0,
    @param:Json(name = "episodes_completed") val episodesCompleted: Int = 0,
    @param:Json(name = "episodes_in_progress") val episodesInProgress: Int = 0,
    @param:Json(name = "watched_minutes_estimate") val watchedMinutesEstimate: Double = 0.0,
    @param:Json(name = "last_watched_at") val lastWatchedAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class ProfileTotalResponse(val total: Int = 0)

@JsonClass(generateAdapter = true)
data class ProfileRatingStatisticsResponse(
    val count: Int = 0,
    val average: Double? = null,
)

@JsonClass(generateAdapter = true)
data class ProfileReviewStatisticsResponse(val count: Int = 0)

fun ProfileStatisticsResponse.toDomain() = ProfileStatistics(
    library = ProfileLibraryStatistics(
        total = library.total,
        watching = library.byStatus.watching,
        planned = library.byStatus.planned,
        completed = library.byStatus.completed,
        dropped = library.byStatus.dropped,
    ),
    actual = ProfileActualStatistics(
        titlesStarted = actual.titlesStarted,
        titlesCompleted = actual.titlesCompleted,
        titlesInProgress = actual.titlesInProgress,
        episodesStarted = actual.episodesStarted,
        episodesCompleted = actual.episodesCompleted,
        episodesInProgress = actual.episodesInProgress,
        watchedMinutesEstimate = actual.watchedMinutesEstimate,
        lastWatchedAt = actual.lastWatchedAt,
    ),
    favorites = favorites.total,
    ratingsCount = ratings.count,
    averageRating = ratings.average,
    reviewsCount = reviews.count,
)
