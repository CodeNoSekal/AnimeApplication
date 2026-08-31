package com.dmitry.yume.data.api

import com.dmitry.yume.data.request.LoginRequest
import com.dmitry.yume.data.request.ChangeEmailRequest
import com.dmitry.yume.data.request.ChangePasswordRequest
import com.dmitry.yume.data.request.ProfileRequest
import com.dmitry.yume.data.request.RegisterRequest
import com.dmitry.yume.data.response.AuthResponse
import com.dmitry.yume.data.response.AvatarResponse
import com.dmitry.yume.data.response.EmailChangeResponse
import com.dmitry.yume.data.response.UsernameAvailabilityResponse
import com.dmitry.yume.data.response.UsernameSuggestionResponse
import com.dmitry.yume.data.response.UserDTO
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.Query

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

    @POST("auth/logout-all")
    suspend fun logoutAll()

    @GET("auth/me")
    suspend fun getMe(): UserDTO

    @PATCH("auth/me")
    suspend fun updateProfile(@Body request: ProfileRequest): UserDTO

    @NoAuth
    @GET("auth/username-available")
    suspend fun usernameAvailable(
        @Query("username") username: String
    ): UsernameAvailabilityResponse

    @NoAuth
    @GET("auth/username-suggest")
    suspend fun suggestUsername(
        @Query("display_name") displayName: String
    ): UsernameSuggestionResponse

    @POST("auth/password/change")
    suspend fun changePassword(@Body request: ChangePasswordRequest)

    @POST("auth/email/change")
    suspend fun changeEmail(@Body request: ChangeEmailRequest): EmailChangeResponse

    @Multipart
    @POST("me/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): AvatarResponse

    @DELETE("me/avatar")
    suspend fun removeAvatar()
}
