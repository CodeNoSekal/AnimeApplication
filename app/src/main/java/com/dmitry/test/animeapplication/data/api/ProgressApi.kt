package com.dmitry.test.animeapplication.data.api

import com.dmitry.test.animeapplication.data.request.ProgressRequest
import com.dmitry.test.animeapplication.data.request.VerifyRequest
import com.dmitry.test.animeapplication.data.response.VerificationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface MeApi {
    @PUT("me/progress")
    suspend fun putProgress(
        @Body progressRequest: ProgressRequest
    )

    @GET("me/continue")
    suspend fun getProgress(): VerificationResponse
}