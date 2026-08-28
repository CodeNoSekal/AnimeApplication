package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.Provider
import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class ResponseParsingTest {
    private val moshi = Moshi.Builder().build()

    @Test fun `details map release facts separately from personal status and prefer modern rating`() {
        val domain = moshi.adapter(AnimeDetailResponse::class.java).fromJson("""
            {"shikimori_id":1,"kind":"tv","status":"anons","my_status":"в планах",
             "rating":8.7,"shikimori_rating":8.5,"rating_source":"internal",
             "episodes_total":12,"episodes_available":0,"rating_mpaa":"pg_13",
             "aired_on":"2027-10-01","aired_on_precision":"month",
             "released_on":"2027-12-31","released_on_precision":"year",
             "next_episode_at":"2027-10-01T12:00:00Z","last_episode_number":3}
        """.trimIndent())!!.toDomain()
        assertEquals("anons", domain.releaseStatus)
        assertEquals("в планах", domain.status)
        assertEquals("tv", domain.kind)
        assertEquals(8.7, domain.rating!!, 0.001)
        assertEquals("internal", domain.ratingSource)
        assertEquals("month", domain.airedOnPrecision)
        assertEquals("2027-10-01", domain.airedOn)
        assertEquals("year", domain.releasedOnPrecision)
        assertEquals(12, domain.episodesTotal)
        assertEquals(0, domain.episodesAvailable)
        assertEquals("pg_13", domain.ageRating)
        assertEquals(3, domain.lastEpisodeNumber)
    }

    @Test fun `score deletion serializes explicit null with application converter`() {
        val retrofit = com.dmitry.yume.di.NetworkModule.provideRetrofit(okhttp3.OkHttpClient(), moshi)
        val converter = retrofit.requestBodyConverter<com.dmitry.yume.data.request.ScoreRequest>(
            com.dmitry.yume.data.request.ScoreRequest::class.java, emptyArray(), emptyArray()
        )
        val buffer = okio.Buffer()
        converter.convert(com.dmitry.yume.data.request.ScoreRequest(null))!!.writeTo(buffer)
        assertEquals("{\"score\":null}", buffer.readUtf8())
    }

    @Test
    fun `anime list item allows missing personalized fields`() {
        val json = """
            {
              "shikimori_id": 1,
              "title": "Title",
              "title_en": null,
              "poster_url": null,
              "year": 2026,
              "shikimori_rating": 8.5,
              "kind": "tv"
            }
        """.trimIndent()

        val response = moshi.adapter(AnimeShort::class.java).fromJson(json)!!

        assertFalse(response.favorite)
        assertNull(response.myStatus)
        assertNull(response.myScore)
    }

    @Test
    fun `nullable anime fields are optional rather than required`() {
        val response = moshi.adapter(AnimeShort::class.java)
            .fromJson("""{"shikimori_id":1}""")!!

        assertNull(response.title)
        assertNull(response.titleEn)
        assertNull(response.posterUrl)
        assertNull(response.year)
        assertNull(response.rating)
        assertNull(response.kind)
        assertNull(response.myStatus)
        assertNull(response.myScore)
    }

    @Test
    fun `anime details preserve playback and personalized fields`() {
        val json = """
            {
              "shikimori_id": 1,
              "title": "Title",
              "title_en": null,
              "poster_full": null,
              "year": 2026,
              "shikimori_rating": 8.5,
              "kind": "tv",
              "has_kodik": true,
              "has_libria": false,
              "my_status": "watching",
              "favorite": true,
              "my_score": 9
            }
        """.trimIndent()

        val domain = moshi.adapter(AnimeDetailResponse::class.java)
            .fromJson(json)!!
            .toDomain()

        assertEquals(true, domain.hasKodik)
        assertEquals(false, domain.hasLibria)
        assertEquals("watching", domain.status)
        assertEquals(true, domain.favorite)
        assertEquals(9, domain.score)
    }

    @Test
    fun `progress allows missing playback preferences`() {
        val json = """
            {
              "shikimori_id": 1,
              "title": "Title",
              "poster_thumb": null,
              "episode_number": 2,
              "position_ms": 1000,
              "duration_ms": 2000,
              "is_completed": false
            }
        """.trimIndent()

        val domain = moshi.adapter(ProgressItem::class.java)
            .fromJson(json)!!
            .toDomain()

        assertNull(domain.sourceProvider)
        assertNull(domain.voiceoverId)
    }

    @Test
    fun `nullable progress presentation fields are optional`() {
        val json = """
            {
              "shikimori_id": 1,
              "episode_number": 2,
              "position_ms": 1000,
              "duration_ms": 2000,
              "is_completed": false
            }
        """.trimIndent()

        val response = moshi.adapter(ProgressItem::class.java).fromJson(json)!!

        assertNull(response.title)
        assertNull(response.posterUrl)
    }

    @Test
    fun `unknown progress provider maps to undefined`() {
        val item = ProgressItem(
            id = 1,
            title = null,
            posterUrl = null,
            episodeNumber = 1,
            positionMs = 0,
            durationMs = 1,
            isCompleted = false,
            sourceProvider = "new-provider",
            voiceoverId = null
        )

        assertEquals(Provider.Unknown("new-provider"), item.toDomain().sourceProvider)
    }
}
