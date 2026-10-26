package com.dmitry.yume.data.api

import com.dmitry.yume.data.request.FavoriteRequest
import com.dmitry.yume.data.request.ProgressRequest
import com.dmitry.yume.data.request.ReviewRequest
import com.dmitry.yume.data.request.ScoreRequest
import com.dmitry.yume.data.request.StatusRequest
import com.dmitry.yume.data.response.AnimeResponse
import com.dmitry.yume.data.response.EpisodeProgressResponse
import com.dmitry.yume.data.response.ProgressItemResponse
import com.dmitry.yume.data.response.ProgressResponse
import com.dmitry.yume.data.response.ProfileResponse
import com.dmitry.yume.data.response.StatusResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MeApi {
    @GET("me/profile")
    suspend fun getProfile(): ProfileResponse

    @PUT("me/progress")
    suspend fun putProgress(
        @Body progressRequest: ProgressRequest
    )

    @GET("me/continue")
    suspend fun getProgress(): ProgressResponse

    @GET("me/continue/{id}")
    suspend fun getProgressById(
        @Path("id") id: Int
    ): ProgressItemResponse

    @DELETE("me/continue/{id}")
    suspend fun deleteFromContinue(
        @Path("id") id: Int
    )

    @GET("me/anime/{id}/progress")
    suspend fun getEpisodeProgress(
        @Path("id") id: Int
    ): EpisodeProgressResponse

    @DELETE("me/anime/{id}/progress/{episode}")
    suspend fun deleteEpisodeProgress(
        @Path("id") id: Int,
        @Path("episode") episode: Int,
    )

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

    @GET("me/anime/favorite")
    suspend fun getAnimeListByFavorite(
        @Query("page")
        page: Int,
        @Query("per_page")
        perPage: Int = 50,
        @Query("q")
        q: String? = null,
    ): AnimeResponse
}
