package com.dmitry.yume.data.api

import com.dmitry.yume.data.response.HomeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MetaApi {

    @GET("home")
    suspend fun getHome(
        @Query("limit")
        limit: Int = 10,
    ): HomeResponse
}