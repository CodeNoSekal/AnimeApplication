package com.dmitry.yume.domain.models

data class ExploreModel(
    val updated: String,
    val seasons: List<SeasonModel>,
    val sections: List<SectionModel>,
)

data class SeasonModel(
    val key: String,
    val label: String,
    val position: String,
    val isDefault: Boolean,
    val total: Int,
    val posters: List<String>,
    val endpoint: String,
)

data class SectionModel(
    val key: String,
    val title: String,
    val subtitle: String,
    val total: Int,
    val posters: List<String>,
    val endpoint: String,
)