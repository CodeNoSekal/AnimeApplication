package com.dmitry.yume.presentation.screens.detail

import com.dmitry.yume.domain.models.AnimeDetailed
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val russian = Locale.forLanguageTag("ru")
private val utc = TimeZone.getTimeZone("UTC")

private fun parseDate(value: String, pattern: String): Date? {
    val formatter = SimpleDateFormat(pattern, Locale.US).apply {
        isLenient = false
        timeZone = utc
    }
    val position = ParsePosition(0)
    return formatter.parse(value, position)?.takeIf { position.index == value.length }
}

fun formatAnimeDate(value: String?, precision: String?): String? {
    if (value == null) return null
    val date = parseDate(value, "yyyy-MM-dd") ?: return null
    // The API may encode an approximate year/month using a placeholder day.
    val pattern = when (precision) {
        "day" -> "d MMMM yyyy"
        "month" -> "LLLL yyyy"
        else -> "yyyy"
    }
    return SimpleDateFormat(pattern, russian).apply { timeZone = utc }.format(date)
}

fun formatNextEpisode(value: String?, nowMillis: Long = System.currentTimeMillis()): String? {
    if (value == null) return null
    val date = parseDate(value, "yyyy-MM-dd'T'HH:mm:ssXXX")
        ?: parseDate(value, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") ?: return null
    if (date.time <= nowMillis) return null
    return SimpleDateFormat("d MMMM, HH:mm", russian).format(date)
}

fun detailPlaybackLabel(anime: AnimeDetailed): String {
    if (anime.isAvailable) {
        if (anime.kind == "movie") return if (anime.lastEpisodeNumber != null && anime.lastEpisodeNumber > 0) "Продолжить" else "Смотреть"
        return anime.lastEpisodeNumber?.takeIf { it > 0 }?.let { "Продолжить · $it серия" } ?: "Смотреть"
    }
    if (anime.releaseStatus == "anons") {
        return formatAnimeDate(anime.airedOn, anime.airedOnPrecision)?.let { "Премьера · $it" }
            ?: "Дата премьеры не объявлена"
    }
    return "Пока недоступно"
}

fun detailEpisodeLabel(anime: AnimeDetailed): String? {
    if (anime.kind == "movie") return null
    val total = anime.episodesTotal?.takeIf { it > 0 }
    val available = anime.episodesAvailable?.takeIf { it >= 0 }
    return when {
        available != null && total != null && available < total -> "$available из $total"
        available != null && available > 0 -> available.toString()
        total != null -> total.toString()
        available == 0 -> "Пока нет доступных серий"
        else -> null
    }
}
