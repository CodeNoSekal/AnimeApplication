package com.dmitry.yume.data.api

import com.dmitry.yume.data.request.ResolvePlaybackRequest
import com.dmitry.yume.data.response.PlaybackCatalogResponse
import com.dmitry.yume.data.response.ResolvedPlaybackResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface PlaybackApi {
    @GET("player/{id}")
    suspend fun getPlaybackCatalog(
        @Path("id") id: Int
    ): PlaybackCatalogResponse

    @POST("player")
    suspend fun resolvePlayback(
        @Body request: ResolvePlaybackRequest
    ): ResolvedPlaybackResponse
}
