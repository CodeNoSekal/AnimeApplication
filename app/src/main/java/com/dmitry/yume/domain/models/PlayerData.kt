package com.dmitry.yume.domain.models

data class PlayerData(
    val id: Int,
    val title: String?,
    val episodesTotal: Int,
    val episodesAvailable: Int,
    val kodik: Boolean,
    val libria: Boolean,
    val episodes: List<Episode>
)

data class Episode(
    val id: Int,
    val title: String?,
    val airDate: String,
    val isAvailable: Boolean,
    val sources: List<Source>
)

data class Source(
    val provider: Provider,
    val voiceovers: List<Voiceover>
)

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
