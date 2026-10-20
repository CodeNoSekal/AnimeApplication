package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.Genre
import com.dmitry.yume.domain.models.Meta
import com.dmitry.yume.domain.models.HomeData
import com.dmitry.yume.domain.models.RankedAnime
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeResponse(
    @param:Json(name = "updated_on")
    val updated: String,
    @param:Json(name = "hero")
    val hero: List<AnimeShort>,
    @param:Json(name = "top")
    val ranking: List<RankedAnimeResponse>
)

@JsonClass(generateAdapter = true)
data class RankedAnimeResponse(
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
    val status: String? = null,
    val kind: String? = null,
    @param:Json(name = "is_available")
    val isAvailable: Boolean = false,

    @param:Json(name = "my_status")
    val myStatus: String? = null,
    val favorite: Boolean = false,
    @param:Json(name = "my_score")
    val myScore: Int? = null,

    val rank: Int,
)

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

fun RankedAnimeResponse.toDomain(): RankedAnime {
    return RankedAnime(
        id = id,
        title = title,
        titleEn = titleEn,
        posterUrl = posterUrl,
        year = year,
        rating = rating,
        status = status,
        favorite = favorite,
        kind = kind,
        myStatus = myStatus,
        myScore = myScore,
        rank = rank
    )
}

fun HomeResponse.toDomain(): HomeData{
    return HomeData(
        updated = updated,
        hero = hero.map { it.toDomain() },
        ranking = ranking.map { it.toDomain() }
    )
}
