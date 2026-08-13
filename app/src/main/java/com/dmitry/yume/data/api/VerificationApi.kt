package com.dmitry.yume.data.api

import com.dmitry.yume.data.request.VerifyRequest
import com.dmitry.yume.data.response.VerificationResponse
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