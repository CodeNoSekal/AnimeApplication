package com.dmitry.test.animeapplication.domain

import com.dmitry.test.animeapplication.data.response.AnimeData

data class Anime(
    val id: Int,
    val title: String,
    val titleOrig: String,
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?
)

fun AnimeData.toDomain(): Anime {
    return Anime(
        id = id,
        title = title,
        titleOrig = titleOrig,
        posterUrl = posterUrl,
        year = year,
        rating = rating
    )
}