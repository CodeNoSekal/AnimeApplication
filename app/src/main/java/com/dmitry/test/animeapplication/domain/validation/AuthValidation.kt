package com.dmitry.test.animeapplication.domain.validation

import androidx.core.text.isDigitsOnly

sealed interface FieldResult {
    data object Valid : FieldResult
    data class Invalid(val message: String) : FieldResult
}

object AuthValidation {
    private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+\$")

    fun email(value: String): FieldResult = when {
        value.isBlank() -> FieldResult.Invalid("Введите эл. почту")
        !EMAIL.matches(value) -> FieldResult.Invalid("Неверный формат почты")
        else -> FieldResult.Valid
    }

    fun password(value: String): FieldResult = when {
        value.isBlank() -> FieldResult.Invalid("Введите пароль")
        value.length < 8 -> FieldResult.Invalid("Минимум 8 символов")
        else -> FieldResult.Valid
    }

    fun displayName(value: String): FieldResult = when {
        value.isBlank() -> FieldResult.Invalid("Введите имя пользователя")
        else -> FieldResult.Valid
    }

    fun code(value: String): FieldResult = when {
        value.isBlank() -> FieldResult.Invalid("Введите код")
        (!value.isDigitsOnly() || value.length != 6) -> FieldResult.Invalid("Код неверный")
        else -> FieldResult.Valid
    }
}