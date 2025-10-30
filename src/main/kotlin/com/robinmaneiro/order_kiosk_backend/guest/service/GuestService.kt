package com.robinmaneiro.order_kiosk_backend.guest.service

import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService.TokenPair
import com.robinmaneiro.order_kiosk_backend.database.model.GuestSession
import com.robinmaneiro.order_kiosk_backend.database.repository.GuestSessionRepository
import com.robinmaneiro.order_kiosk_backend.guest.service.model.GuestDetailsResponse
import com.robinmaneiro.order_kiosk_backend.security.model.TokenClaims
import com.robinmaneiro.order_kiosk_backend.security.model.Variant
import com.robinmaneiro.order_kiosk_backend.security.token.JwtService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class GuestService(
    private val jwtService: JwtService,
    private val guestSessionRepository: GuestSessionRepository
) {
    fun createGuestSession(): TokenPair {
        val guestUser = ObjectId.get()
        val accessToken =
            jwtService.generateAccessToken(TokenClaims("guest-${guestUser.toHexString()}", Variant.Guest.AccessToken))
        val refreshToken =
            jwtService.generateRefreshToken(TokenClaims("guest-${guestUser.toHexString()}", Variant.Guest.RefreshToken))

        // save user in the database
        // assign bag id and wishlist id

        val guestSession = GuestSession(
            id = guestUser,
            bagId = ObjectId.get(),
            wishlistId = ObjectId.get()
        )

        guestSessionRepository.save(guestSession)

        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw ResponseStatusException(
                /* status = */ HttpStatus.UNAUTHORIZED,
                /* reason = */ "Invalid refresh token."
            )
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
//        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
//            throw ResponseStatusException(
//                HttpStatus.UNAUTHORIZED,
//                "Invalid refresh token."
//            )
//        }
//
//        val hashed = hashToken(refreshToken)
//        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
//            ?: throw ResponseStatusException(
//                HttpStatus.UNAUTHORIZED,
//                "Refresh token not recognized (maybe used or expired?)."
//            )
//
//        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)
        val newAccessToken = jwtService.generateAccessToken(TokenClaims(userId, Variant.Guest.AccessToken))
        val newRefreshToken = jwtService.generateRefreshToken(TokenClaims(userId, Variant.Guest.RefreshToken))
//
//        storeRefreshToken(user.id, newRefreshToken)
        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    fun getGuestDetails(guestSessionId: String): GuestDetailsResponse {
        val guestSession = guestSessionRepository.findById(ObjectId(guestSessionId)).getOrElse {
            // TODO: Throw exception here
            throw Exception()
        }

        return GuestDetailsResponse( //TODO: Change random assignation for creation in the database and subsequent retrieval of IDs
            guestBagId = guestSession.bagId.toHexString(),
            guestWishlistId = guestSession.wishlistId.toHexString()
        )
    }
}
