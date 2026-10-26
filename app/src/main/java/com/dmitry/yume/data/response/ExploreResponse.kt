package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.ExploreData
import com.dmitry.yume.domain.models.Season
import com.dmitry.yume.domain.models.Section
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ExploreResponse(
    @param:Json(name = "updated_on")
    val updated: String,
    val seasons: List<SeasonResponse>,
    val sections: List<SectionResponse>,
)

fun ExploreResponse.toDomain() = ExploreData(
    updated = updated,
    seasons = seasons.map { it.toDomain() },
    sections = sections.map { it.toDomain() }
)

@JsonClass(generateAdapter = true)
data class SeasonResponse(
    val key: String,
    val label: String,
    val position: String,
    @param:Json(name = "is_default")
    val isDefault: Boolean,
    val total: Int,
    val posters: List<String>,
    val endpoint: String,
)

fun SeasonResponse.toDomain() = Season(
    key = key,
    label = label,
    position = position,
    isDefault = isDefault,
    total = total,
    posters = posters,
    endpoint = endpoint,
)

@JsonClass(generateAdapter = true)
data class SectionResponse(
    val key: String,
    val title: String,
    val subtitle: String,
    val total: Int,
    val posters: List<String>,
    val endpoint: String,
)

fun SectionResponse.toDomain() = Section(
    key = key,
    title = title,
    subtitle = subtitle,
    total = total,
    posters = posters,
    endpoint = endpoint,
)