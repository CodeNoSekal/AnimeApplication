package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.ResolvedPlayback
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.VideoQuality
import com.dmitry.yume.domain.models.PlaybackSegments
import com.dmitry.yume.domain.models.PlaybackSegment
import com.dmitry.yume.domain.models.VideoStream
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResolvedPlaybackResponse(
    val provider: String,
    val animeId: Int,
    val episodeNumber: Int,
    val voiceoverId: Int,
    val streams: List<VideoStreamResponse>,
    val segments: PlaybackSegmentsResponse
)

@JsonClass(generateAdapter = true)
data class VideoStreamResponse(
    val quality: String,
    val url: String,
)

@JsonClass(generateAdapter = true)
data class PlaybackSegmentsResponse(
    val ad: List<PlaybackSegmentResponse> = listOf(),
    val skip: List<PlaybackSegmentResponse> = listOf()
)

@JsonClass(generateAdapter = true)
data class PlaybackSegmentResponse(
    val start: Double,
    val end: Double,
)


fun ResolvedPlaybackResponse.toDomain(): ResolvedPlayback {
    return ResolvedPlayback(
        Provider.getProvider(provider),
        animeId,
        episodeNumber,
        voiceoverId,
        streams.map { it.toDomain() },
        segments.toDomain()
    )
}

fun VideoStreamResponse.toDomain(): VideoStream {
    return VideoStream(
        VideoQuality.fromRaw(quality),
        url
    )
}

fun PlaybackSegmentsResponse.toDomain(): PlaybackSegments {
    return PlaybackSegments(
        ad.map { it.toDomain() },
        skip.map { it.toDomain() }
    )
}

fun PlaybackSegmentResponse.toDomain(): PlaybackSegment {
    return PlaybackSegment(
        start,
        end
    )
}
