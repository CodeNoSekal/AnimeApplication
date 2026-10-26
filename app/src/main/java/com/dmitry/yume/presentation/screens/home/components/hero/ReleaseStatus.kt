package com.dmitry.yume.presentation.screens.home.components.hero

enum class ReleaseStatus(
    val raw: String,
    val title: String,
){
    Anons("anons", "Анонс"),
    Ongoing("ongoing", "Онгоинг"),
    Released("released", "Вышел"),
}
