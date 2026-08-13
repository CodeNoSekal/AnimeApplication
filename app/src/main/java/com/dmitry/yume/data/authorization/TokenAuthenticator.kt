package com.dmitry.yume.data.authorization

import com.dmitry.yume.data.api.RefreshApi
import com.dmitry.yume.data.api.NoAuth
import com.dmitry.yume.data.request.RefreshRequest
import javax.inject.Provider
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import retrofit2.Invocation
import java.io.IOException
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val sessionManager: AuthSessionManager,
    private val refreshApi: Provider<RefreshApi>
) : Authenticator {

    private val refreshLock = Any()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        val skipAuth = response.request.tag(Invocation::class.java)
            ?.method()?.isAnnotationPresent(NoAuth::class.java) == true
        if (skipAuth) return null

        val failedAuthorization =
            response.request.header("Authorization")

        return synchronized(refreshLock) {
            runBlocking {
                val currentTokens = sessionManager.currentTokens()
                val currentAuthorization = currentTokens?.bearerAuthorization()

                if (responseCount(response) >= 2) {
                    if (currentAuthorization != null && currentAuthorization != failedAuthorization) {
                        return@runBlocking response.request.withAuthorization(currentAuthorization)
                    }

                    currentTokens?.let {
                        sessionManager.invalidateIfCurrent(it.refreshToken)
                    }
                    return@runBlocking null
                }

                if (currentAuthorization != null && currentAuthorization != failedAuthorization) {
                    return@runBlocking response.request.withAuthorization(currentAuthorization)
                }

                val tokens = currentTokens ?: run {
                    sessionManager.invalidate()
                    return@runBlocking null
                }

                val newTokens = try {
                    refreshApi.get().refresh(
                        RefreshRequest(tokens.refreshToken)
                    )
                } catch (e: HttpException) {
                    if (e.code() == 401 || e.code() == 403) {
                        val invalidated = sessionManager.invalidateIfCurrent(tokens.refreshToken)
                        if (!invalidated) {
                            return@runBlocking retryWithLatestToken(response.request, failedAuthorization)
                        }
                    }

                    return@runBlocking null
                } catch (e: IOException) {
                    return@runBlocking null
                } catch (_: Exception) {
                    return@runBlocking null
                }

                val refreshedTokens = StoredTokens(
                    accessToken = newTokens.accessToken,
                    refreshToken = newTokens.refreshToken
                )
                val replaced = sessionManager.replaceTokensIfCurrent(
                    expectedRefreshToken = tokens.refreshToken,
                    newTokens = refreshedTokens
                )

                if (replaced) {
                    response.request.withAuthorization(refreshedTokens.bearerAuthorization())
                } else {
                    retryWithLatestToken(response.request, failedAuthorization)
                }
            }
        }
    }

    private fun retryWithLatestToken(
        request: Request,
        failedAuthorization: String?
    ): Request? {
        val latestAuthorization = sessionManager.currentTokens()?.bearerAuthorization()
        return if (latestAuthorization != null && latestAuthorization != failedAuthorization) {
            request.withAuthorization(latestAuthorization)
        } else {
            null
        }
    }

    private fun StoredTokens.bearerAuthorization(): String = "Bearer $accessToken"

    private fun Request.withAuthorization(authorization: String): Request =
        newBuilder()
            .header("Authorization", authorization)
            .build()

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
