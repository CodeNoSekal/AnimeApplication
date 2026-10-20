package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.ExploreModel
import com.dmitry.yume.domain.models.SeasonModel
import com.dmitry.yume.domain.models.SectionModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ExploreResponse(
    @param:Json(name = "updated_on")
    val updated: String,
    val seasons: List<SeasonResponse>,
    val sections: List<SectionResponse>,
)

fun ExploreResponse.toDomain() = ExploreModel(
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

fun SeasonResponse.toDomain() = SeasonModel(
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

fun SectionResponse.toDomain() = SectionModel(
    key = key,
    title = title,
    subtitle = subtitle,
    total = total,
    posters = posters,
    endpoint = endpoint,
)