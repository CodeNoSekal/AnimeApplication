package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.AnimeDetailed
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.String

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
    val status: String? = null,
    val kind: String? = null,
    @param:Json(name = "is_available")
    val isAvailable: Boolean = false,

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

    val relations: List<AnimeShort> = emptyList(),
    @param:Json(name = "rating")
    val displayRating: Double? = null,
    @param:Json(name = "rating_source")
    val ratingSource: String? = null,
    val status: String? = null,
    @param:Json(name = "rating_mpaa")
    val ageRating: String? = null,
    @param:Json(name = "episodes_total")
    val episodesTotal: Int? = null,
    @param:Json(name = "episodes_available")
    val episodesAvailable: Int? = null,
    @param:Json(name = "aired_on")
    val airedOn: String? = null,
    @param:Json(name = "aired_on_precision")
    val airedOnPrecision: String? = null,
    @param:Json(name = "released_on")
    val releasedOn: String? = null,
    @param:Json(name = "released_on_precision")
    val releasedOnPrecision: String? = null,
    @param:Json(name = "next_episode_at")
    val nextEpisodeAt: String? = null,
    @param:Json(name = "last_episode_number")
    val lastEpisodeNumber: Int? = null
)

fun AnimeShort.toDomain(): Anime {
    return Anime(
        id = id,
        title = title,
        titleEn = titleEn,
        posterUrl = posterUrl,
        year = year,
        rating = rating,
        status = status,
        favorite = favorite,
        kind = kind,
        isAvailable = isAvailable,
        myStatus = myStatus,
        myScore = myScore,
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
        rating = displayRating ?: rating,
        isAvailable = isAvailable,
        hasKodik = hasKodik,
        hasLibria = hasLibria,
        status = myStatus,
        favorite = favorite,
        score = myScore,
        relations = relations.toDomain(),
        kind = kind,
        releaseStatus = status,
        ratingSource = ratingSource,
        ageRating = ageRating,
        episodesTotal = episodesTotal,
        episodesAvailable = episodesAvailable,
        airedOn = airedOn,
        airedOnPrecision = airedOnPrecision,
        releasedOn = releasedOn,
        releasedOnPrecision = releasedOnPrecision,
        nextEpisodeAt = nextEpisodeAt,
        lastEpisodeNumber = lastEpisodeNumber
    )
}

@JvmName("animeShortToDomain")
fun List<AnimeShort>.toDomain(): List<Anime> {
    return map {
        it.toDomain()
    }
}
