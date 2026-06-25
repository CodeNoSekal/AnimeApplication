package com.dmitry.test.animeapplication.data

import com.dmitry.test.animeapplication.data.request.LoginRequest
import com.dmitry.test.animeapplication.data.request.RefreshRequest
import com.dmitry.test.animeapplication.data.request.RegisterRequest
import com.dmitry.test.animeapplication.data.request.VerifyRequest
import com.dmitry.test.animeapplication.data.response.AnimeResponse
import com.dmitry.test.animeapplication.data.response.AuthResponse
import com.dmitry.test.animeapplication.data.response.UserDTO
import com.dmitry.test.animeapplication.data.response.VerificationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


@Target(AnnotationTarget.FUNCTION)
annotation class NoAuth


interface TestApi {

    @GET("anime")
    suspend fun getAnimeCatalog(
        @Query("page")
        page: Int,
        @Query("per_page")
        perPage: Int = 50,
        @Query("sort")
        sort: String = "rating"
    ): AnimeResponse
}

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

interface VerificationApi{
    @POST("auth/verify/resend")
    suspend fun sendCode()

    @POST("auth/verify")
    suspend fun verify(
        @Body verifyRequest: VerifyRequest
    ): VerificationResponse
}

interface RefreshApi {
    @NoAuth
    @POST("auth/refresh")
    suspend fun refresh(
        @Body refreshRequest: RefreshRequest
    ): AuthResponse
}