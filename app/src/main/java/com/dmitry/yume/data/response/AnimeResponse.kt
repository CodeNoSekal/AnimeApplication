package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.AnimeDetailed
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
    val title: String? = null,
    @param:Json(name = "title_en")
    val titleEn: String? = null,
    @param:Json(name = "poster_url")
    val posterUrl: String? = null,
    val year: Int? = null,
    @param:Json(name = "shikimori_rating")
    val rating: Double? = null,
    val kind: String? = null,

    @param:Json(name = "my_status")
    val myStatus: String? = null,
    val favorite: Boolean = false,
    @param:Json(name = "my_score")
    val myScore: Int? = null,
)

@JsonClass(generateAdapter = true)
data class AnimeDetailResponse(
    @param:Json(name = "shikimori_id")
    val id: Int,
    val title: String? = null,
    @param:Json(name = "title_en")
    val titleEn: String? = null,
    @param:Json(name = "poster_full")
    val posterUrl: String? = null,
    val year: Int? = null,
    val description: String? = null,

    val duration: Int? = null,
    val genres: List<String>? = null,
    val studios: List<String>? = null,

    @param:Json(name = "shikimori_rating")
    val rating: Double? = null,
    val kind: String? = null,

    @param:Json(name = "is_available")
    val isAvailable: Boolean = false,
    @param:Json(name = "has_kodik")
    val hasKodik: Boolean = false,
    @param:Json(name = "has_libria")
    val hasLibria: Boolean = false,

    @param:Json(name = "my_status")
    val myStatus: String? = null,
    val favorite: Boolean = false,
    @param:Json(name = "my_score")
    val myScore: Int? = null,
)

fun AnimeShort.toDomain(): Anime {
    return Anime(
        id = id,
        title = title,
        titleEn = titleEn,
        posterUrl = posterUrl,
        year = year,
        rating = rating,
        status = myStatus,
        favorite = favorite
    )
}

fun AnimeDetailResponse.toDomain(): AnimeDetailed{
    return AnimeDetailed(
        id = id,
        title = title,
        titleEn = titleEn,
        posterUrl = posterUrl,
        year = year,
        description = description,
        duration = duration,
        genres = genres,
        studios = studios,
        rating = rating,
        isAvailable = isAvailable,
        hasKodik = hasKodik,
        hasLibria = hasLibria,
        status = myStatus,
        favorite = favorite,
        score = myScore
    )
}
