package com.dmitry.test.animeapplication.domain.models

data class PlayerData(
    val id: Int,
    val title: String?,
    val episodesTotal: Int,
    val episodesAvailable: Int,
    val kodik: Boolean,
    val libria: Boolean,
    val episodes: List<Episode>
)

fun PlayerData.getEpisode(target: Int?): Episode?{
    return episodes.find { it.id == target } ?: getAvailableEpisode()
}


fun PlayerData.getAvailableEpisode(): Episode?{
    episodes.forEach {
        if (it.isAvailable) {
            return it
        }
    }

    return null
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

fun Source.getVoiceover(target: Int?): Voiceover{
    return voiceovers.find {it.voiceoverId == target} ?: voiceovers.first()
}

data class Voiceover(
    val voiceoverId: Int,
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
    object Libria : Provider()
    object Liberty : Provider()
    object Kodik : Provider()
    object Undefined : Provider()

    fun toRaw(): String? = when (this) {
        Kodik -> "kodik"
        Libria -> "libria"
        Liberty -> "liberty"
        Undefined -> null
    }

    companion object {
        fun getProvider(raw: String): Provider {
            return when (raw.lowercase()) {
                "kodik" -> Kodik
                "libria" -> Libria
                "liberty" -> Liberty
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
