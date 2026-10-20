package com.dmitry.yume.domain.models

data class HomeData(
    val updated: String,
    val hero: List<Anime>,
    val ranking: List<RankedAnime>
)
