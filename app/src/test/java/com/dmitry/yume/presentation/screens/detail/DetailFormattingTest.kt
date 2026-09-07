package com.dmitry.yume.presentation.screens.detail

import com.dmitry.yume.domain.models.AnimeDetailed
import org.junit.Assert.*
import org.junit.Test
import java.util.TimeZone

class DetailFormattingTest {
    private val anime = AnimeDetailed(
        1, "Title", null, null, 2027, null,
        isAvailable = false, description = null, duration = null, genres = null, studios = null,
        relations = emptyList(), releaseStatus = "anons", airedOn = "2027-10-01"
    )

    @Test fun `premiere respects day month and year precision`() {
        assertEquals("Премьера · 1 октября 2027", detailPlaybackLabel(anime.copy(airedOnPrecision = "day")))
        assertEquals("Премьера · октябрь 2027", detailPlaybackLabel(anime.copy(airedOnPrecision = "month")))
        assertEquals("Премьера · 2027", detailPlaybackLabel(anime.copy(airedOnPrecision = "year")))
        assertEquals("Премьера · 2027", detailPlaybackLabel(anime))
    }

    @Test fun `invalid or missing premiere never fabricates a date`() {
        for (value in listOf(null, "", "bad", "2027-02-30")) {
            assertNull(formatAnimeDate(value, "day"))
            assertEquals("Дата премьеры не объявлена", detailPlaybackLabel(anime.copy(airedOn = value)))
        }
    }

    @Test fun `availability takes precedence and unavailable released title is not coming soon`() {
        assertEquals("Смотреть", detailPlaybackLabel(anime.copy(isAvailable = true)))
        assertEquals("Продолжить · 5 серия", detailPlaybackLabel(anime.copy(isAvailable = true, lastEpisodeNumber = 5)))
        assertEquals("Пока недоступно", detailPlaybackLabel(anime.copy(releaseStatus = "released")))
    }

    @Test fun `episode totals distinguish unknown and zero`() {
        assertNull(detailEpisodeLabel(anime))
        assertEquals("2 из 12", detailEpisodeLabel(anime.copy(episodesAvailable = 2, episodesTotal = 12)))
        assertEquals("12", detailEpisodeLabel(anime.copy(episodesTotal = 12)))
        assertEquals("Пока нет доступных серий", detailEpisodeLabel(anime.copy(episodesAvailable = 0)))
    }

    @Test fun `complete series omit redundant fraction and unknown total stays numeric`() {
        assertEquals("12", detailEpisodeLabel(anime.copy(episodesAvailable = 12, episodesTotal = 12)))
        assertEquals("13", detailEpisodeLabel(anime.copy(episodesAvailable = 13, episodesTotal = 12)))
        assertEquals("2", detailEpisodeLabel(anime.copy(episodesAvailable = 2)))
        assertEquals("0 из 12", detailEpisodeLabel(anime.copy(episodesAvailable = 0, episodesTotal = 12)))
    }

    @Test fun `movie never displays an episode count or episode number on playback`() {
        val movie = anime.copy(kind = "movie", isAvailable = true, episodesTotal = 1, episodesAvailable = 1)
        assertNull(detailEpisodeLabel(movie))
        assertEquals("Смотреть", detailPlaybackLabel(movie))
        assertEquals("Продолжить", detailPlaybackLabel(movie.copy(lastEpisodeNumber = 1)))
        assertEquals("Премьера · 2027", detailPlaybackLabel(movie.copy(isAvailable = false)))
    }

    @Test fun `next episode hides stale or malformed dates`() {
        assertNull(formatNextEpisode("2020-01-01T12:00:00Z", nowMillis = 2_000_000_000_000))
        assertNull(formatNextEpisode("not a date", nowMillis = 0))
        assertNotNull(formatNextEpisode("2027-10-01T12:00:00+03:00", nowMillis = 0))
    }

    @Test fun `next episode accepts server format and displays device local time`() {
        val previousTimeZone = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Yekaterinburg"))
            assertEquals(
                "6 сентября, 20:00",
                formatNextEpisode("2026-09-06 18:00:00+03:00", nowMillis = 0),
            )
        } finally {
            TimeZone.setDefault(previousTimeZone)
        }
    }
}
