package com.dmitry.yume.data.authorization

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Шифрование токенов ключом, который хранится рядом с шифротекстом в том же
 * DataStore-файле и переносится вместе с ним (перенос на новое устройство,
 * резервное копирование). Это осознанный компромисс: схема защищает от
 * попадания токена в чужие руки при изолированном чтении отдельного значения,
 * но не от копирования файла целиком вместе с ключом — для этого понадобился
 * бы аппаратный ключ (Android Keystore), который принципиально не переносится
 * на другое устройство.
 */
internal object TokenCipher {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_ALGORITHM = "AES"
    private const val KEY_SIZE_BITS = 256
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val IV_LENGTH_BYTES = 12

    fun generateKeyHex(): String =
        KeyGenerator.getInstance(KEY_ALGORITHM)
            .apply { init(KEY_SIZE_BITS) }
            .generateKey()
            .encoded
            .toHex()

    fun encrypt(plainText: String, keyHex: String): String {
        val iv = ByteArray(IV_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, secretKey(keyHex), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        }
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return (iv + encrypted).toHex()
    }

    fun decrypt(payloadHex: String, keyHex: String): String {
        val bytes = payloadHex.hexToByteArray()
        val iv = bytes.copyOfRange(0, IV_LENGTH_BYTES)
        val encrypted = bytes.copyOfRange(IV_LENGTH_BYTES, bytes.size)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, secretKey(keyHex), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        }
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    private fun secretKey(keyHex: String): SecretKey =
        SecretKeySpec(keyHex.hexToByteArray(), KEY_ALGORITHM)

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.hexToByteArray(): ByteArray = ByteArray(length / 2) { i ->
        ((Character.digit(this[i * 2], 16) shl 4) + Character.digit(this[i * 2 + 1], 16)).toByte()
    }
}
