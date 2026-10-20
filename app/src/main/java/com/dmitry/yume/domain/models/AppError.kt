package com.dmitry.yume.domain.models

sealed interface AppError {
    data object NoConnection : AppError
    data object NotFound : AppError
    data object Server : AppError
    data object Unknown : AppError
}