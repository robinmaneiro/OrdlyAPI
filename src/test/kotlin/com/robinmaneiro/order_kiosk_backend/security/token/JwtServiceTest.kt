package com.robinmaneiro.order_kiosk_backend.security.token

import com.robinmaneiro.order_kiosk_backend.security.model.TokenClaims
import com.robinmaneiro.order_kiosk_backend.security.model.Variant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException
import java.util.Base64

class JwtServiceTest {

    // The service expects a base64-encoded secret that decodes to >= 32 bytes (HS256).
    private val secret = base64Secret("0123456789012345678901234567890123456789")
    private val jwtService = JwtService(secret)

    private val userAccess = TokenClaims(userId = "user-1", variant = Variant.User.AccessToken)
    private val userRefresh = TokenClaims(userId = "user-1", variant = Variant.User.RefreshToken)
    private val guestAccess = TokenClaims(userId = "guest-1", variant = Variant.Guest.AccessToken)

    @Test
    fun `generated access token carries the subject`() {
        val token = jwtService.generateAccessToken(userAccess)

        assertEquals("user-1", jwtService.getUserIdFromToken(token))
    }

    @Test
    fun `getUserIdFromToken tolerates the Bearer prefix`() {
        val token = jwtService.generateAccessToken(userAccess)

        assertEquals("user-1", jwtService.getUserIdFromToken("Bearer $token"))
    }

    @Test
    fun `role claim is derived from the token variant`() {
        assertEquals("USER", jwtService.getRoleFromClaim(jwtService.generateAccessToken(userAccess)))
        assertEquals("GUEST", jwtService.getRoleFromClaim(jwtService.generateAccessToken(guestAccess)))
    }

    @Test
    fun `a valid access token is accepted by validateAccessToken`() {
        assertTrue(jwtService.validateAccessToken(jwtService.generateAccessToken(userAccess)))
    }

    @Test
    fun `a refresh token is not accepted as an access token`() {
        assertFalse(jwtService.validateAccessToken(jwtService.generateRefreshToken(userRefresh)))
    }

    @Test
    fun `a valid refresh token is accepted by validateRefreshToken`() {
        assertTrue(jwtService.validateRefreshToken(jwtService.generateRefreshToken(userRefresh)))
    }

    @Test
    fun `an access token is not accepted as a refresh token`() {
        assertFalse(jwtService.validateRefreshToken(jwtService.generateAccessToken(userAccess)))
    }

    @Test
    fun `a malformed token fails validation instead of throwing`() {
        assertFalse(jwtService.validateAccessToken("not-a-real-token"))
    }

    @Test
    fun `getUserIdFromToken throws 401 for a malformed token`() {
        val ex = assertThrows<ResponseStatusException> { jwtService.getUserIdFromToken("garbage") }
        assertEquals(401, ex.statusCode.value())
    }

    @Test
    fun `a token signed with a different secret is rejected`() {
        val foreignToken = JwtService(base64Secret("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN"))
            .generateAccessToken(userAccess)

        assertFalse(jwtService.validateAccessToken(foreignToken))
    }

    private fun base64Secret(raw: String): String =
        Base64.getEncoder().encodeToString(raw.toByteArray())
}
