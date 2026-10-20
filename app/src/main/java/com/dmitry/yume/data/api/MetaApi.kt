package com.dmitry.yume.data.api

import com.dmitry.yume.data.response.MetaResponse
import com.dmitry.yume.data.response.HomeResponse
import retrofit2.http.GET

interface MetaApi {

    @GET("home")
    suspend fun getHome(): HomeResponse

    @GET("meta")
    suspend fun getMeta(): MetaResponse
}