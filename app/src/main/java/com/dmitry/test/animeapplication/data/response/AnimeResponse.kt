package com.dmitry.test.animeapplication.data.response

import androidx.compose.ui.graphics.Color
import com.dmitry.test.animeapplication.domain.models.Anime
import com.dmitry.test.animeapplication.domain.models.AnimeDetailed
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeColors
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
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

    @param:Json(name = "in_library")
    val inLibrary: Boolean,
    @param:Json(name = "my_status")
    val myStatus: String?,
    val favorite: Boolean,
    @param:Json(name = "my_score")
    val myScore: Int?,
)

@JsonClass(generateAdapter = true)
data class AnimeDetailResponse(
    @param:Json(name = "shikimori_id")
    val id: Int,
    val title: String?,
    @param:Json(name = "title_en")
    val titleEn: String?,
    @param:Json(name = "poster_full")
    val posterUrl: String?,
    val year: Int?,
    @param:Json(name = "shikimori_rating")
    val rating: Double?,
    val kind: String,

    @param:Json(name = "has_kodik")
    val hasKodik: Boolean,
    @param:Json(name = "has_anilibria")
    val hasAnilibria: Boolean,

    @param:Json(name = "in_library")
    val inLibrary: Boolean,
    @param:Json(name = "my_status")
    val myStatus: String?,
    val favorite: Boolean,
    @param:Json(name = "my_score")
    val myScore: Int?,
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
        rating = rating
    )
}



