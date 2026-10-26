package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.Genre
import com.dmitry.yume.domain.models.Meta
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetaResponse(
    @param:Json(name = "genres")
    val genres: List<GenreResponse>,
    @param:Json(name = "year_from")
    val yearFrom: Int,
    @param:Json(name = "year_to")
    val yearTo: Int,
)

fun MetaResponse.toDomain() = Meta(
    genres = genres.map { it.toDomain() },
    yearFrom = yearFrom,
    yearTo = yearTo,
)

@JsonClass(generateAdapter = true)
data class GenreResponse(
    val id: Int,
    val name: String,
)

fun GenreResponse.toDomain() = Genre(id, name)
