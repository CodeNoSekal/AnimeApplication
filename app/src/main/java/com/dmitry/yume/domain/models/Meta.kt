package com.dmitry.yume.domain.models

data class Meta(
    val genres: List<Genre>,
    val yearFrom: Int,
    val yearTo: Int,
)

data class Genre(
    val id: Int,
    val name: String
)
