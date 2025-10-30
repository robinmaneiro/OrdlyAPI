package com.robinmaneiro.order_kiosk_backend.account.controller

import com.robinmaneiro.order_kiosk_backend.account.service.AccountService
import com.robinmaneiro.order_kiosk_backend.account.service.model.AccountDetailsResponse
import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService
import com.robinmaneiro.order_kiosk_backend.security.token.JwtService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class AccountController(
    private val accountService: AccountService,
    private val jwtService: JwtService
) {
    @GetMapping("/me")
    fun getAccountDetails(
        @RequestHeader("Authorization") authorizationHeader: String
    ): AccountDetailsResponse {

        // TODO: Add validation after this
        val userId = jwtService.getUserIdFromToken(authorizationHeader.removePrefix("Bearer "))



        return accountService.getAccountDetails(userId)
    }
}
