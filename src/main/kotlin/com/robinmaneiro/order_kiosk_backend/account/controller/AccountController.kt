package com.robinmaneiro.order_kiosk_backend.account.controller

import com.robinmaneiro.order_kiosk_backend.account.service.AccountService
import com.robinmaneiro.order_kiosk_backend.account.service.model.AccountDetailsResponse
import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class AccountController(
    private val accountService: AccountService,
    private val authService: AuthService
) {
    @GetMapping("/me")
    fun getAccountDetails(): AccountDetailsResponse {
        return accountService.getAccountDetails()
    }
}
