package com.dmitry.test.animeapplication.data.api

import com.dmitry.test.animeapplication.data.request.VerifyRequest
import com.dmitry.test.animeapplication.data.response.VerificationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface VerificationApi{
    @POST("auth/verify/resend")
    suspend fun sendCode()

    @POST("auth/verify")
    suspend fun verify(
        @Body verifyRequest: VerifyRequest
    ): VerificationResponse
}