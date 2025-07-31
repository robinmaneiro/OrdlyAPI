package com.robinmaneiro.order_kiosk_backend.controller

import com.robinmaneiro.order_kiosk_backend.security.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/auth"])
class AuthController(
    private val authService: AuthService
) {
    data class AuthRequest(
        val email: String,
        val password: String
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    @PostMapping(value = ["/register"])
    fun register(
        @RequestBody body: AuthRequest
    ) {
        authService.register(body.email, body.password)
    }

    @PostMapping(value = ["/login"])
    fun login(
        @RequestBody body: AuthRequest
    ): AuthService.TokenPair {
         return authService.login(body.email, body.password)
    }

    @PostMapping(value = ["/refresh"])
    fun refresh(
        @RequestBody refreshBody: RefreshRequest
    ): AuthService.TokenPair {
        return authService.refresh(refreshBody.refreshToken)
    }
}
