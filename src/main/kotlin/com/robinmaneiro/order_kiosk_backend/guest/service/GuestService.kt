package com.robinmaneiro.order_kiosk_backend.guest.service

import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService
import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService.TokenPair
import com.robinmaneiro.order_kiosk_backend.security.model.TokenClaims
import com.robinmaneiro.order_kiosk_backend.security.model.Variant
import com.robinmaneiro.order_kiosk_backend.security.token.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GuestService(
    private val jwtService: JwtService,
    ) {
    fun createGuestSession(): TokenPair {
        val guestUser = "guest-${UUID.randomUUID()}"
        val newAccessToken = jwtService.generateAccessToken(TokenClaims(guestUser, Variant.Guest.AccessToken))
        val newRefreshToken = jwtService.generateRefreshToken(TokenClaims(guestUser, Variant.Guest.RefreshToken) )

        // save user in the database
        // assign bag id and wishlist id

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    fun getGuestDetails(): GuestDetailsResponse {
        val guestDetailsResponse = GuestDetailsResponse( //TODO: Change random assignation for creation in the database and subsequent retrieval of IDs
            bagId = UUID.randomUUID().toString(),
            wishlistId = UUID.randomUUID().toString()
        )
        return guestDetailsResponse
    }
}
