package com.dmitry.yume.data.authorization

import com.dmitry.yume.data.api.RefreshApi
import com.dmitry.yume.data.request.RefreshRequest
import javax.inject.Provider
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val refreshApi: Provider<RefreshApi>
) : Authenticator {

    private val refreshLock = Any()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        if (responseCount(response) >= 2)
            return null

        val failedAuthorization =
            response.request.header("Authorization")

        return synchronized(refreshLock) {
            runBlocking {
                val currentAccessToken =
                    tokenStorage.getAccessToken()

                val currentAuthorization =
                    currentAccessToken?.let { "Bearer $it" }

                if (
                    currentAuthorization != null &&
                    currentAuthorization != failedAuthorization
                ) {
                    return@runBlocking response.request
                        .newBuilder()
                        .header("Authorization", currentAuthorization)
                        .build()
                }

                val refreshToken =
                    tokenStorage.getRefreshToken()
                        ?: return@runBlocking null

                val newTokens = try {
                    refreshApi.get().refresh(
                        RefreshRequest(refreshToken)
                    )
                } catch (e: HttpException) {
                    if (e.code() == 401 || e.code() == 403) {
                        tokenStorage.clear()
                    }

                    return@runBlocking null
                } catch (e: IOException) {
                    return@runBlocking null
                }

                val latestRefreshToken = tokenStorage.getRefreshToken()

                if (latestRefreshToken != refreshToken) {
                    return@runBlocking null
                }

                tokenStorage.saveTokens(
                    accessToken = newTokens.accessToken,
                    refreshToken = newTokens.refreshToken
                )

                response.request
                    .newBuilder()
                    .header(
                        "Authorization",
                        "Bearer ${newTokens.accessToken}"
                    )
                    .build()
            }
        }
    }


    private fun responseCount(r: Response): Int {
        var n = 1
        var p = r.priorResponse
        while (p != null) {
            n++
            p = p.priorResponse
        }
        return n
    }
}