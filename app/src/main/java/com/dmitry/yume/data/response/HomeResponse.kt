package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.HomeData
import com.dmitry.yume.domain.models.RankedAnime
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeResponse(
    @param:Json(name = "updated_on")
    val updated: String,
    @param:Json(name = "hero")
    val hero: List<AnimeShortResponse>,
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
