package com.dmitry.test.animeapplication.data

import com.dmitry.test.animeapplication.data.response.AnimeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeApi {

    @GET("/anime")
    suspend fun getAnimeCatalog(
        @Query("page")
        page: Int,
        @Query("per_page")
        perPage: Int = 50,
        @Query("sort")
        sort: String = "rating"
    ): AnimeResponse
}