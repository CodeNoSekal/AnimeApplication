package com.dmitry.yume.data.response

import com.dmitry.yume.domain.models.ProfileListCount
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileListsResponse(
    val lists: List<ProfileListCountResponse>,
)

@JsonClass(generateAdapter = true)
data class ProfileListCountResponse(
    val key: String,
    val title: String,
    val count: Int,
)

fun ProfileListsResponse.toDomain() = lists.map {
    ProfileListCount(it.key, it.title, it.count)
}
