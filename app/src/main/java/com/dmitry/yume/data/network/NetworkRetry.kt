package com.dmitry.yume.data.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.time.Duration.Companion.milliseconds

suspend fun <T : Any> withNetworkRetry(
    attepts: Int = 3,
    attemptTimeoutMs: Long = 3_000,
    block: suspend () -> T
): T {
    var pause = 300L
    repeat(attepts - 1) {
        try {
            withTimeoutOrNull(attemptTimeoutMs.milliseconds) { block() }
                ?.let { return it }
        } catch (_: IOException) {

        }
        delay(pause.milliseconds)
        pause *= 2
    }

    return withTimeoutOrNull(attemptTimeoutMs.milliseconds) { block() }
        ?: throw SocketTimeoutException("Нет ответа после $attepts попыток")
}