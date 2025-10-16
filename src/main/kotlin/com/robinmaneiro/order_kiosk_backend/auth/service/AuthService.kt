package com.robinmaneiro.order_kiosk_backend.auth.service

import com.robinmaneiro.order_kiosk_backend.auth.controller.AuthController
import com.robinmaneiro.order_kiosk_backend.auth.service.model.RegistrationResponse
import com.robinmaneiro.order_kiosk_backend.database.model.RefreshToken
import com.robinmaneiro.order_kiosk_backend.database.model.User
import com.robinmaneiro.order_kiosk_backend.database.repository.RefreshTokenRepository
import com.robinmaneiro.order_kiosk_backend.database.repository.UserRepository
import com.robinmaneiro.order_kiosk_backend.security.password.HashEncoder
import com.robinmaneiro.order_kiosk_backend.security.token.JwtService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String,
    )

    fun register(registrationBody: AuthController.RegistrationRequestBody): RegistrationResponse {
        val user = userRepository.findByEmail(registrationBody.email.trim())
        if (user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists")
        }
        val savedUser =  userRepository.save(
            User(
                title = registrationBody.title,
                firstName = registrationBody.firstName,
                lastName = registrationBody.lastName,
                email = registrationBody.email,
                hashedPassword = hashEncoder.encode(registrationBody.password),
            )
        )

        return RegistrationResponse(
            savedUser.id.toHexString()
        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email)
            ?: throw BadCredentialsException("Invalid credentials.")

        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials.")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
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
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid refresh token."
            )
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Refresh token not recognized (maybe used or expired?)."
            )

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)
        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)
        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}