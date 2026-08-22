package com.dmitry.yume.domain.models

data class PlaybackCatalog(
    val animeId: Int,
    val title: String?,
    val episodesTotal: Int,
    val episodesAvailable: Int,
    val hasKodik: Boolean,
    val hasLibria: Boolean,
    val episodes: List<PlaybackEpisode>
)

data class PlaybackEpisode(
    val number: Int,
    val title: String?,
    val airDate: String,
    val isAvailable: Boolean,
    val sources: List<PlaybackSource>
)

data class PlaybackSource(
    val provider: Provider,
    val voiceovers: List<Voiceover>
)

data class Voiceover(
    val id: Int,
    val name: String,
    val maxQuality: VideoQuality,
    val logoUrl: String,
)

sealed class Provider {
    object Libria : Provider()
    object Liberty : Provider()
    object Kodik : Provider()
    data class Unknown(
        val rawName: String
    ) : Provider()

    fun toRaw(): String = when (this) {
        Kodik -> "kodik"
        Libria -> "libria"
        Liberty -> "liberty"
        is Unknown -> rawName
    }

    companion object {
        fun getProvider(raw: String): Provider {
            return when (raw.lowercase()) {
                "kodik" -> Kodik
                "libria" -> Libria
                "liberty" -> Liberty
                else -> Unknown(raw)
            }
        }
    }
}


sealed class VideoQuality {
    object FHD : VideoQuality()
    object HD : VideoQuality()
    object SD : VideoQuality()
    object NHD : VideoQuality()
    object Unknown : VideoQuality()

    companion object {
        fun fromRaw(raw: String?): VideoQuality {
            return when {
                raw?.contains("1080") == true -> FHD
                raw?.contains("720") == true -> HD
                raw?.contains("480") == true -> SD
                raw?.contains("360") == true -> NHD
                else -> Unknown
            }
        }
    }
}
