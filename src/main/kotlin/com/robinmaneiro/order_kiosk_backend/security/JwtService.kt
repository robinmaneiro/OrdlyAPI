package com.robinmaneiro.order_kiosk_backend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Base64
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String
) {
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 15L * 60 * 1000L // 15 minutes
    val refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1000L // 30 days

    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String): String {
        return generateToken(userId, "access", accessTokenValidityMs)
    }

    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, "refresh", refreshTokenValidityMs)
    }

    fun validateAccessToken(accessToken: String): Boolean {
        val claims = parseAllClaims(accessToken) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return !claims.hasTokenExpired() && tokenType == "access"
    }

    fun validateRefreshToken(refreshToken: String): Boolean {
        val claims = parseAllClaims(refreshToken) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return !claims.hasTokenExpired() && tokenType == "refresh"
    }

    private fun Claims.hasTokenExpired(): Boolean {
        val expirationDate = (this["exp"] as? Long)?.let { Instant.ofEpochSecond(it) } ?: return true
        val allowedClockSkewSeconds = 30L
        return Instant.now().isAfter(expirationDate.plusSeconds(allowedClockSkewSeconds))
    }

    fun getUserIdFromToken(token: String): String {
        val claims = parseAllClaims(token)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid token.")
        return claims.subject
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = token.removePrefix("Bearer ")

        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (_: Exception) {
            null
        }
    }
}
