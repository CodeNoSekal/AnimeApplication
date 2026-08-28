package com.dmitry.yume.domain.models

import com.dmitry.yume.presentation.screens.catalog.Status
import com.squareup.moshi.Json

data class Anime(
    val id: Int,
    val title: String?,
    val titleEn: String?,
    val posterUrl: String?,
    val year: Int?,
    val rating: Double?,
    val status: String? = null,
    val favorite: Boolean,

    val kind: String? = null,

    val myStatus: String? = null,
    val myScore: Int? = null,
)