package com.robinmaneiro.order_kiosk_backend.auth.controller

import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService
import com.robinmaneiro.order_kiosk_backend.auth.service.model.AccountDetailsResponse
import com.robinmaneiro.order_kiosk_backend.auth.service.model.RegistrationResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    data class AuthRequest(
        @field:Email(message = "Invalid email field.")
        val email: String,
        @field:Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
            message = "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        )
        val password: String
    )

    data class RegistrationRequestBody(
        @field:NotBlank val title: String,
        @field:NotBlank val firstName: String,
        @field:NotBlank val lastName: String,
        @field:Email(message = "Invalid email field.")
        val email: String,
        @field:Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
            message = "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        )
        val password: String
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registrationBody: RegistrationRequestBody
    ): RegistrationResponse {
        return authService.register(registrationBody)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: AuthRequest
    ): AuthService.TokenPair {
        return authService.login(body.email, body.password)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody refreshBody: RefreshRequest
    ): AuthService.TokenPair {
        return authService.refresh(refreshBody.refreshToken)
    }
}