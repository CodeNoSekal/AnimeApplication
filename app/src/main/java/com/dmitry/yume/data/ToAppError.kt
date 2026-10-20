package com.dmitry.yume.data

import com.dmitry.yume.domain.models.AppError
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toAppError(): AppError = when (this) {
    is IOException -> AppError.NoConnection

    is HttpException -> when (code()) {
        404 -> AppError.NotFound
        in 500..599 -> AppError.Server
        else -> AppError.Unknown
    }

    else -> AppError.Unknown
}