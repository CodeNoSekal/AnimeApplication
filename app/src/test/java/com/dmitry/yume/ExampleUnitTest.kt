package com.dmitry.yume

import com.dmitry.yume.domain.validation.AuthValidation
import com.dmitry.yume.domain.validation.FieldResult
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