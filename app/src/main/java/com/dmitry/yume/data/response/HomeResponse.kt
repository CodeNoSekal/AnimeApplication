package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.Home
import com.dmitry.yume.domain.models.Rail
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeResponse(
    val hero: HeroResponse,
    val rails: List<RailResponse>
)

@JsonClass(generateAdapter = true)
data class HeroResponse(
    @param:Json(name = "shikimori_id")
    val id: Int,
    val title: String? = null,
    @param:Json(name = "title_en")
    val titleEn: String? = null,
    @param:Json(name = "poster_full")
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

fun HeroResponse.toDomain(): Anime {
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

@JsonClass(generateAdapter = true)
data class RailResponse(
    val key: String,
    val title: String,
    val items: List<AnimeShort>
)

fun HomeResponse.toDomain(): Home{
    return Home(
        hero = hero.toDomain(),
        rails = rails.toDomain()
    )
}

fun RailResponse.toDomain(): Rail{
    return Rail(
        key = key,
        title = title,
        items = items.toDomain()
    )
}

@JvmName("railResponseToDomain")
fun List<RailResponse>.toDomain(): List<Rail>{
    return map {
        it.toDomain()
    }
}
