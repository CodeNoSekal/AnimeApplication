package com.dmitry.yume.domain.models

data class ProfileStatistics(
    val library: ProfileLibraryStatistics,
    val actual: ProfileActualStatistics,
    val favorites: Int,
    val ratingsCount: Int,
    val averageRating: Double?,
    val reviewsCount: Int,
)

data class ProfileLibraryStatistics(
    val total: Int,
    val watching: Int,
    val planned: Int,
    val completed: Int,
    val dropped: Int,
)

data class ProfileActualStatistics(
    val titlesStarted: Int,
    val titlesCompleted: Int,
    val titlesInProgress: Int,
    val episodesStarted: Int,
    val episodesCompleted: Int,
    val episodesInProgress: Int,
    val watchedMinutesEstimate: Double,
    val lastWatchedAt: String?,
)
