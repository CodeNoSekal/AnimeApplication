package com.dmitry.yume.presentation.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Аниме-сезоны и их цвета (раздел «Поиск», карусель сезонов).
 * Сезон определяется по месяцу выхода первой серии:
 * 1–3 зима, 4–6 весна, 7–9 лето, 10–12 осень.
 */
enum class AnimeSeason(
    val title: String,      // «Осень»
    val months: String,     // «октябрь — декабрь»
    val firstMonth: Int,    // 10
    val apiKey: String,     // ключ для запроса по сезону: "fall" → "fall_2026"
) {
    Winter("Зима", "январь — март", 1, "winter"),
    Spring("Весна", "апрель — июнь", 4, "spring"),
    Summer("Лето", "июль — сентябрь", 7, "summer"),
    Autumn("Осень", "октябрь — декабрь", 10, "fall");

    companion object {
        fun of(month: Int): AnimeSeason = entries[(month - 1) / 3]
    }
}

/** Конкретный сезон конкретного года: «Осень 2026». */
@Immutable
data class SeasonRef(val season: AnimeSeason, val year: Int) {
    val label: String get() = "${season.title} $year"
    val apiValue: String get() = "${season.apiKey}_$year"
    val start: LocalDate get() = LocalDate.of(year, season.firstMonth, 1)
    val end: LocalDate get() = start.plusMonths(3).minusDays(1)

    fun next(): SeasonRef = if (season == AnimeSeason.Autumn) SeasonRef(AnimeSeason.Winter, year + 1)
    else SeasonRef(AnimeSeason.entries[season.ordinal + 1], year)

    fun prev(): SeasonRef = if (season == AnimeSeason.Winter) SeasonRef(AnimeSeason.Autumn, year - 1)
    else SeasonRef(AnimeSeason.entries[season.ordinal - 1], year)

    fun daysUntilStart(today: LocalDate = LocalDate.now()): Long = ChronoUnit.DAYS.between(today, start)

    companion object {
        fun current(today: LocalDate = LocalDate.now()) = SeasonRef(AnimeSeason.of(today.monthValue), today.year)
    }
}

/**
 * Токены цвета сезона.
 * fill    — заливка карточки сезона и точка карусели;
 * onFill  — текст и контур цифр на заливке (всегда тёмный: белый на этих цветах не читается);
 * deep    — нажатие / тень;
 * onDark  — цвет сезона для текста на тёмном фоне (подписи плиток);
 * soft    — тонировка поверх постера на плитке;
 * line    — обводка.
 */
@Immutable
data class SeasonColors(
    val fill: Color,
    val onFill: Color,
    val deep: Color,
    val onDark: Color,
) {
    val soft: Color get() = fill.copy(alpha = 0.16f)
    val line: Color get() = fill.copy(alpha = 0.35f)
    val tileTint: Color get() = fill.copy(alpha = 0.42f)
}

val WinterColors = SeasonColors(
    fill = Color(0xFF8CC8FF),
    onFill = Color(0xFF0B2238),
    deep = Color(0xFF4A8FD9),
    onDark = Color(0xFFB5DCFF),
)

val SpringColors = SeasonColors(
    fill = Color(0xFFFF9CC8),
    onFill = Color(0xFF3D0F25),
    deep = Color(0xFFE0609C),
    onDark = Color(0xFFFFC2DD),
)

val SummerColors = SeasonColors(
    fill = Color(0xFF9BE15D),
    onFill = Color(0xFF183108),
    deep = Color(0xFF5FAF2A),
    onDark = Color(0xFFC3EF9C),
)

val AutumnColors = SeasonColors(
    fill = Color(0xFFFF8A5B),
    onFill = Color(0xFF3A1406),
    deep = Color(0xFFE0582A),
    onDark = Color(0xFFFFB599),
)

val AnimeSeason.colors: SeasonColors
    get() = when (this) {
        AnimeSeason.Winter -> WinterColors
        AnimeSeason.Spring -> SpringColors
        AnimeSeason.Summer -> SummerColors
        AnimeSeason.Autumn -> AutumnColors
    }

/**
 * Цвета плиток-подборок под каруселью.
 * Онгоинги — фирменный фиолетовый, анонсы — цвет следующего сезона,
 * «вышли» — цвет прошлого сезона, классика — золото рейтинга.
 */
object ExplorationTileTint {
    val ongoing = YuAccent.copy(alpha = 0.5f)
    val classic = YuGold.copy(alpha = 0.42f)
    fun announcements(now: SeasonRef = SeasonRef.current()) = now.next().season.colors.tileTint
    fun released(now: SeasonRef = SeasonRef.current()) = now.prev().season.colors.tileTint
}
