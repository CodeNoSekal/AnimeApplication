package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.LibraryStatusItem
import com.dmitry.yume.domain.models.Profile
import com.dmitry.yume.domain.models.ProfileLibrary
import com.dmitry.yume.domain.models.ProfileUser
import com.dmitry.yume.domain.models.ProfileWatch
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileResponse(
    val user: ProfileUserResponse,
    val watch: ProfileWatchResponse,
    val library: ProfileLibraryResponse,
    val ratings: ProfileRatingResponse,
    val reviews: ProfileReviewResponse,
    @param:Json(name = "top_genres")
    val topGenres: List<MyGenreResponse>? = null,
)

@JsonClass(generateAdapter = true)
data class ProfileUserResponse(
    val id: Int,
    val email: String,
    @param:Json(name = "email_verified")
    val emailVerified: Boolean,
    val username: String,
    @param:Json(name = "display_name")
    val displayName: String? = null,
    @param:Json(name = "avatar_url")
    val avatar: String,
    @param:Json(name = "is_premium")
    val isPremium: Boolean = false,
    @param:Json(name = "premium_until")
    val premiumUntil: String? = null,
    @param:Json(name = "member_for_text")
    val memberFor: String,
)

@JsonClass(generateAdapter = true)
data class ProfileWatchResponse(
    @param:Json(name = "watched_hours")
    val watchedHours: Int,
    @param:Json(name = "last_watched_text")
    val lastWatchedText: String,
)

@JsonClass(generateAdapter = true)
data class ProfileLibraryResponse(
    @param:Json(name = "with_status")
    val total: Int,
    val watching: Int,
    val planned: Int,
    val completed: Int,
    val dropped: Int,
    val lists: List<LibraryStatusItemResponse>,
)

@JsonClass(generateAdapter = true)
data class LibraryStatusItemResponse(
    val key: String,
    val title: String,
    val count: Int = 0,
)

@JsonClass(generateAdapter = true)
data class ProfileRatingResponse(
    val count: Int,
    val average: Double? = null,
)


@JsonClass(generateAdapter = true)
data class ProfileReviewResponse(val count: Int = 0)

@JsonClass(generateAdapter = true)
data class MyGenreResponse(
    val name: String,
    val count: Int,
)

fun ProfileResponse.toDomain() = Profile(
    user = ProfileUser(
        id = user.id,
        email = user.email,
        emailVerified = user.emailVerified,
        username = user.username,
        displayName = user.displayName,
        avatar = user.avatar,
        isPremium = user.isPremium,
        premiumUntil = user.premiumUntil,
        memberFor = user.memberFor
    ),
    watch = ProfileWatch(
        watchedHours = watch.watchedHours,
        lastWatchedText = watch.lastWatchedText
    ),
    library = ProfileLibrary(
        total = library.total,
        watching = library.watching,
        planned = library.planned,
        completed = library.completed,
        dropped = library.dropped,
        lists = library.lists.map { LibraryStatusItem(it.title, it.key, it.count) },
    ),
)
