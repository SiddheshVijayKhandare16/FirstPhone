package com.firstphone.app.util

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Salted PBKDF2-SHA256 hashing for the Parent PIN.
 * Both salt and hash are stored as hex-encoded strings in Room.
 */
object PinHasher {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SALT_BYTES = 16
    private const val ALGO = "PBKDF2WithHmacSHA256"

    fun newSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(pin: String, saltHex: String): String {
        val salt = saltHex.hexToBytes()
        val spec: KeySpec = PBEKeySpec(pin.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(ALGO)
        val raw = factory.generateSecret(spec).encoded
        return raw.toHex()
    }

    fun verify(pin: String, saltHex: String, expectedHashHex: String): Boolean {
        val computed = hash(pin, saltHex)
        // Constant-time compare
        if (computed.length != expectedHashHex.length) return false
        var result = 0
        for (i in computed.indices) result = result or (computed[i].code xor expectedHashHex[i].code)
        return result == 0
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
    private fun String.hexToBytes(): ByteArray {
        val out = ByteArray(length / 2)
        for (i in out.indices) {
            out[i] = ((Character.digit(this[i * 2], 16) shl 4) +
                Character.digit(this[i * 2 + 1], 16)).toByte()
        }
        return out
    }
}
