package com.dmitry.yume.data.api

import com.dmitry.yume.data.response.AnimeDetailResponse
import com.dmitry.yume.data.response.AnimeResponse
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
        @Query("status")
        status: String? = null,
        @Query("sort")
        sort: String = "rating",
        @Query("order")
        order: String = "desc",
        @Query("q")
        q: String? = null
    ): AnimeResponse

    @GET("anime/{id}")
    suspend fun getAnimeById(
        @Path("id") id: Int
    ): AnimeDetailResponse
}