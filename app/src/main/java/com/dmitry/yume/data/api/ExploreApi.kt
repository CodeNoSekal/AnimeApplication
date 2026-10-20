package com.dmitry.yume.data.api

import com.dmitry.yume.data.response.AnimeResponse
import com.dmitry.yume.data.response.ExploreResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ExploreApi {

    @GET ("/explore")
    suspend fun getExplore(): ExploreResponse

    @GET
    suspend fun getCollection(
        @Url endpoint: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30,
    ): AnimeResponse
}