package com.dmitry.test.animeapplication.data.response

import com.squareup.moshi.Json

data class AnimeResponse(
    val page: Int,
    @param:Json(name = "per_page")
    val perPage: Int,
    val total: Int,
    @param:Json(name = "total_pages")
    val totalPages: Int,
    val items: List<AnimeData>
)

data class AnimeData(
    @param:Json(name = "shikimori_id")
    val id: Int,
    val title: String,
    @param:Json(name = "title_en")
    val titleEn: String,
    @param:Json(name = "poster_url")
    val posterUrl: String?,
    val year: Int?,
    @param:Json(name = "shikimori_rating")
    val rating: Double?
)
