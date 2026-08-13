package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.Episode
import com.dmitry.yume.domain.models.PlayerData
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.Quality
import com.dmitry.yume.domain.models.Source
import com.dmitry.yume.domain.models.Voiceover
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackTest {

    @Test
    fun `returns error when there are no available episodes`() {
        val result = resolvePlayback(
            playerData(episode(isAvailable = false))
        )

        assertError(result, "Нет доступных эпизодов")
    }

    @Test
    fun `returns error when selected episode has no sources`() {
        val result = resolvePlayback(
            playerData(episode(sources = emptyList()))
        )

        assertError(result, "Для серии нет доступных источников")
    }

    @Test
    fun `returns error when every source has no voiceovers`() {
        val result = resolvePlayback(
            playerData(
                episode(
                    sources = listOf(
                        Source(Provider.Libria, emptyList()),
                        Source(Provider.Kodik, emptyList())
                    )
                )
            )
        )

        assertError(result, "Для серии нет доступных озвучек")
    }

    @Test
    fun `falls back to another source when preferred source has no playable video`() {
        val result = resolvePlayback(
            playerData(
                episode(
                    sources = listOf(
                        Source(
                            provider = Provider.Libria,
                            voiceovers = listOf(voiceover(id = 1))
                        ),
                        Source(
                            provider = Provider.Kodik,
                            voiceovers = listOf(
                                voiceover(
                                    id = 2,
                                    hls720 = "https://example.test/episode.m3u8"
                                )
                            )
                        )
                    )
                )
            ),
            PreferredPlayback(
                episodeNumber = 1,
                sourceProvider = Provider.Libria,
                voiceoverId = 1,
                quality = Quality.FHD,
                positionMs = 12_000L
            )
        )

        val resolved = result as PlaybackResolution.Success
        assertEquals(Provider.Kodik, resolved.sourceProvider)
        assertEquals(2, resolved.voiceoverId)
        assertEquals(Quality.HD, resolved.quality)
        assertEquals("https://example.test/episode.m3u8", resolved.url)
        assertEquals(12_000L, resolved.positionMs)
    }

    @Test
    fun `falls back to lower quality and reports the actual selected quality`() {
        val result = resolvePlayback(
            playerData(
                episode(
                    sources = listOf(
                        Source(
                            provider = Provider.Libria,
                            voiceovers = listOf(
                                voiceover(
                                    id = 1,
                                    hls480 = "https://example.test/480.m3u8"
                                )
                            )
                        )
                    )
                )
            ),
            PreferredPlayback(
                episodeNumber = 1,
                sourceProvider = Provider.Libria,
                voiceoverId = 1,
                quality = Quality.FHD,
                positionMs = 0L
            )
        )

        val resolved = result as PlaybackResolution.Success
        assertEquals(Quality.SD, resolved.quality)
        assertEquals("https://example.test/480.m3u8", resolved.url)
    }

    @Test
    fun `resets position when saved episode is no longer available`() {
        val result = resolvePlayback(
            playerData(
                episode(id = 1, isAvailable = false),
                episode(
                    id = 2,
                    sources = listOf(
                        Source(
                            Provider.Libria,
                            listOf(voiceover(id = 2, url = "https://example.test/video"))
                        )
                    )
                )
            ),
            PreferredPlayback(
                episodeNumber = 1,
                sourceProvider = Provider.Libria,
                voiceoverId = 1,
                quality = Quality.FHD,
                positionMs = 25_000L
            )
        )

        val resolved = result as PlaybackResolution.Success
        assertEquals(2, resolved.episodeNumber)
        assertEquals(0L, resolved.positionMs)
    }

    @Test
    fun `episode navigation follows actual sparse list order`() {
        val data = playerData(
            episode(id = 1),
            episode(id = 4),
            episode(id = 9)
        )

        val navigation = data.navigationFor(selectedEpisodeNumber = 4)

        assertEquals(1, navigation.previousEpisodeId)
        assertEquals(9, navigation.nextEpisodeId)
    }

    private fun assertError(result: PlaybackResolution, message: String) {
        assertTrue(result is PlaybackResolution.Error)
        assertEquals(message, (result as PlaybackResolution.Error).message)
    }

    private fun playerData(vararg episodes: Episode) = PlayerData(
        id = 10,
        title = "Test",
        episodesTotal = episodes.size,
        episodesAvailable = episodes.count { it.isAvailable },
        kodik = true,
        libria = true,
        episodes = episodes.toList()
    )

    private fun episode(
        id: Int = 1,
        isAvailable: Boolean = true,
        sources: List<Source> = listOf(
            Source(
                Provider.Libria,
                listOf(voiceover(id = 1, url = "https://example.test/video"))
            )
        )
    ) = Episode(
        id = id,
        title = null,
        airDate = "",
        isAvailable = isAvailable,
        sources = sources
    )

    private fun voiceover(
        id: Int,
        url: String? = null,
        hls480: String? = null,
        hls720: String? = null,
        hls1080: String? = null
    ) = Voiceover(
        voiceoverId = id,
        url = url,
        type = "",
        voiceover = "",
        quality = Quality.Undefined,
        hls480 = hls480,
        hls720 = hls720,
        hls1080 = hls1080
    )
}
