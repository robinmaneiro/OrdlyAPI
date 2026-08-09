package com.robinmaneiro.order_kiosk_backend.security.password

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HashEncoderTest {

    private val encoder = HashEncoder()

    @Test
    fun `encode produces a hash different from the raw password`() {
        val hash = encoder.encode("s3cret")

        assertNotEquals("s3cret", hash)
        assertTrue(hash.startsWith("\$2"), "expected a bcrypt hash prefix")
    }

    @Test
    fun `matches returns true for the correct password`() {
        val hash = encoder.encode("s3cret")

        assertTrue(encoder.matches("s3cret", hash))
    }

    @Test
    fun `matches returns false for an incorrect password`() {
        val hash = encoder.encode("s3cret")

        assertFalse(encoder.matches("wrong", hash))
    }

    @Test
    fun `the same password hashes differently each time due to salting`() {
        assertNotEquals(encoder.encode("same"), encoder.encode("same"))
    }
}
