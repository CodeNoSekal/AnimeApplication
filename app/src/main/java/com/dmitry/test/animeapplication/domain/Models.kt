package com.dmitry.test.animeapplication.domain

import com.dmitry.test.animeapplication.data.response.AnimeData
import com.squareup.moshi.Json

data class Anime(
    val id: Int,
    val title: String,
    val titleEn: String,
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?
)

fun AnimeData.toDomain(): Anime {
    return Anime(
        id = id,
        title = title,
        titleEn = titleEn,
        posterUrl = posterUrl,
        year = year,
        rating = rating
    )
}