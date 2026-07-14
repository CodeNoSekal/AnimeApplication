package com.dmitry.test.animeapplication.data.api

import com.dmitry.test.animeapplication.data.response.AnimeDetailResponse
import com.dmitry.test.animeapplication.data.response.AnimeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Target(AnnotationTarget.FUNCTION)
annotation class NoAuth


interface AnimeApi {

    @GET("anime")
    suspend fun getAnimeList(
        @Query("page")
        page: Int,
        @Query("per_page")
        perPage: Int = 50,
        @Query("sort")
        sort: String = "rating",
        @Query("q")
        q: String? = null
    ): AnimeResponse

    @GET("anime/{id}")
    suspend fun getAnimeById(
        @Path("id") id: Int
    ): AnimeDetailResponse
}