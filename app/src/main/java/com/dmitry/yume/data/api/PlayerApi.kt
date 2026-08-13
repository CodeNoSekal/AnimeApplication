package com.dmitry.yume.data.api

import com.dmitry.yume.data.response.PlayerResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface PlayerApi {
    @GET("player/{id}")
    suspend fun getPlayerById(
        @Path("id") id: Int
    ): PlayerResponse
}