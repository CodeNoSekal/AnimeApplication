package com.dmitry.test.animeapplication

import com.dmitry.test.animeapplication.domain.validation.AuthValidation
import com.dmitry.test.animeapplication.domain.validation.FieldResult
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun `пустая почта невалидна`() {
        assertTrue(AuthValidation.email("") is FieldResult.Invalid)
    }
}