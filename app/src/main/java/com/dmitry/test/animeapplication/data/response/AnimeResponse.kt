package com.dmitry.test.animeapplication.data.response

import com.squareup.moshi.Json

data class AnimeResponse(
    val page: Int,
    val limit: Int,
    val total: Int,
    val items: List<AnimeData>
)

data class AnimeData(
    val id: Int,
    val title: String,
    @param:Json(name = "title_orig")
    val titleOrig: String,
    @param:Json(name = "poster_url")
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?
)
