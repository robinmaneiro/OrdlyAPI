package com.robinmaneiro.order_kiosk_backend.guest.controller

import com.robinmaneiro.order_kiosk_backend.auth.controller.AuthController.RefreshRequest
import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService
import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService.TokenPair
import com.robinmaneiro.order_kiosk_backend.guest.service.model.GuestDetailsResponse
import com.robinmaneiro.order_kiosk_backend.guest.service.GuestService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/guests")
class GuestController(
    private val guestService: GuestService
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
    fun getGuestDetails(): GuestDetailsResponse {
        return guestService.getGuestDetails()
    }
}
