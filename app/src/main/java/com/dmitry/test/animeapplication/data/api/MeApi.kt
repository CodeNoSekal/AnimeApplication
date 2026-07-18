package com.dmitry.test.animeapplication.data.api

import com.dmitry.test.animeapplication.data.request.FavoriteRequest
import com.dmitry.test.animeapplication.data.request.ProgressRequest
import com.dmitry.test.animeapplication.data.request.ReviewRequest
import com.dmitry.test.animeapplication.data.request.ScoreRequest
import com.dmitry.test.animeapplication.data.request.StatusRequest
import com.dmitry.test.animeapplication.data.response.AnimeResponse
import com.dmitry.test.animeapplication.data.response.ProgressItem
import com.dmitry.test.animeapplication.data.response.ProgressResponse
import com.dmitry.test.animeapplication.data.response.StatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MeApi {
    @PUT("me/progress")
    suspend fun putProgress(
        @Body progressRequest: ProgressRequest
    )

    @GET("me/continue")
    suspend fun getProgress(): ProgressResponse

    @GET("me/continue/{id}")
    suspend fun getProgressById(
        @Path("id") id: Int
    ): ProgressItem

    @GET("me/anime/{id}")
    suspend fun getStatus(
        @Path("id") id: Int
    ): StatusResponse

    @PUT("me/anime/{id}/status")
    suspend fun putStatus(
        @Path("id") id: Int,
        @Body statusRequest: StatusRequest
    ): StatusResponse

    @PUT("me/anime/{id}/favorite")
    suspend fun putFavorite(
        @Path("id") id: Int,
        @Body favoriteRequest: FavoriteRequest
    ): StatusResponse

    @PUT("me/anime/{id}/score")
    suspend fun putScore(
        @Path("id") id: Int,
        @Body scoreRequest: ScoreRequest
    ): StatusResponse

    @PUT("me/anime/{id}/review")
    suspend fun putReview(
        @Path("id") id: Int,
        @Body reviewRequest: ReviewRequest
    ): StatusResponse

    @GET("me/anime/status")
    suspend fun getAnimeListByStatus(
        @Query("page")
        page: Int,
        @Query("per_page")
        perPage: Int = 50,
        @Query("status")
        status: String,
        @Query("q")
        q: String? = null,
    ): AnimeResponse

    @GET("me/anime")
    suspend fun getAnimeListByFavourite(
        @Query("page")
        page: Int,
        @Query("per_page")
        perPage: Int = 50,
        @Query("favorite")
        favorite: Boolean,
        @Query("q")
        q: String? = null,
    ): AnimeResponse
}
