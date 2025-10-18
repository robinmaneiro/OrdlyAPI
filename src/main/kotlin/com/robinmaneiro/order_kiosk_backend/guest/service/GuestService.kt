package com.robinmaneiro.order_kiosk_backend.guest.service

import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService
import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService.TokenPair
import com.robinmaneiro.order_kiosk_backend.security.token.JwtService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GuestService(
    private val jwtService: JwtService,
    ) {
    fun createGuestSession(): AuthService.TokenPair {
        val guestUser = "guest-${UUID.randomUUID()}"
        val newAccessToken = jwtService.generateAuthAccessToken(guestUser)
        val newRefreshToken = jwtService.generateAuthRefreshToken(guestUser)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }
}
