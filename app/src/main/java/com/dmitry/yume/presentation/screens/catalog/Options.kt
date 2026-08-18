package com.dmitry.yume.presentation.screens.catalog

data class Options(
    val status: Status? = null,
    val sort: Sort = Sort.Rating,
    val order: Order = Order.Desc
)

enum class Status{
    Anons,
    Ongoing,
    Released;

    fun toRaw(): String = when (this) {
        Anons -> "anons"
        Ongoing -> "ongoing"
        Released -> "released"
    }

    fun toUi(): String = when (this) {
        Anons -> "Анонс"
        Ongoing -> "Выходит"
        Released -> "Вышел"
    }
}

enum class Sort {
    Rating,
    Year,
    Updated,
    Title;

    fun toRaw(): String = when (this) {
        Rating -> "rating"
        Year -> "year"
        Updated -> "updated"
        Title -> "title"
    }

    fun toUi(): String = when (this) {
        Rating -> "Рейтинг"
        Year -> "Год"
        Updated -> "Обновление"
        Title -> "Название"
    }
}

enum class Order {
    Asc,
    Desc;

    fun toRaw(): String = when (this) {
        Asc -> "asc"
        Desc -> "desc"
    }
}
