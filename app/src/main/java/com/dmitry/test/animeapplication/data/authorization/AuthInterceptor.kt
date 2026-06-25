package com.dmitry.test.animeapplication.data.authorization

import com.dmitry.test.animeapplication.data.NoAuth
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val skipAuth = request.tag(Invocation::class.java)
            ?.method()?.isAnnotationPresent(NoAuth::class.java) == true
        if (skipAuth) return chain.proceed(request)

        val token = runBlocking { tokenStorage.getAccessToken() }
        val authed = token?.let {
            request.newBuilder().header("Authorization", "Bearer $it").build()
        } ?: request
        return chain.proceed(authed)
    }

}