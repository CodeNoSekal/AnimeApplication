package com.dmitry.test.animeapplication.presentation.screens.collections

enum class CollectionTab(val title: String, val status: String?){
    FAVORITES("Избранное", null),
    WATCHING("Смотрю", "смотрю"),
    PLANNED("В планах", "в планах"),
    COMPLETED("Просмотрено", "просмотрено"),
    DROPPED("Брошено", "брошено"),
}