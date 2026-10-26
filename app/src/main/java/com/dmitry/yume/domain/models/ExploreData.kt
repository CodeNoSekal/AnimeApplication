package com.dmitry.yume.domain.models

data class ExploreData(
    val updated: String,
    val seasons: List<Season>,
    val sections: List<Section>,
)

data class Season(
    val key: String,
    val label: String,
    val position: String,
    val isDefault: Boolean,
    val total: Int,
    val posters: List<String>,
    val endpoint: String,
)

data class Section(
    val key: String,
    val title: String,
    val subtitle: String,
    val total: Int,
    val posters: List<String>,
    val endpoint: String,
)