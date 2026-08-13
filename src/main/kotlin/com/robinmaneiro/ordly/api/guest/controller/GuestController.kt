package com.robinmaneiro.ordly.api.guest.controller

import com.robinmaneiro.ordly.api.auth.controller.AuthController.RefreshRequest
import com.robinmaneiro.ordly.api.auth.service.AuthService
import com.robinmaneiro.ordly.api.auth.service.AuthService.TokenPair
import com.robinmaneiro.ordly.api.guest.service.model.GuestDetailsResponse
import com.robinmaneiro.ordly.api.guest.service.GuestService
import com.robinmaneiro.ordly.api.security.token.JwtService
import org.apache.tomcat.util.http.parser.Authorization
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/guests")
class GuestController(
    private val guestService: GuestService,
    private val jwtService: JwtService
) {
    @GetMapping("/create")
    fun createGuestSession(): TokenPair {
        return guestService.createGuestSession()
    }

    @PostMapping("/refresh")
    fun refreshGuestSession(
        @RequestBody refreshBody: RefreshRequest
    ): TokenPair {
        return guestService.refresh(refreshBody.refreshToken)
    }

    @GetMapping("/me")
    fun getGuestDetails(
        @RequestHeader("Authorization") authorization: String
    ): GuestDetailsResponse {

        val guestToken = authorization.removePrefix("Bearer ")
        val guestSessionId = jwtService.getUserIdFromToken(guestToken).removePrefix("guest-")
        return guestService.getGuestDetails(guestSessionId)
    }
}
