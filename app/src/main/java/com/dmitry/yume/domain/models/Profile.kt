package com.dmitry.yume.domain.models

data class Profile(
    val user: ProfileUser,
    val watch: ProfileWatch,
    val library: ProfileLibrary,
)

data class ProfileUser(
    val id: Int,
    val email: String,
    val emailVerified: Boolean,
    val username: String,
    val displayName: String?,
    val avatar: String,
    val isPremium: Boolean,
    val premiumUntil: String?,
    val memberFor: String,
)

data class ProfileWatch(
    val watchedHours: Int,
    val lastWatchedText: String,
)

data class ProfileLibrary(
    val total: Int,
    val watching: Int,
    val planned: Int,
    val completed: Int,
    val dropped: Int,
    val lists: List<LibraryStatusItem>,
)

data class LibraryStatusItem(
    val key: String,
    val title: String,
    val count: Int,
)
