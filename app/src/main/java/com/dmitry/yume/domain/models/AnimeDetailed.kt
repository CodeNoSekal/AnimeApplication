package com.dmitry.yume.domain.models

data class AnimeDetailed(
    val id: Int,
    val title: String?,
    val titleEn: String?,
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?,
    val hasKodik: Boolean = false,
    val hasLibria: Boolean = false,
    val status: String? = null,
    val favorite: Boolean = false,
    val score: Int? = null,
    val isAvailable: Boolean,
    val description: String?,
    val duration: Int?,
    val genres: List<String>?,
    val studios: List<String>?
)
