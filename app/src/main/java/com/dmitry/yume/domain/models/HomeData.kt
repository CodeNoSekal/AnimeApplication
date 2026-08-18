package com.dmitry.yume.domain.models

data class Home(
    val hero: Anime,
    val rails: List<Rail>
)

data class Rail(
    val key: String,
    val title: String,
    val items: List<Anime>
)
