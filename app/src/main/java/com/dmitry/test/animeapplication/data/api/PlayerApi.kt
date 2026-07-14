package com.dmitry.test.animeapplication.data.api

import com.dmitry.test.animeapplication.data.response.PlayerResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface PlayerApi {
    @GET("player/{id}")
    suspend fun getPlayerById(
        @Path("id") id: Int
    ): PlayerResponse
}