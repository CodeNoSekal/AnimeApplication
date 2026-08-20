package com.dmitry.yume.domain.models

data class ResolvedPlayback(
    val sourceProvider: Provider,
    val animeId: Int,
    val episodeNumber: Int,
    val voiceoverId: Int,
    val streams: List<VideoStream>,
    val segments: PlaybackSegments
)

data class VideoStream(
    val quality: VideoQuality,
    val url: String,
)

data class PlaybackSegments(
    val ad: List<PlaybackSegment>,
    val skip: List<PlaybackSegment>
)

data class PlaybackSegment(
    val start: Double,
    val end: Double,
)
