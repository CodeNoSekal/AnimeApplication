package com.dmitry.yume.data.authorization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TokenCipherTest {

    @Test
    fun `decrypt reverses encrypt`() {
        val key = TokenCipher.generateKeyHex()
        val encrypted = TokenCipher.encrypt("access-token-value", key)

        assertEquals("access-token-value", TokenCipher.decrypt(encrypted, key))
    }

    @Test
    fun `same plaintext encrypts differently each time`() {
        val key = TokenCipher.generateKeyHex()

        val first = TokenCipher.encrypt("same-value", key)
        val second = TokenCipher.encrypt("same-value", key)

        assertNotEquals(first, second)
    }

    @Test
    fun `decrypt with wrong key fails`() {
        val encrypted = TokenCipher.encrypt("access-token-value", TokenCipher.generateKeyHex())
        val wrongKey = TokenCipher.generateKeyHex()

        assertThrows(Exception::class.java) {
            TokenCipher.decrypt(encrypted, wrongKey)
        }
    }
}
