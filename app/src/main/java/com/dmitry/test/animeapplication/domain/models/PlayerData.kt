package com.dmitry.test.animeapplication.domain.models

data class PlayerData(
    val id: Int,
    val title: String?,
    val episodesTotal: Int,
    val episodesAvailable: Int,
    val kodik: Boolean,
    val anilibria: Boolean,
    val episodes: List<Episode>
)

fun PlayerData.getEpisode(target: Int): Episode{
    return episodes.find {it.id == target} ?: episodes.first()
}

data class Episode(
    val id: Int,
    val title: String?,
    val airDate: String,
    val isAvailable: Boolean,
    val sources: List<Source>
)

fun Episode.getSource(target: Provider): Source{
    return sources.find {it.provider == target} ?: sources.first()
}

data class Source(
    val provider: Provider,
    val voiceovers: List<Voiceover>
)

fun Source.getVoiceover(target: String): Voiceover{
    return voiceovers.find {it.voiceover == target} ?: voiceovers.first()
}

data class Voiceover(
    val url: String?,
    val type: String,
    val voiceover: String,
    val quality: Quality,
    val hls480: String?,
    val hls720: String?,
    val hls1080: String?
)

fun Voiceover.hlsByQuality(q: Quality): String? {
    return when (q) {
        Quality.FHD -> hls1080
        Quality.HD -> hls720
        Quality.SD -> hls480
        Quality.Undefined -> hls1080 ?: hls720 ?: hls480 ?: url
    }
}

sealed class Provider {
    object Anilibria : Provider()
    object Kodik : Provider()
    object Undefined : Provider()

    companion object {
        fun getProvider(raw: String): Provider {
            return when (raw.lowercase()) {
                "kodik" -> Kodik
                "anilibria" -> Anilibria
                else -> Undefined
            }
        }
    }
}

sealed class Quality {
    object FHD : Quality()
    object HD : Quality()
    object SD : Quality()
    object Undefined : Quality()

    companion object {
        fun getQuality(raw: String): Quality {
            return when {
                raw.contains("1080") -> FHD
                raw.contains("720") -> HD
                raw.contains("480") -> SD
                else -> Undefined
            }
        }
    }
}
