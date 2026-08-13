package com.dmitry.yume.domain.repository

sealed interface OperationResult {
    data object Success : OperationResult
    data class Error(val message: String) : OperationResult
}
