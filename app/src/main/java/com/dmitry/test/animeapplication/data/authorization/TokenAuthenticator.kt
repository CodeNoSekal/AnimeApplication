package com.dmitry.test.animeapplication.data.authorization

import com.dmitry.test.animeapplication.data.AuthApi
import com.dmitry.test.animeapplication.data.RefreshApi
import com.dmitry.test.animeapplication.data.request.RefreshRequest
import javax.inject.Provider
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val refreshApi: Provider<RefreshApi>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2)
            return null

        val refresh = runBlocking {
            tokenStorage.getRefreshToken()
        } ?: return null

        val newTokens = runBlocking {
            runCatching {
                refreshApi.get().refresh(RefreshRequest(refresh))
            }.getOrNull()
        } ?: return null

        runBlocking {
            tokenStorage.saveAccessToken(newTokens.accessToken)
            tokenStorage.saveRefreshToken(newTokens.refreshToken)
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.accessToken}")
            .build()
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