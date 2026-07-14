package com.dmitry.test.animeapplication.data.api

import com.dmitry.test.animeapplication.data.request.LoginRequest
import com.dmitry.test.animeapplication.data.request.RegisterRequest
import com.dmitry.test.animeapplication.data.response.AuthResponse
import com.dmitry.test.animeapplication.data.response.UserDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @NoAuth
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): AuthResponse

    @NoAuth
    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): AuthResponse

    @POST("auth/logout")
    suspend fun logout()

    @GET("auth/me")
    suspend fun getMe(): UserDTO
}