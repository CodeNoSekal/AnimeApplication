package com.dmitry.yume.domain.models

data class RankedAnime(
    val id: Int,
    val title: String?,
    val titleEn: String?,
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?,
    val status: String? = null,
    val favorite: Boolean,

    val kind: String? = null,
    val isAvailable: Boolean = false,

    val myStatus: String? = null,
    val myScore: Int? = null,
    val rank: Int,
)
