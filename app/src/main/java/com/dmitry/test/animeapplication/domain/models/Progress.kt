package com.dmitry.test.animeapplication.domain.models

data class Progress (
    val animeId: Int,
    val episodeNumber: Int,
    val positionMs: Long,
    val durationMs: Long,
    val sourceProvider: String?,
    val voiceoverId: Int?,
)

data class ProgressData (
    val items: List<ProgressItemData>
)

data class ProgressItemData (
    val animeId: Int,
    val title: String?,
    val posterUrl: String?,
    val episodeNumber: Int,
    val positionMs: Long,
    val durationMs: Long,
    val isCompleted: Boolean,
    val sourceProvider: Provider?,
    val voiceoverId: Int?,
)