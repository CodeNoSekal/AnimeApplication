package com.dmitry.yume.data.api

import com.dmitry.yume.data.request.RefreshRequest
import com.dmitry.yume.data.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {
    @NoAuth
    @POST("auth/refresh")
    suspend fun refresh(
        @Body refreshRequest: RefreshRequest
    ): AuthResponse
}