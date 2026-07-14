package com.dmitry.test.animeapplication.data.api

import com.dmitry.test.animeapplication.data.request.RefreshRequest
import com.dmitry.test.animeapplication.data.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {
    @NoAuth
    @POST("auth/refresh")
    suspend fun refresh(
        @Body refreshRequest: RefreshRequest
    ): AuthResponse
}