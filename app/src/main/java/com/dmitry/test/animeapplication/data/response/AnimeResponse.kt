package com.dmitry.test.animeapplication.data.response

import com.dmitry.test.animeapplication.domain.Anime
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnimeResponse(
    val meta: Meta,
    val items: List<AnimeShort>
)

@JsonClass(generateAdapter = true)
data class Meta(
    val page: Int,
    @param:Json(name = "per_page")
    val perPage: Int,
    val total: Int,
    @param:Json(name = "total_pages")
    val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class AnimeShort(
    @param:Json(name = "shikimori_id")
    val id: Int,
    val title: String?,
    @param:Json(name = "title_en")
    val titleEn: String?,
    @param:Json(name = "poster_url")
    val posterUrl: String?,
    val year: Int?,
    @param:Json(name = "shikimori_rating")
    val rating: Double?,
    val kind: String,
)

fun AnimeShort.toDomain(): Anime {
    return Anime(
        id = id,
        title = title,
        titleEn = titleEn,
        posterUrl = posterUrl,
        year = year,
        rating = rating
    )
}
