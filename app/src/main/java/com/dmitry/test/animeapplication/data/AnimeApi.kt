package com.dmitry.test.animeapplication.data

import com.dmitry.test.animeapplication.data.response.AnimeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeApi {

    @GET("/mobile/anime")
    suspend fun getAnimeCatalog(
        @Query("page")
        page: Int,
        @Query("limit")
        limit: Int = 50
    ): AnimeResponse
}