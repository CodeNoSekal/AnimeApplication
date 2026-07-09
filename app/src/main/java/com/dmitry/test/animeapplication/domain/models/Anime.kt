package com.dmitry.test.animeapplication.domain.models

data class Anime(
    val id: Int,
    val title: String?,
    val titleEn: String?,
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?
)
